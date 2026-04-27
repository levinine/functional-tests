#!/usr/bin/env python3
"""
OpenAPI Helper Script for Functional Tests

This script reads OpenAPI specifications and provides information to help with test implementation:
- List all available API endpoints
- Show endpoint details (methods, parameters, schemas)
- Generate DSO class templates from OpenAPI models

Supports:
- Local JSON files
- Local YAML files
- Remote URLs (JSON/YAML)

Usage:
    python openapi_helper.py list-endpoints <spec-file-or-url>
    python openapi_helper.py endpoint-details <spec-file-or-url> <path>
    python openapi_helper.py generate-dso <spec-file-or-url> <schema-name>

Examples:
    python openapi_helper.py list-endpoints src/main/resources/openapi/room-open-api.json
    python openapi_helper.py list-endpoints https://petstore.swagger.io/v2/swagger.json
    python openapi_helper.py generate-dso https://petstore.swagger.io/v2/swagger.json Pet
"""

import json
import sys
from pathlib import Path
from typing import Dict, List, Any, Optional
from urllib.request import urlopen, Request
from urllib.error import URLError, HTTPError


def load_openapi_spec(spec_path: str) -> Dict[str, Any]:
    """Load OpenAPI specification from JSON/YAML file or URL."""
    
    # Check if it's a URL
    if spec_path.startswith('http://') or spec_path.startswith('https://'):
        try:
            print(f"Fetching OpenAPI spec from URL: {spec_path}")
            req = Request(spec_path, headers={'User-Agent': 'OpenAPI-Helper/1.0'})
            with urlopen(req, timeout=30) as response:
                content = response.read().decode('utf-8')
                
                # Try to parse as JSON first
                try:
                    return json.loads(content)
                except json.JSONDecodeError:
                    # Try YAML if JSON fails
                    return _parse_yaml(content, spec_path)
        except HTTPError as e:
            print(f"HTTP Error {e.code}: {e.reason}")
            print(f"Failed to fetch from URL: {spec_path}")
            sys.exit(1)
        except URLError as e:
            print(f"URL Error: {e.reason}")
            print(f"Failed to fetch from URL: {spec_path}")
            sys.exit(1)
        except Exception as e:
            print(f"Error fetching URL: {e}")
            sys.exit(1)
    
    # Local file
    spec_file = Path(spec_path)
    if not spec_file.exists():
        print(f"Error: File not found: {spec_path}")
        sys.exit(1)
    
    with open(spec_file, 'r') as f:
        content = f.read()
        
        # Determine format by extension or content
        if spec_path.endswith('.json'):
            return json.loads(content)
        elif spec_path.endswith('.yaml') or spec_path.endswith('.yml'):
            return _parse_yaml(content, spec_path)
        else:
            # Try JSON first, then YAML
            try:
                return json.loads(content)
            except json.JSONDecodeError:
                return _parse_yaml(content, spec_path)


def _parse_yaml(content: str, source: str) -> Dict[str, Any]:
    """Parse YAML content, with fallback error if PyYAML not available."""
    try:
        import yaml  # type: ignore  # PyYAML is optional dependency
        return yaml.safe_load(content)
    except ImportError:
        print("Error: PyYAML is not installed.")
        print("To parse YAML files, install it with: pip install pyyaml")
        print(f"Alternatively, convert the spec to JSON format.")
        sys.exit(1)
    except Exception as e:
        print(f"Error parsing YAML from {source}: {e}")
        sys.exit(1)


def list_endpoints(spec: Dict[str, Any]) -> List[Dict[str, Any]]:
    """Extract all endpoints from OpenAPI spec."""
    endpoints = []
    paths = spec.get('paths', {})
    
    for path, methods in paths.items():
        for method, details in methods.items():
            if method.upper() in ['GET', 'POST', 'PUT', 'DELETE', 'PATCH']:
                endpoints.append({
                    'path': path,
                    'method': method.upper(),
                    'operationId': details.get('operationId', ''),
                    'summary': details.get('summary', ''),
                    'tags': details.get('tags', [])
                })
    
    return endpoints


