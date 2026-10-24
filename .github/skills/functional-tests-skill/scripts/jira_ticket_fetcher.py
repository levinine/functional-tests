#!/usr/bin/env python3
"""
Jira & GitHub Ticket Fetcher — Extracts ticket details to help generate test scenarios.

Connects to a Jira instance or GitHub via REST API, fetches ticket details, and
outputs structured information about the feature, what needs to be done, and how
it should work.

Usage:
    # Fetch a single Jira ticket
    python3 jira_ticket_fetcher.py fetch PROJ-123

    # Fetch a Jira ticket with all linked/child issues
    python3 jira_ticket_fetcher.py fetch PROJ-123 --include-children

    # Fetch and output as markdown (default)
    python3 jira_ticket_fetcher.py fetch PROJ-123 --format markdown

    # Fetch and output as JSON
    python3 jira_ticket_fetcher.py fetch PROJ-123 --format json

    # Fetch and generate a Gherkin skeleton from acceptance criteria
    python3 jira_ticket_fetcher.py generate-gherkin PROJ-123

    # GitHub Issues — fetch an issue
    python3 jira_ticket_fetcher.py fetch owner/repo#42
    python3 jira_ticket_fetcher.py fetch owner/repo#42 --format json

    # GitHub Issues — generate Gherkin skeleton
    python3 jira_ticket_fetcher.py generate-gherkin owner/repo#42

Environment Variables (set before running, or use a .env file — see .env.example):

    Jira:
      JIRA_BASE_URL   — Base URL (e.g., https://yourcompany.atlassian.net)
      JIRA_USER_EMAIL — Email for Jira Cloud authentication
      JIRA_API_TOKEN  — API token (https://id.atlassian.com/manage-profile/security/api-tokens)
      JIRA_PAT        — Personal Access Token (alternative to email+token, for on-prem)

    GitHub:
      GITHUB_TOKEN    — Personal access token (https://github.com/settings/tokens)
"""

from __future__ import annotations

import argparse
import json
import os
import re
import sys
import textwrap
from base64 import b64encode
from pathlib import Path
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

# ---------------------------------------------------------------------------
# Constants
# ---------------------------------------------------------------------------
_GITHUB_API_BASE = "https://api.github.com"
_GITHUB_API_VERSION = "2022-11-28"
_JIRA_API_VERSION = "3"
_MAX_GITHUB_COMMENTS = 10
_MAX_AC_TITLE_LENGTH = 120
_MIN_AC_TITLE_LENGTH = 10
_MAX_ERROR_BODY_LENGTH = 500
_GHERKIN_WRAP_WIDTH = 100

# Acceptance criteria custom field IDs (common Jira configurations)
_AC_CUSTOM_FIELDS = ("customfield_10400", "customfield_10300", "customfield_10206")

# Pre-compiled regex patterns
_GITHUB_REF_PATTERN = re.compile(r"^([\w.-]+)/([\w.-]+)#(\d+)$")
_AC_HEADING_PATTERN = re.compile(
    r"(?:^|\n)#{1,4}\s*(?:acceptance\s*criteria|AC)\s*\n(.*?)(?=\n#{1,4}\s|\Z)",
    re.IGNORECASE | re.DOTALL,
)
_TASK_LIST_PATTERN = re.compile(r"- \[([ xX])\]\s+(.*)")
_AC_ITEMS_PATTERN = re.compile(
    r"(?:^|\n)\s*(?:\d+[.)]\s*|[-*•]\s*)(.*?)(?=\n\s*(?:\d+[.)]\s*|[-*•]\s*)|\Z)",
    re.DOTALL,
)


# ---------------------------------------------------------------------------
# .env file loader
# ---------------------------------------------------------------------------

