# Functional Tests Skill — Scripts

Helper scripts for the `functional-tests-skill`. No external dependencies required — all scripts use Python standard library only (PyYAML optional for YAML OpenAPI specs).
 
## Scripts

| Script | Purpose |
|--------|---------|
| [`openapi_helper.py`](#openapi-helper) | List endpoints, view details, generate Java DSOs from OpenAPI specs |
| [`jira_ticket_fetcher.py`](#jira--github-ticket-fetcher) | Fetch ticket details from Jira or GitHub Issues for test generation |

## Requirements

- Python 3.10+
- PyYAML (optional, only for YAML OpenAPI specs): `pip install pyyaml`

---

## OpenAPI Helper

This Python script helps developers work with OpenAPI specifications during test development.

### Capabilities

1. **List Endpoints** — Display all API endpoints from an OpenAPI spec
2. **Endpoint Details** — Show detailed information about a specific endpoint
3. **Generate DSO** — Generate Java DSO class templates from OpenAPI schemas

✅ Supports local files and remote URLs  
✅ Handles both JSON and YAML OpenAPI specifications  
✅ No external dependencies

### Usage

#### List All Endpoints

Display a table of all available API endpoints:

```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py list-endpoints \
  src/main/resources/openapi/restfulbooker/room-open-api.json
```

**Examples:**

Local JSON file:
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py list-endpoints \
  src/main/resources/openapi/restfulbooker/room-open-api.json
```

Remote URL:
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py list-endpoints \
  https://petstore.swagger.io/v2/swagger.json
```

Local YAML file:
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py list-endpoints \
  specs/api-spec.yaml
```

**Output:**
```
====================================================================================================
Method   Path                                     Operation ID                   Tags                
====================================================================================================
GET      /{id}                                    getRoom                        room-controller     
PUT      /{id}                                    updateRoom                     room-controller     
DELETE   /{id}                                    deleteRoom                     room-controller     
GET      /                                        getRooms                       room-controller     
POST     /                                        createRoom                     room-controller     
====================================================================================================

Total endpoints: 5
```

#### Get Endpoint Details

Show detailed information about a specific endpoint:

```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py endpoint-details \
  src/main/resources/openapi/restfulbooker/room-open-api.json \
  /room/{id}
```

**Examples:**

Local file:
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py endpoint-details \
  src/main/resources/openapi/restfulbooker/room-open-api.json \
  /room/{id}
```

Remote URL:
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py endpoint-details \
  https://petstore.swagger.io/v2/swagger.json \
  /pet/{petId}
```

#### Generate DSO Class Template

Generate a complete Java DSO class from an OpenAPI schema:

```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py generate-dso \
  src/main/resources/openapi/restfulbooker/room-open-api.json \
  Room \
  com.levi9.functionaltests.rest.data.restfulbooker
```

**Examples:**

Local file:
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py generate-dso \
  src/main/resources/openapi/restfulbooker/room-open-api.json \
  Room \
  com.levi9.functionaltests.rest.data.restfulbooker
```

Remote URL:
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py generate-dso \
  https://petstore.swagger.io/v2/swagger.json \
  Pet \
  com.levi9.functionaltests.rest.data.petstore
```

**Output:**
```java
package com.levi9.functionaltests.rest.data.restfulbooker;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Service Object for Room
 * Generated from OpenAPI specification
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RoomDSO {

    private Integer roomid;

    @JsonProperty("roomName")
    private String roomName;

    private String type;

    // ... other fields
}
```

The generated DSO includes:
- ✅ Lombok annotations (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
- ✅ Correct Java types based on OpenAPI schema
- ✅ `@JsonProperty` annotations for non-standard field names
- ✅ JavaDoc comments from OpenAPI descriptions
- ✅ Proper imports for List, LocalDate, LocalDateTime

### Type Mapping

The script automatically maps OpenAPI types to Java types:

| OpenAPI Type | OpenAPI Format | Java Type |
|--------------|----------------|-----------|
| string       | -              | String    |
| string       | date           | LocalDate |
| string       | date-time      | LocalDateTime |
| integer      | int32          | Integer   |
| integer      | int64          | Long      |
| number       | float          | Float     |
| number       | double         | Double    |
| boolean      | -              | Boolean   |
| array        | -              | List<T>   |
| object       | -              | Object or referenced type |

### Notes

- Reads OpenAPI 3.0 and Swagger 2.0 specifications
- Supports both JSON and YAML formats (PyYAML required for YAML)
- URLs are fetched with a 30-second timeout
- Default package: `com.levi9.functionaltests.rest.data`
- Generated DSOs follow project conventions (Lombok, builder pattern, etc.)

---

## Jira & GitHub Ticket Fetcher

Fetches ticket details from **Jira** or **GitHub Issues** and extracts feature requirements, acceptance criteria, subtasks, and linked issues — enabling test generation directly from tickets.

### Setup

1. Copy the example environment file and fill in your credentials:

```bash
cp .github/skills/functional-tests-skill/scripts/.env.example \
   .github/skills/functional-tests-skill/scripts/.env
```

2. Fill in the relevant values in `.env`:

**Jira Cloud:**
```
JIRA_BASE_URL=https://yourcompany.atlassian.net
JIRA_USER_EMAIL=your-email@company.com
JIRA_API_TOKEN=your-api-token
```

**Jira Server / Data Center (PAT):**
```
JIRA_BASE_URL=https://jira.yourcompany.com
JIRA_PAT=your-personal-access-token
```

**GitHub Issues:**
```
GITHUB_TOKEN=ghp_your-personal-access-token
```

> The `.env` file is auto-loaded from next to the script or from the project root. Already-set environment variables take precedence.

### Usage

The script auto-detects the source based on the ticket reference format:
- **Jira:** `PROJ-123`
- **GitHub:** `owner/repo#123`

#### Fetch Ticket Details

```bash
# Jira
python3 .github/skills/functional-tests-skill/scripts/jira_ticket_fetcher.py fetch PROJ-123

# GitHub
python3 .github/skills/functional-tests-skill/scripts/jira_ticket_fetcher.py fetch owner/repo#42
```

Output includes: summary, description, acceptance criteria, subtasks, linked issues, and (for GitHub) comments.

#### Fetch with Child/Subtask Details (Jira only)

```bash
python3 .github/skills/functional-tests-skill/scripts/jira_ticket_fetcher.py fetch PROJ-123 --include-children
```

#### Output as JSON

```bash
python3 .github/skills/functional-tests-skill/scripts/jira_ticket_fetcher.py fetch PROJ-123 --format json
```

#### Generate Gherkin Skeleton

Generates a `.feature` file skeleton from the ticket's acceptance criteria:

```bash
# Jira
python3 .github/skills/functional-tests-skill/scripts/jira_ticket_fetcher.py generate-gherkin PROJ-123

# GitHub
python3 .github/skills/functional-tests-skill/scripts/jira_ticket_fetcher.py generate-gherkin owner/repo#42
```

### GitHub-Specific Features

- Fetches issue comments (up to 10) for additional context
- Parses task list checkboxes (`- [ ]` / `- [x]`) as subtasks
- Extracts `## Acceptance Criteria` sections from the issue body
- Works with public repos without a token; private repos require `GITHUB_TOKEN` with `repo` scope

### Workflow: From Ticket to Tests

1. **Fetch the ticket** to get full context
2. **Analyze** the description and acceptance criteria
3. **Generate a Gherkin skeleton** (optional starting point)
4. **Implement the full test** — feature file, step definitions, services, page objects, etc.
5. **Map each acceptance criterion** to at least one scenario

### Notes

- Uses Jira REST API v3 (Cloud) — works with Jira Server/Data Center via PAT
- `.env` file is searched next to the script, then in the project root
- No external Python dependencies required

---

## File Structure

```
scripts/
├── README.md              ← This file
├── .env.example           ← Template for environment variables
├── openapi_helper.py      ← OpenAPI spec helper
└── jira_ticket_fetcher.py ← Jira & GitHub ticket fetcher
```
