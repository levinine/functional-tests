#!/usr/bin/env python3
"""
Jira Ticket Fetcher — Extracts ticket details to help generate test scenarios.

Connects to a Jira instance (or compatible board like Azure DevOps, Linear, etc.)
via REST API, fetches ticket details, and outputs structured information about
what the feature is, what needs to be done, and how it should work.

Usage:
    # Fetch a single ticket
    python3 jira_ticket_fetcher.py fetch PROJ-123

    # Fetch a ticket with all linked/child issues
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

import argparse
import json
import os
import re
import sys
import textwrap
from pathlib import Path
from urllib.request import Request, urlopen
from urllib.error import HTTPError, URLError
from base64 import b64encode


# ---------------------------------------------------------------------------
# .env file loader
# ---------------------------------------------------------------------------

def _load_env_file():
    """Load variables from a .env file if present.

    Searches for .env in this order:
      1. Next to this script  (.github/skills/functional-tests-skill/scripts/.env)
      2. In the project root   (functional-tests/.env)

    Lines like KEY=VALUE or KEY="VALUE" are parsed. Comments (#) and blank
    lines are ignored. Already-set env vars are NOT overwritten.
    """
    script_dir = Path(__file__).resolve().parent
    project_root = script_dir.parents[3]  # .github/skills/functional-tests-skill/scripts -> project root

    candidates = [script_dir / ".env", project_root / ".env"]

    for env_path in candidates:
        if env_path.is_file():
            with open(env_path) as f:
                for line in f:
                    line = line.strip()
                    if not line or line.startswith("#"):
                        continue
                    if "=" not in line:
                        continue
                    key, _, value = line.partition("=")
                    key = key.strip()
                    value = value.strip().strip("\"'")
                    # Don't overwrite existing env vars
                    if key and key not in os.environ:
                        os.environ[key] = value
            break  # use the first .env found


_load_env_file()


# ---------------------------------------------------------------------------
# Authentication helpers
# ---------------------------------------------------------------------------

def _get_auth_header():
    """Build the Authorization header based on available env vars."""
    pat = os.environ.get("JIRA_PAT")
    if pat:
        return f"Bearer {pat}"

    email = os.environ.get("JIRA_USER_EMAIL")
    token = os.environ.get("JIRA_API_TOKEN")
    if email and token:
        creds = b64encode(f"{email}:{token}".encode()).decode()
        return f"Basic {creds}"

    return None


def _get_base_url():
    url = os.environ.get("JIRA_BASE_URL", "").rstrip("/")
    if not url:
        print("ERROR: JIRA_BASE_URL environment variable is not set.", file=sys.stderr)
        print("  Set it to your Jira instance URL, e.g.:", file=sys.stderr)
        print("  export JIRA_BASE_URL=https://yourcompany.atlassian.net", file=sys.stderr)
        sys.exit(1)
    return url


# ---------------------------------------------------------------------------
# API interaction
# ---------------------------------------------------------------------------

def _api_get(path: str) -> dict:
    """Perform a GET request to the Jira REST API."""
    base_url = _get_base_url()
    url = f"{base_url}/rest/api/3/{path.lstrip('/')}"

    headers = {"Accept": "application/json", "Content-Type": "application/json"}
    auth = _get_auth_header()
    if auth:
        headers["Authorization"] = auth

    req = Request(url, headers=headers, method="GET")
    try:
        with urlopen(req) as resp:
            return json.loads(resp.read().decode())
    except HTTPError as e:
        body = e.read().decode() if e.fp else ""
        print(f"ERROR: HTTP {e.code} from {url}", file=sys.stderr)
        if body:
            print(body[:500], file=sys.stderr)
        sys.exit(1)
    except URLError as e:
        print(f"ERROR: Cannot reach {url} — {e.reason}", file=sys.stderr)
        sys.exit(1)


# ---------------------------------------------------------------------------
# Ticket parsing
# ---------------------------------------------------------------------------

def _extract_ticket(data: dict) -> dict:
    """Extract the useful fields from a Jira issue JSON response."""
    fields = data.get("fields", {})

    # Acceptance criteria may live in a custom field or in description
    acceptance_criteria = (
        fields.get("customfield_10400")  # common custom field id
        or fields.get("customfield_10300")
        or fields.get("customfield_10206")
        or ""
    )

    # Extract subtasks / child issues
    subtasks = []
    for sub in fields.get("subtasks", []):
        subtasks.append({
            "key": sub.get("key"),
            "summary": sub.get("fields", {}).get("summary"),
            "status": sub.get("fields", {}).get("status", {}).get("name"),
        })

    # Extract linked issues
    links = []
    for link in fields.get("issuelinks", []):
        linked = link.get("outwardIssue") or link.get("inwardIssue")
        if linked:
            links.append({
                "key": linked.get("key"),
                "summary": linked.get("fields", {}).get("summary"),
                "relationship": link.get("type", {}).get("outward") or link.get("type", {}).get("name"),
            })

    return {
        "key": data.get("key"),
        "summary": fields.get("summary"),
        "type": fields.get("issuetype", {}).get("name"),
        "status": fields.get("status", {}).get("name"),
        "priority": fields.get("priority", {}).get("name"),
        "assignee": (fields.get("assignee") or {}).get("displayName"),
        "reporter": (fields.get("reporter") or {}).get("displayName"),
        "description": fields.get("description") or "",
        "acceptance_criteria": acceptance_criteria,
        "labels": fields.get("labels", []),
        "components": [c.get("name") for c in fields.get("components", [])],
        "fix_versions": [v.get("name") for v in fields.get("fixVersions", [])],
        "subtasks": subtasks,
        "linked_issues": links,
    }


def _strip_jira_markup(text: str) -> str:
    """Rough conversion of Jira wiki markup / ADF to plain text."""
    if not text:
        return ""
    # Handle ADF (Atlassian Document Format) JSON
    if isinstance(text, dict):
        return _adf_to_text(text)
    # Simple wiki-markup stripping
    text = re.sub(r"\{noformat\}.*?\{noformat\}", "", text, flags=re.DOTALL)
    text = re.sub(r"\{code[^}]*\}.*?\{code\}", "", text, flags=re.DOTALL)
    text = re.sub(r"\[([^|]*)\|[^\]]*\]", r"\1", text)  # [text|url] -> text
    text = re.sub(r"[{*_~^+]", "", text)  # remove markup chars
    text = re.sub(r"h[1-6]\.\s*", "", text)  # headings
    text = re.sub(r"^[#\-\*]+\s*", "", text, flags=re.MULTILINE)  # list markers
    return text.strip()


def _adf_to_text(node: dict) -> str:
    """Recursively extract text from Atlassian Document Format."""
    if node.get("type") == "text":
        return node.get("text", "")
    parts = []
    for child in node.get("content", []):
        parts.append(_adf_to_text(child))
    return "\n".join(parts)


# ---------------------------------------------------------------------------
# GitHub Issues support
# ---------------------------------------------------------------------------

def _is_github_ref(ticket: str) -> bool:
    """Check if the ticket reference is a GitHub issue (owner/repo#123)."""
    return bool(re.match(r"^[\w.-]+/[\w.-]+#\d+$", ticket))


def _parse_github_ref(ticket: str) -> tuple[str, str, int]:
    """Parse 'owner/repo#123' into (owner, repo, issue_number)."""
    match = re.match(r"^([\w.-]+)/([\w.-]+)#(\d+)$", ticket)
    if not match:
        print(f"ERROR: Invalid GitHub issue reference: {ticket}", file=sys.stderr)
        print("  Expected format: owner/repo#123", file=sys.stderr)
        sys.exit(1)
    return match.group(1), match.group(2), int(match.group(3))


def _github_api_get(path: str) -> dict:
    """Perform a GET request to the GitHub REST API."""
    url = f"https://api.github.com/{path.lstrip('/')}"
    headers = {"Accept": "application/vnd.github+json", "X-GitHub-Api-Version": "2022-11-28"}
    token = os.environ.get("GITHUB_TOKEN")
    if token:
        headers["Authorization"] = f"Bearer {token}"

    req = Request(url, headers=headers, method="GET")
    try:
        with urlopen(req) as resp:
            return json.loads(resp.read().decode())
    except HTTPError as e:
        body = e.read().decode() if e.fp else ""
        print(f"ERROR: HTTP {e.code} from {url}", file=sys.stderr)
        if e.code == 401:
            print("  Set GITHUB_TOKEN env var for authentication.", file=sys.stderr)
        elif e.code == 404:
            print("  Issue not found or repo is private (set GITHUB_TOKEN with 'repo' scope).", file=sys.stderr)
        if body:
            try:
                msg = json.loads(body).get("message", body[:300])
            except Exception:
                msg = body[:300]
            print(f"  {msg}", file=sys.stderr)
        sys.exit(1)
    except URLError as e:
        print(f"ERROR: Cannot reach {url} — {e.reason}", file=sys.stderr)
        sys.exit(1)


def _extract_github_issue(owner: str, repo: str, issue_number: int) -> dict:
    """Fetch and extract a GitHub issue into the standard ticket dict."""
    data = _github_api_get(f"repos/{owner}/{repo}/issues/{issue_number}")

    # Fetch comments for additional context
    comments = []
    if data.get("comments", 0) > 0:
        comments_data = _github_api_get(f"repos/{owner}/{repo}/issues/{issue_number}/comments")
        for c in comments_data[:10]:  # limit to 10
            comments.append({
                "author": (c.get("user") or {}).get("login"),
                "body": c.get("body", ""),
            })

    # Try to extract acceptance criteria from issue body
    body = data.get("body") or ""
    acceptance_criteria = _extract_ac_from_markdown(body)

    # Linked issues / sub-tasks from task list checkboxes
    subtasks = _extract_task_list(body)

    return {
        "key": f"{owner}/{repo}#{issue_number}",
        "summary": data.get("title", ""),
        "type": "Pull Request" if data.get("pull_request") else "Issue",
        "status": data.get("state", "open").capitalize(),
        "priority": None,
        "assignee": (data.get("assignee") or {}).get("login"),
        "reporter": (data.get("user") or {}).get("login"),
        "description": body,
        "acceptance_criteria": acceptance_criteria,
        "labels": [l.get("name") for l in data.get("labels", [])],
        "components": [],
        "fix_versions": [m.get("title") for m in [data.get("milestone")] if m],
        "subtasks": subtasks,
        "linked_issues": [],
        "_comments": comments,
    }


def _extract_ac_from_markdown(body: str) -> str:
    """Try to extract an 'Acceptance Criteria' section from a markdown body."""
    # Look for a heading like ## Acceptance Criteria, ### AC, etc.
    match = re.search(
        r"(?:^|\n)#{1,4}\s*(?:acceptance\s*criteria|AC)\s*\n(.*?)(?=\n#{1,4}\s|\Z)",
        body, re.IGNORECASE | re.DOTALL,
    )
    return match.group(1).strip() if match else ""


def _extract_task_list(body: str) -> list[dict]:
    """Extract GitHub task list items (- [ ] / - [x]) as subtasks."""
    items = re.findall(r"- \[([ xX])\]\s+(.*)", body)
    subtasks = []
    for checked, text in items:
        subtasks.append({
            "key": None,
            "summary": text.strip(),
            "status": "Done" if checked.lower() == "x" else "To Do",
        })
    return subtasks


# ---------------------------------------------------------------------------
# Formatters
# ---------------------------------------------------------------------------

def _format_markdown(ticket: dict, children: list[dict] | None = None) -> str:
    """Format ticket info as readable markdown."""
    lines = []
    lines.append(f"# {ticket['key']}: {ticket['summary']}")
    lines.append("")
    lines.append(f"**Type:** {ticket['type']}  ")
    lines.append(f"**Status:** {ticket['status']}  ")
    lines.append(f"**Priority:** {ticket['priority']}  ")
    if ticket["assignee"]:
        lines.append(f"**Assignee:** {ticket['assignee']}  ")
    if ticket["labels"]:
        lines.append(f"**Labels:** {', '.join(ticket['labels'])}  ")
    if ticket["components"]:
        lines.append(f"**Components:** {', '.join(ticket['components'])}  ")
    lines.append("")

    desc = _strip_jira_markup(ticket["description"])
    if desc:
        lines.append("## Description")
        lines.append("")
        lines.append(desc)
        lines.append("")

    ac = _strip_jira_markup(ticket["acceptance_criteria"])
    if ac:
        lines.append("## Acceptance Criteria")
        lines.append("")
        lines.append(ac)
        lines.append("")

    if ticket["subtasks"]:
        lines.append("## Subtasks")
        lines.append("")
        for st in ticket["subtasks"]:
            lines.append(f"- **{st['key']}** — {st['summary']} _{st['status']}_")
        lines.append("")

    if ticket["linked_issues"]:
        lines.append("## Linked Issues")
        lines.append("")
        for li in ticket["linked_issues"]:
            lines.append(f"- {li['relationship']}: **{li['key']}** — {li['summary']}")
        lines.append("")

    if children:
        lines.append("## Child Ticket Details")
        lines.append("")
        for child in children:
            lines.append(f"### {child['key']}: {child['summary']}")
            child_desc = _strip_jira_markup(child["description"])
            if child_desc:
                lines.append(child_desc)
            child_ac = _strip_jira_markup(child["acceptance_criteria"])
            if child_ac:
                lines.append("")
                lines.append("**Acceptance Criteria:**")
                lines.append(child_ac)
            lines.append("")

    # GitHub comments
    comments = ticket.get("_comments", [])
    if comments:
        lines.append("## Comments")
        lines.append("")
        for c in comments:
            lines.append(f"**{c['author']}:**")
            lines.append(c["body"].strip())
            lines.append("")

    return "\n".join(lines)


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
    lines = []
    summary = ticket["summary"]
    desc = _strip_jira_markup(ticket["description"])
    ac = _strip_jira_markup(ticket["acceptance_criteria"])

    # Determine tags
    tags = ["@api"]  # default; the user can adjust
    ticket_type = (ticket.get("type") or "").lower()
    if "ui" in ticket_type or "frontend" in ticket_type:
        tags = ["@ui"]

    lines.append(f"# Auto-generated from {ticket['key']}")
    lines.append(f"# Review and refine before use")
    lines.append("")
    lines.append(" ".join(f"@{t.lstrip('@')}" for t in tags))
    lines.append(f"Feature: {summary}")
    lines.append("")
    if desc:
        for line in textwrap.wrap(desc, width=100):
            lines.append(f"  {line}")
        lines.append("")

    # Try to parse acceptance criteria into scenarios
    scenarios = _parse_ac_into_scenarios(ac or desc)
    if scenarios:
        for i, scenario in enumerate(scenarios, 1):
            lines.append(f"  @normal")
            lines.append(f"  Scenario: {scenario['title']}")
            lines.append(f"    # TODO: Implement steps")
            lines.append(f"    Given <precondition>")
            lines.append(f"    When <action>")
            lines.append(f"    Then <expected result>")
            lines.append("")
    else:
        lines.append("  @normal")
        lines.append(f"  Scenario: {summary}")
        lines.append(f"    # TODO: Derive steps from description and acceptance criteria")
        lines.append(f"    Given <precondition>")
        lines.append(f"    When <action>")
        lines.append(f"    Then <expected result>")
        lines.append("")

    return "\n".join(lines)


def _parse_ac_into_scenarios(text: str) -> list[dict]:
    """Try to extract individual acceptance criteria as scenario titles."""
    if not text:
        return []
    scenarios = []
    # Match numbered or bulleted items
    items = re.findall(r"(?:^|\n)\s*(?:\d+[.)]\s*|[-*•]\s*)(.*?)(?=\n\s*(?:\d+[.)]\s*|[-*•]\s*)|\Z)", text, re.DOTALL)
    for item in items:
        title = item.strip().split("\n")[0].strip()
        if len(title) > 10:  # skip tiny fragments
            # Clean up and truncate
            title = title[:120].rstrip(".")
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
    else:
        data = _api_get(f"issue/{ticket_ref}?expand=renderedFields")
        return _extract_ticket(data)


# ---------------------------------------------------------------------------
# Commands
# ---------------------------------------------------------------------------

def cmd_fetch(args):
    """Fetch and display a ticket."""
    ticket = _fetch_ticket(args.ticket)

    children = []
    if args.include_children and not _is_github_ref(args.ticket):
        for sub in ticket.get("subtasks", []):
            child_data = _api_get(f"issue/{sub['key']}")
            children.append(_extract_ticket(child_data))

    if args.format == "json":
        print(_format_json(ticket, children))
    else:
        print(_format_markdown(ticket, children))


def cmd_generate_gherkin(args):
    """Fetch a ticket and generate a Gherkin skeleton."""
    ticket = _fetch_ticket(args.ticket)
    print(_generate_gherkin_skeleton(ticket))


# ---------------------------------------------------------------------------
# CLI
# ---------------------------------------------------------------------------

def main():
    parser = argparse.ArgumentParser(
        description="Fetch Jira ticket details for test generation.",
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

    # fetch
    p_fetch = sub.add_parser("fetch", help="Fetch ticket details")
    p_fetch.add_argument("ticket", help="Ticket key: PROJ-123 (Jira) or owner/repo#123 (GitHub)")
    p_fetch.add_argument("--format", choices=["markdown", "json"], default="markdown")
    p_fetch.add_argument("--include-children", action="store_true", help="Also fetch subtask/child details")
    p_fetch.set_defaults(func=cmd_fetch)

    # generate-gherkin
    p_gherkin = sub.add_parser("generate-gherkin", help="Generate Gherkin skeleton from ticket")
    p_gherkin.add_argument("ticket", help="Ticket key: PROJ-123 (Jira) or owner/repo#123 (GitHub)")
    p_gherkin.set_defaults(func=cmd_generate_gherkin)

    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()