def print_endpoints_table(endpoints: List[Dict[str, Any]]):
    """Print endpoints in a formatted table."""
    print("\n" + "="*100)
    print(f"{'Method':<8} {'Path':<40} {'Operation ID':<30} {'Tags':<20}")
    print("="*100)
    
    for endpoint in endpoints:
        tags = ', '.join(endpoint['tags']) if endpoint['tags'] else 'N/A'
        print(f"{endpoint['method']:<8} {endpoint['path']:<40} {endpoint['operationId']:<30} {tags:<20}")
    
    print("="*100)
    print(f"\nTotal endpoints: {len(endpoints)}\n")


def get_endpoint_details(spec: Dict[str, Any], path: str) -> Optional[Dict[str, Any]]:
    """Get detailed information about a specific endpoint."""
    paths = spec.get('paths', {})
    
    if path not in paths:
        return None
    
    endpoint_data = paths[path]
    details = {'path': path, 'methods': {}}
    
    for method, info in endpoint_data.items():
        if method.upper() in ['GET', 'POST', 'PUT', 'DELETE', 'PATCH']:
            method_details = {
                'operationId': info.get('operationId', ''),
                'summary': info.get('summary', ''),
                'description': info.get('description', ''),
                'parameters': info.get('parameters', []),
                'requestBody': info.get('requestBody', {}),
                'responses': info.get('responses', {})
            }
            details['methods'][method.upper()] = method_details
    
    return details


def print_endpoint_details(details: Dict[str, Any]):
    """Print detailed endpoint information."""
    print(f"\n{'='*80}")
    print(f"Endpoint: {details['path']}")
    print(f"{'='*80}\n")
    
    for method, info in details['methods'].items():
        print(f"{method} - {info['operationId']}")
        print(f"Summary: {info['summary']}")
        
        if info['description']:
            print(f"Description: {info['description']}")
        
        # Parameters
        if info['parameters']:
            print("\nParameters:")
            for param in info['parameters']:
                required = " (required)" if param.get('required', False) else ""
                print(f"  - {param['name']} ({param['in']}){required}: {param.get('description', '')}")
        
        # Request Body
        if info['requestBody']:
            print("\nRequest Body:")
            content = info['requestBody'].get('content', {})
            for content_type, schema_info in content.items():
                print(f"  Content-Type: {content_type}")
                if 'schema' in schema_info:
                    print(f"  Schema: {schema_info['schema']}")
        
        # Responses
        if info['responses']:
            print("\nResponses:")
            for status_code, response_info in info['responses'].items():
                description = response_info.get('description', '')
                print(f"  {status_code}: {description}")
        
        print("\n" + "-"*80 + "\n")


def get_schema_definition(spec: Dict[str, Any], schema_name: str) -> Optional[Dict[str, Any]]:
    """Get schema definition from OpenAPI spec (supports both OpenAPI 3.0 and Swagger 2.0)."""
    # Try OpenAPI 3.0 format (components/schemas)
    components = spec.get('components', {})
    schemas = components.get('schemas', {})
    
    if schema_name in schemas:
        return schemas.get(schema_name)
    
    # Try Swagger 2.0 format (definitions)
    definitions = spec.get('definitions', {})
    if schema_name in definitions:
        return definitions.get(schema_name)
    
    return None


def java_type_mapping(openapi_type: str, format_type: Optional[str] = None) -> str:
    """Map OpenAPI types to Java types."""
    type_map = {
        'string': 'String',
        'integer': 'Integer',
        'number': 'Double',
        'boolean': 'Boolean',
        'array': 'List',
        'object': 'Object'
    }
    
    # Handle format-specific types
    if format_type:
        format_map = {
            'int32': 'Integer',
            'int64': 'Long',
            'float': 'Float',
            'double': 'Double',
            'date': 'LocalDate',
            'date-time': 'LocalDateTime'
        }
        return format_map.get(format_type, type_map.get(openapi_type, 'Object'))
    
    return type_map.get(openapi_type, 'Object')


