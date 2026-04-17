# OpenAPI Helper Script

This Python script helps developers work with OpenAPI specifications during test development.

## Overview

The `openapi_helper.py` script provides three main capabilities:
1. **List Endpoints** - Display all API endpoints from an OpenAPI spec
2. **Endpoint Details** - Show detailed information about a specific endpoint
3. **Generate DSO** - Generate Java DSO class templates from OpenAPI schemas

## Features

✅ **Multiple Sources**: Supports local files and remote URLs  
✅ **Format Support**: Handles both JSON and YAML OpenAPI specifications  
✅ **No Dependencies**: Uses Python standard library (PyYAML optional for YAML support)

## Requirements

- Python 3.x
- PyYAML (optional, only needed for YAML format support): `pip install pyyaml`

## Usage

### 1. List All Endpoints

Display a table of all available API endpoints:

```bash
python3 openapi_helper.py list-endpoints <spec-file-or-url>
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

### 2. Get Endpoint Details

Show detailed information about a specific endpoint:

```bash
python3 openapi_helper.py endpoint-details <spec-file-or-url> <path>
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

**Output includes:**
- HTTP methods supported
- Operation ID and summary
- Request parameters (path, query, header)
- Request body schema
- Response codes and descriptions

### 3. Generate DSO Class Template

Generate a complete Java DSO class from an OpenAPI schema:

```bash
python3 openapi_helper.py generate-dso <spec-file-or-url> <schema-name> [package-name]
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

## Type Mapping

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

## When to Use

Use this script when:
- **Starting a new REST API test** - List endpoints to understand what's available
- **Creating DSO classes** - Generate templates that match the OpenAPI spec exactly
- **API documentation is unclear** - View endpoint details with parameters and schemas
- **API spec changes** - Regenerate DSOs to ensure consistency
- **Working with third-party APIs** - Fetch specs directly from remote URLs (e.g., Swagger UI endpoints)
- **Evaluating external APIs** - Quickly explore API structure without downloading files

## Supported Formats

| Source Type | Format | Example |
|-------------|--------|---------|
| Local file | JSON | `src/main/resources/openapi/room-open-api.json` |
| Local file | YAML | `specs/api-spec.yaml` |
| Remote URL | JSON | `https://petstore.swagger.io/v2/swagger.json` |
| Remote URL | YAML | `https://example.com/api/openapi.yaml` |
| Swagger UI | JSON | `https://api.example.com/swagger/v1/swagger.json` |

## Integration with AI Skill

This script is referenced in the `functional-tests-skill` SKILL.md:
- In the "Creating a New REST API Test" workflow (Step 4: Create DSOs)
- In the "OpenAPI Model Generation" section

The AI assistant can invoke this script to help generate accurate test code that matches the OpenAPI specification.

## Examples

### Complete Workflow Example

When creating a new REST API test for booking:

1. **Discover available endpoints:**
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py list-endpoints \
  src/main/resources/openapi/restfulbooker/booking-open-api.json
```

2. **Get details about the POST endpoint:**
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py endpoint-details \
  src/main/resources/openapi/restfulbooker/booking-open-api.json \
  /booking
```

3. **Generate DSO for the Booking schema:**
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py generate-dso \
  src/main/resources/openapi/restfulbooker/booking-open-api.json \
  Booking \
  com.levi9.functionaltests.rest.data.restfulbooker > src/main/java/com/levi9/functionaltests/rest/data/restfulbooker/BookingDSO.java
```

4. **Review and customize** the generated DSO if needed

### Working with Third-Party APIs

When testing external APIs like Petstore:

1. **List endpoints from remote spec:**
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py list-endpoints \
  https://petstore.swagger.io/v2/swagger.json
```

2. **Explore specific endpoint:**
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py endpoint-details \
  https://petstore.swagger.io/v2/swagger.json \
  /pet/{petId}
```

3. **Generate Pet DSO:**
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py generate-dso \
  https://petstore.swagger.io/v2/swagger.json \
  Pet \
  com.levi9.functionaltests.rest.data.petstore
```

## Notes

- The script reads OpenAPI 3.0 and Swagger 2.0 specifications
- Supports both JSON and YAML formats
- For YAML files, PyYAML must be installed: `pip install pyyaml`
- URLs are fetched with a 30-second timeout
- If `package-name` is not provided, it defaults to `com.levi9.functionaltests.rest.data`
- Generated DSOs follow project conventions (Lombok usage, builder pattern, etc.)
- The script is read-only — it never modifies the OpenAPI spec files or remote sources
- User-Agent header is set to `OpenAPI-Helper/1.0` for URL requests