def _load_env_file() -> None:
    """Load variables from a .env file if present.

    Searches for .env in this order:
      1. Next to this script  (.github/skills/functional-tests-skill/scripts/.env)
      2. In the project root   (functional-tests/.env)

    Lines like KEY=VALUE or KEY="VALUE" are parsed. Comments (#) and blank
    lines are ignored. Already-set env vars are NOT overwritten.
    """
    script_dir = Path(__file__).resolve().parent
    project_root = script_dir.parents[3]

    for env_path in (script_dir / ".env", project_root / ".env"):
        if env_path.is_file():
            _parse_env_file(env_path)
            break


def _parse_env_file(env_path: Path) -> None:
    """Parse a single .env file and set missing environment variables."""
    with open(env_path, encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith("#") or "=" not in line:
                continue
            key, _, value = line.partition("=")
            key = key.strip()
            value = value.strip().strip("\"'")
            if key and key not in os.environ:
                os.environ[key] = value


# ---------------------------------------------------------------------------
# Error handling
# ---------------------------------------------------------------------------

def _exit_with_error(message: str) -> None:
    """Print an error message to stderr and exit."""
    print(f"ERROR: {message}", file=sys.stderr)
    sys.exit(1)


def _handle_http_error(error: HTTPError, url: str) -> None:
    """Handle an HTTP error by printing details and exiting."""
    body = error.read().decode() if error.fp else ""
    messages = [f"HTTP {error.code} from {url}"]

    if error.code == 401:
        messages.append("  Authentication failed. Check your credentials.")
    elif error.code == 404:
        messages.append("  Resource not found. Check the ticket key or repository.")

    if body:
        try:
            parsed_msg = json.loads(body).get("message", body[:_MAX_ERROR_BODY_LENGTH])
        except (json.JSONDecodeError, AttributeError):
            parsed_msg = body[:_MAX_ERROR_BODY_LENGTH]
        messages.append(f"  {parsed_msg}")

    _exit_with_error("\n".join(messages))


# ---------------------------------------------------------------------------
# HTTP helpers
# ---------------------------------------------------------------------------

def _http_get(url: str, headers: dict[str, str]) -> dict:
    """Perform an HTTP GET request and return parsed JSON."""
    req = Request(url, headers=headers, method="GET")
    try:
        with urlopen(req) as resp:
            return json.loads(resp.read().decode())
    except HTTPError as e:
        _handle_http_error(e, url)
    except URLError as e:
        _exit_with_error(f"Cannot reach {url} — {e.reason}")
    return {}  # unreachable, satisfies type checker


# ---------------------------------------------------------------------------
# Authentication helpers
# ---------------------------------------------------------------------------

def _get_jira_auth_header() -> str | None:
    """Build the Authorization header for Jira based on available env vars."""
    pat = os.environ.get("JIRA_PAT")
    if pat:
        return f"Bearer {pat}"

    email = os.environ.get("JIRA_USER_EMAIL")
    token = os.environ.get("JIRA_API_TOKEN")
    if email and token:
        creds = b64encode(f"{email}:{token}".encode()).decode()
        return f"Basic {creds}"

    return None


def _get_jira_base_url() -> str:
    """Return the Jira base URL from environment, or exit with an error."""
    url = os.environ.get("JIRA_BASE_URL", "").rstrip("/")
    if not url:
        _exit_with_error(
            "JIRA_BASE_URL environment variable is not set.\n"
            "  Set it to your Jira instance URL, e.g.:\n"
            "  export JIRA_BASE_URL=https://yourcompany.atlassian.net"
        )
    return url


# ---------------------------------------------------------------------------
# Jira API interaction
# ---------------------------------------------------------------------------

def _jira_api_get(path: str) -> dict:
    """Perform a GET request to the Jira REST API."""
    base_url = _get_jira_base_url()
    url = f"{base_url}/rest/api/{_JIRA_API_VERSION}/{path.lstrip('/')}"
    headers = {"Accept": "application/json", "Content-Type": "application/json"}
    auth = _get_jira_auth_header()
    if auth:
        headers["Authorization"] = auth
    return _http_get(url, headers)


# ---------------------------------------------------------------------------
# GitHub API interaction
# ---------------------------------------------------------------------------

def _github_api_get(path: str) -> dict:
    """Perform a GET request to the GitHub REST API."""
    url = f"{_GITHUB_API_BASE}/{path.lstrip('/')}"
    headers = {
        "Accept": "application/vnd.github+json",
        "X-GitHub-Api-Version": _GITHUB_API_VERSION,
    }
    token = os.environ.get("GITHUB_TOKEN")
    if token:
        headers["Authorization"] = f"Bearer {token}"
    return _http_get(url, headers)


# ---------------------------------------------------------------------------
# Utility
# ---------------------------------------------------------------------------

def _nested_get(data: dict | None, *keys: str) -> str | None:
    """Safely traverse nested dicts, returning None if any key is missing."""
    current: dict | None = data
    for key in keys:
        if not isinstance(current, dict):
            return None
        current = current.get(key)
    return current


# ---------------------------------------------------------------------------
# Jira ticket parsing
# ---------------------------------------------------------------------------

def _extract_jira_ticket(data: dict) -> dict:
    """Extract the useful fields from a Jira issue JSON response."""
    fields = data.get("fields", {})
    return {
        "key": data.get("key"),
        "summary": fields.get("summary"),
        "type": _nested_get(fields, "issuetype", "name"),
        "status": _nested_get(fields, "status", "name"),
        "priority": _nested_get(fields, "priority", "name"),
        "assignee": _nested_get(fields, "assignee", "displayName"),
        "reporter": _nested_get(fields, "reporter", "displayName"),
        "description": fields.get("description") or "",
        "acceptance_criteria": _find_acceptance_criteria(fields),
        "labels": fields.get("labels", []),
        "components": [c.get("name") for c in fields.get("components", [])],
        "fix_versions": [v.get("name") for v in fields.get("fixVersions", [])],
        "subtasks": _extract_jira_subtasks(fields),
        "linked_issues": _extract_jira_links(fields),
    }


def _find_acceptance_criteria(fields: dict) -> str:
    """Find acceptance criteria from known Jira custom fields."""
    for field_id in _AC_CUSTOM_FIELDS:
        value = fields.get(field_id)
        if value:
            return value
    return ""


def _extract_jira_subtasks(fields: dict) -> list[dict]:
    """Extract subtask info from Jira fields."""
    return [
        {
            "key": sub.get("key"),
            "summary": _nested_get(sub, "fields", "summary"),
            "status": _nested_get(sub, "fields", "status", "name"),
        }
        for sub in fields.get("subtasks", [])
    ]


def _extract_jira_links(fields: dict) -> list[dict]:
    """Extract linked issue info from Jira fields."""
    links = []
    for link in fields.get("issuelinks", []):
        linked = link.get("outwardIssue") or link.get("inwardIssue")
        if linked:
            links.append({
                "key": linked.get("key"),
                "summary": _nested_get(linked, "fields", "summary"),
                "relationship": (
                    _nested_get(link, "type", "outward")
                    or _nested_get(link, "type", "name")
                ),
            })
    return links


# ---------------------------------------------------------------------------
# Jira markup conversion
# ---------------------------------------------------------------------------

def _strip_jira_markup(text: str | dict) -> str:
    """Convert Jira wiki markup or ADF to plain text."""
    if not text:
        return ""
    if isinstance(text, dict):
        return _adf_to_text(text)
    text = re.sub(r"\{noformat\}.*?\{noformat\}", "", text, flags=re.DOTALL)
    text = re.sub(r"\{code[^}]*\}.*?\{code\}", "", text, flags=re.DOTALL)
    text = re.sub(r"\[([^|]*)\|[^\]]*\]", r"\1", text)
    text = re.sub(r"[{*_~^+]", "", text)
    text = re.sub(r"h[1-6]\.\s*", "", text)
    text = re.sub(r"^[#\-*]+\s*", "", text, flags=re.MULTILINE)
    return text.strip()


def _adf_to_text(node: dict) -> str:
    """Recursively extract text from Atlassian Document Format."""
    if node.get("type") == "text":
        return node.get("text", "")
    return "\n".join(_adf_to_text(child) for child in node.get("content", []))


# ---------------------------------------------------------------------------
# GitHub Issues support
# ---------------------------------------------------------------------------

def _is_github_ref(ticket: str) -> bool:
    """Check if the ticket reference is a GitHub issue (owner/repo#123)."""
    return bool(_GITHUB_REF_PATTERN.match(ticket))


def _parse_github_ref(ticket: str) -> tuple[str, str, int]:
    """Parse 'owner/repo#123' into (owner, repo, issue_number)."""
    match = _GITHUB_REF_PATTERN.match(ticket)
    if not match:
        _exit_with_error(
            f"Invalid GitHub issue reference: {ticket}\n"
            "  Expected format: owner/repo#123"
        )
    return match.group(1), match.group(2), int(match.group(3))


def _extract_github_issue(owner: str, repo: str, issue_number: int) -> dict:
    """Fetch and extract a GitHub issue into the standard ticket dict."""
    data = _github_api_get(f"repos/{owner}/{repo}/issues/{issue_number}")
    body = data.get("body") or ""

    return {
        "key": f"{owner}/{repo}#{issue_number}",
        "summary": data.get("title", ""),
        "type": "Pull Request" if data.get("pull_request") else "Issue",
        "status": data.get("state", "open").capitalize(),
        "priority": None,
        "assignee": _nested_get(data, "assignee", "login"),
        "reporter": _nested_get(data, "user", "login"),
        "description": body,
        "acceptance_criteria": _extract_ac_from_markdown(body),
        "labels": [label.get("name") for label in data.get("labels", [])],
        "components": [],
        "fix_versions": [m.get("title") for m in [data.get("milestone")] if m],
        "subtasks": _extract_task_list(body),
        "linked_issues": [],
        "_comments": _fetch_github_comments(owner, repo, issue_number, data),
    }


def _fetch_github_comments(
    owner: str, repo: str, issue_number: int, issue_data: dict
) -> list[dict]:
    """Fetch issue comments if any exist."""
    if issue_data.get("comments", 0) == 0:
        return []
    comments_data = _github_api_get(
        f"repos/{owner}/{repo}/issues/{issue_number}/comments"
    )
    return [
        {
            "author": _nested_get(c, "user", "login"),
            "body": c.get("body", ""),
        }
        for c in comments_data[:_MAX_GITHUB_COMMENTS]
    ]


def _extract_ac_from_markdown(body: str) -> str:
    """Try to extract an 'Acceptance Criteria' section from a markdown body."""
    match = _AC_HEADING_PATTERN.search(body)
    return match.group(1).strip() if match else ""


def _extract_task_list(body: str) -> list[dict]:
    """Extract GitHub task list items (- [ ] / - [x]) as subtasks."""
    return [
        {
            "key": None,
            "summary": text.strip(),
            "status": "Done" if checked.lower() == "x" else "To Do",
        }
        for checked, text in _TASK_LIST_PATTERN.findall(body)
    ]


# ---------------------------------------------------------------------------
# Formatters
# ---------------------------------------------------------------------------

def _format_markdown(ticket: dict, children: list[dict] | None = None) -> str:
    """Format ticket info as readable markdown."""
    lines: list[str] = []
    _append_header(lines, ticket)
    _append_section(lines, "Description", _strip_jira_markup(ticket["description"]))
    _append_section(lines, "Acceptance Criteria", _strip_jira_markup(ticket["acceptance_criteria"]))
    _append_subtasks(lines, ticket.get("subtasks", []))
    _append_linked_issues(lines, ticket.get("linked_issues", []))
    _append_children(lines, children)
    _append_comments(lines, ticket.get("_comments", []))
    return "\n".join(lines)


def _append_header(lines: list[str], ticket: dict) -> None:
    """Append ticket header metadata."""
    lines.append(f"# {ticket['key']}: {ticket['summary']}")
    lines.append("")
    lines.append(f"**Type:** {ticket['type']}  ")
    lines.append(f"**Status:** {ticket['status']}  ")
    lines.append(f"**Priority:** {ticket['priority']}  ")
    if ticket["assignee"]:
        lines.append(f"**Assignee:** {ticket['assignee']}  ")
    if ticket.get("labels"):
        lines.append(f"**Labels:** {', '.join(ticket['labels'])}  ")
    if ticket.get("components"):
        lines.append(f"**Components:** {', '.join(ticket['components'])}  ")
    lines.append("")


def _append_section(lines: list[str], heading: str, content: str) -> None:
    """Append a markdown section if content is non-empty."""
    if content:
        lines.extend([f"## {heading}", "", content, ""])


def _append_subtasks(lines: list[str], subtasks: list[dict]) -> None:
    """Append subtasks section if present."""
    if not subtasks:
        return
    lines.extend(["## Subtasks", ""])
    for st in subtasks:
        lines.append(f"- **{st['key']}** — {st['summary']} _{st['status']}_")
    lines.append("")


def _append_linked_issues(lines: list[str], linked_issues: list[dict]) -> None:
    """Append linked issues section if present."""
    if not linked_issues:
        return
    lines.extend(["## Linked Issues", ""])
    for li in linked_issues:
        lines.append(f"- {li['relationship']}: **{li['key']}** — {li['summary']}")
    lines.append("")


def _append_children(lines: list[str], children: list[dict] | None) -> None:
    """Append expanded child ticket details if present."""
    if not children:
        return
    lines.extend(["## Child Ticket Details", ""])
    for child in children:
        lines.append(f"### {child['key']}: {child['summary']}")
        child_desc = _strip_jira_markup(child["description"])
        if child_desc:
            lines.append(child_desc)
        child_ac = _strip_jira_markup(child["acceptance_criteria"])
        if child_ac:
            lines.extend(["", "**Acceptance Criteria:**", child_ac])
        lines.append("")


def _append_comments(lines: list[str], comments: list[dict]) -> None:
    """Append GitHub comments section if present."""
    if not comments:
        return
    lines.extend(["## Comments", ""])
    for c in comments:
        lines.append(f"**{c['author']}:**")
        lines.append(c["body"].strip())
        lines.append("")


def _format_json(ticket: dict, children: list[dict] | None = None) -> str:
    """Format as JSON."""
    output = dict(ticket)
    if children:
        output["children_details"] = children
    return json.dumps(output, indent=2, ensure_ascii=False)


# ---------------------------------------------------------------------------
# Gherkin generator
# ---------------------------------------------------------------------------

def _generate_gherkin_skeleton(ticket: dict) -> str:
    """Generate a rough Gherkin feature skeleton from ticket details."""
    summary = ticket["summary"]
    desc = _strip_jira_markup(ticket["description"])
    ac = _strip_jira_markup(ticket["acceptance_criteria"])
    tag = _determine_test_tag(ticket)

    lines = [
        f"# Auto-generated from {ticket['key']}",
        "# Review and refine before use",
        "",
        f"@{tag}",
        f"Feature: {summary}",
        "",
    ]

    if desc:
        for line in textwrap.wrap(desc, width=_GHERKIN_WRAP_WIDTH):
            lines.append(f"  {line}")
        lines.append("")

    scenarios = _parse_ac_into_scenarios(ac or desc)
    if scenarios:
        for scenario in scenarios:
            lines.extend([
                "  @normal",
                f"  Scenario: {scenario['title']}",
                "    # TODO: Implement steps",
                "    Given <precondition>",
                "    When <action>",
                "    Then <expected result>",
                "",
            ])
    else:
        lines.extend([
            "  @normal",
            f"  Scenario: {summary}",
            "    # TODO: Derive steps from description and acceptance criteria",
            "    Given <precondition>",
            "    When <action>",
            "    Then <expected result>",
            "",
        ])

    return "\n".join(lines)


def _determine_test_tag(ticket: dict) -> str:
    """Determine the primary test tag based on ticket type."""
    ticket_type = (ticket.get("type") or "").lower()
    if "ui" in ticket_type or "frontend" in ticket_type:
        return "ui"
    return "api"


def _parse_ac_into_scenarios(text: str) -> list[dict]:
    """Try to extract individual acceptance criteria as scenario titles."""
    if not text:
        return []
    scenarios = []
    for item in _AC_ITEMS_PATTERN.findall(text):
        title = item.strip().split("\n")[0].strip()
        if len(title) > _MIN_AC_TITLE_LENGTH:
            title = title[:_MAX_AC_TITLE_LENGTH].rstrip(".")
            scenarios.append({"title": title})
    return scenarios


# ---------------------------------------------------------------------------
# Ticket resolver — routes to Jira or GitHub
# ---------------------------------------------------------------------------

def _fetch_ticket(ticket_ref: str) -> dict:
    """Fetch a ticket from Jira or GitHub based on the reference format."""
    if _is_github_ref(ticket_ref):
        owner, repo, number = _parse_github_ref(ticket_ref)
        return _extract_github_issue(owner, repo, number)
    data = _jira_api_get(f"issue/{ticket_ref}?expand=renderedFields")
    return _extract_jira_ticket(data)


# ---------------------------------------------------------------------------
# Commands
# ---------------------------------------------------------------------------

def cmd_fetch(args: argparse.Namespace) -> None:
    """Fetch and display a ticket."""
    ticket = _fetch_ticket(args.ticket)

    children: list[dict] = []
    if args.include_children and not _is_github_ref(args.ticket):
        for sub in ticket.get("subtasks", []):
            child_data = _jira_api_get(f"issue/{sub['key']}")
            children.append(_extract_jira_ticket(child_data))

    if args.format == "json":
        print(_format_json(ticket, children))
    else:
        print(_format_markdown(ticket, children))


def cmd_generate_gherkin(args: argparse.Namespace) -> None:
    """Fetch a ticket and generate a Gherkin skeleton."""
    ticket = _fetch_ticket(args.ticket)
    print(_generate_gherkin_skeleton(ticket))


# ---------------------------------------------------------------------------
# CLI
# ---------------------------------------------------------------------------

def main() -> None:
    """Entry point for the CLI."""
    parser = argparse.ArgumentParser(
        description="Fetch Jira or GitHub ticket details for test generation.",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=textwrap.dedent("""\
            Jira env vars:
              JIRA_BASE_URL    https://yourcompany.atlassian.net
              JIRA_USER_EMAIL  your-email@company.com
              JIRA_API_TOKEN   your-api-token
            OR:
              JIRA_PAT         personal-access-token

            GitHub env vars:
              GITHUB_TOKEN     ghp_your-personal-access-token

            See .env.example for a template.
        """),
    )
    sub = parser.add_subparsers(dest="command", required=True)

    p_fetch = sub.add_parser("fetch", help="Fetch ticket details")
    p_fetch.add_argument(
        "ticket", help="Ticket key: PROJ-123 (Jira) or owner/repo#123 (GitHub)"
    )
    p_fetch.add_argument(
        "--format", choices=["markdown", "json"], default="markdown"
    )
    p_fetch.add_argument(
        "--include-children",
        action="store_true",
        help="Also fetch subtask/child details",
    )
    p_fetch.set_defaults(func=cmd_fetch)

    p_gherkin = sub.add_parser(
        "generate-gherkin", help="Generate Gherkin skeleton from ticket"
    )
    p_gherkin.add_argument(
        "ticket", help="Ticket key: PROJ-123 (Jira) or owner/repo#123 (GitHub)"
    )
    p_gherkin.set_defaults(func=cmd_generate_gherkin)

    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    _load_env_file()
    main()