def generate_dso_template(spec: Dict[str, Any], schema_name: str, package_name: str = "com.levi9.functionaltests.rest.data") -> str:
    """Generate Java DSO class template from OpenAPI schema."""
    schema = get_schema_definition(spec, schema_name)
    
    if not schema:
        # List available schemas
        available_schemas = []
        
        # Check OpenAPI 3.0 format
        components = spec.get('components', {})
        schemas = components.get('schemas', {})
        available_schemas.extend(schemas.keys())
        
        # Check Swagger 2.0 format
        definitions = spec.get('definitions', {})
        available_schemas.extend(definitions.keys())
        
        error_msg = f"Schema '{schema_name}' not found in OpenAPI spec."
        if available_schemas:
            error_msg += f"\n\nAvailable schemas: {', '.join(sorted(set(available_schemas)))}"
        return error_msg
    
    properties = schema.get('properties', {})
    required_fields = schema.get('required', [])
    
    # Start building the class
    lines = [
        "package " + package_name + ";",
        "",
        "import com.fasterxml.jackson.annotation.JsonProperty;",
        "import lombok.AllArgsConstructor;",
        "import lombok.Builder;",
        "import lombok.Getter;",
        "import lombok.NoArgsConstructor;",
        "import lombok.Setter;",
        "",
        "import java.util.List;",
        "import java.time.LocalDate;",
        "import java.time.LocalDateTime;",
        "",
        "/**",
        f" * Data Service Object for {schema_name}",
        " * Generated from OpenAPI specification",
        " */",
        "@Getter",
        "@Setter",
        "@NoArgsConstructor",
        "@AllArgsConstructor",
        "@Builder(toBuilder = true)",
        f"public class {schema_name}DSO {{",
        ""
    ]
    
    # Add fields
    for prop_name, prop_info in properties.items():
        prop_type = prop_info.get('type', 'object')
        prop_format = prop_info.get('format')
        prop_description = prop_info.get('description', '')
        
        java_type = java_type_mapping(prop_type, prop_format)
        
        # Handle arrays
        if prop_type == 'array':
            items = prop_info.get('items', {})
            item_type = items.get('type', 'Object')
            item_ref = items.get('$ref', '')
            
            if item_ref:
                # Extract schema name from $ref
                item_type = item_ref.split('/')[-1]
            else:
                item_type = java_type_mapping(item_type, items.get('format'))
            
            java_type = f"List<{item_type}>"
        
        # Handle object references
        if '$ref' in prop_info:
            java_type = prop_info['$ref'].split('/')[-1]
        
        # Add JavaDoc if description exists
        if prop_description:
            lines.append(f"    /**")
            lines.append(f"     * {prop_description}")
            lines.append(f"     */")
        
        # Add @JsonProperty if field name differs from Java conventions
        if '_' in prop_name or prop_name != prop_name.lower():
            lines.append(f'    @JsonProperty("{prop_name}")')
        
        # Convert snake_case to camelCase for Java field name
        java_field_name = ''.join(word.capitalize() if i > 0 else word 
                                   for i, word in enumerate(prop_name.split('_')))
        
        lines.append(f"    private {java_type} {java_field_name};")
        lines.append("")
    
    lines.append("}")
    
    return '\n'.join(lines)


def main():
    if len(sys.argv) < 2:
        print(__doc__)
        sys.exit(1)
    
    command = sys.argv[1]
    
    if command == 'list-endpoints':
        if len(sys.argv) < 3:
            print("Usage: openapi_helper.py list-endpoints <spec-file>")
            sys.exit(1)
        
        spec_file = sys.argv[2]
        spec = load_openapi_spec(spec_file)
        endpoints = list_endpoints(spec)
        print_endpoints_table(endpoints)
    
    elif command == 'endpoint-details':
        if len(sys.argv) < 4:
            print("Usage: openapi_helper.py endpoint-details <spec-file> <path>")
            sys.exit(1)
        
        spec_file = sys.argv[2]
        path = sys.argv[3]
        spec = load_openapi_spec(spec_file)
        details = get_endpoint_details(spec, path)
        
        if details:
            print_endpoint_details(details)
        else:
            print(f"Endpoint '{path}' not found in OpenAPI spec.")
            sys.exit(1)
    
    elif command == 'generate-dso':
        if len(sys.argv) < 4:
            print("Usage: openapi_helper.py generate-dso <spec-file> <schema-name> [package-name]")
            sys.exit(1)
        
        spec_file = sys.argv[2]
        schema_name = sys.argv[3]
        package_name = sys.argv[4] if len(sys.argv) > 4 else "com.levi9.functionaltests.rest.data"
        
        spec = load_openapi_spec(spec_file)
        dso_code = generate_dso_template(spec, schema_name, package_name)
        print(dso_code)
    
    else:
        print(f"Unknown command: {command}")
        print(__doc__)
        sys.exit(1)


if __name__ == '__main__':
    main()
