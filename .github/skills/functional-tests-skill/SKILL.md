---
name: functional-tests-skill
description: Expert testing skill for the Levi9 functional-tests project — a Java 23 / Maven / Cucumber BDD framework using REST Assured for API tests and Selenium 4 for UI tests with Spring DI. Use this skill whenever the user wants to write, create, add, extend, debug, review, or refactor any kind of test in this project. Trigger when the user mentions writing tests, adding test coverage, creating feature files, Gherkin scenarios, step definitions, stepdefs, REST API tests, UI tests, Selenium, page objects, debugging failing tests, fixing test failures, test architecture improvements, generating test data, reviewing test code, or anything related to testing in this Cucumber/REST Assured/Selenium codebase. Even if the user just says "write a test" or "add coverage for X" or "this test is failing", this skill should activate immediately.
---

# Functional Tests Skill

This skill guides you in working with the **Levi9 functional-tests** project — a Java 23 / Maven / Cucumber BDD framework that tests two systems:
- **Pet Store API** (REST API tests only)
- **Restful Booker Platform** (REST API + UI tests)

## Core Principles

1. **Follow existing patterns exactly** — Study similar tests in the codebase before creating new ones. Match naming conventions, package structure, coding style, and architectural patterns.

2. **Run tests after implementation** — After creating or modifying tests, ALWAYS run them to verify they work. If they fail, investigate and fix the root cause.

3. **Comprehensive coverage** — Create at least one happy path scenario, one rainy day scenario, and relevant edge cases that make sense for the feature.

4. **Readable and reusable** — Write clean, self-documenting code with minimal effort needed for maintenance. Use descriptive names, clear Gherkin, and well-structured step definitions.

5. **Consistency is key** — The code you generate should be indistinguishable from existing code in terms of style, patterns, and practices.

## Project Architecture

### Layer Structure

```
Feature Files (.feature)
        ↓
Step Definitions (@Given/@When/@Then)
        ↓
Service Layer (Business logic)
        ↓
REST Clients (BaseRestClient + specific) | UI Pages (BasePage<T> + specific)
        ↓
DSOs (Data Service Objects)              | Storage (Entity classes)
```

### Key Packages

```
src/main/java/com/levi9/functionaltests/
├── exceptions/           # FunctionalTestsException
├── rest/
│   ├── client/          # BaseRestClient, PetStoreRestClient, RestfulBookerRestClient
│   ├── data/            # DSO classes (request/response DTOs)
│   └── service/         # Service layer (@Component with business logic)
├── storage/             # Storage.java + Entity classes
│   ├── domain/
│   │   ├── petstore/    # PetEntity, OrderEntity
│   │   └── restfulbooker/ # RoomEntity
│   └── ScenarioEntity   # Test scenario metadata
├── ui/
│   ├── base/           # BaseDriver, BasePage<T>, Browser enum
│   ├── helpers/        # WaitHelper, ActionsHelper, UploadHelper
│   └── pages/          # Page Object classes
└── util/               # FakeUtil, FileUtil

src/test/java/com/levi9/functionaltests/
├── config/             # SpringConfig (@PropertySource, @ComponentScan)
├── hooks/              # Hooks.java (@Before/@After with ordering)
├── runners/            # DryRunRunnerIT (validation)
├── stepdefs/           # Step definitions by domain
│   ├── petstore/       # PetStepdef, StoreStepdef
│   └── restfulbooker/  # LoginStepdef, RoomManagementStepdef, etc.
└── typeregistry/       # Custom Cucumber parameter types

src/test/resources/features/
├── pet-store/          # Pet Store features
└── restful-booker-platform/ # Restful Booker features
    ├── admin-panel/
    └── front-page/
```

## Test Types

### REST API Tests (`@api`)

API tests use **REST Assured** via service layer pattern. Services are Spring `@Component` beans with `@Scope("cucumber-glue")`.

**Tags**: `@api`, plus domain tags like `@pet`, `@store`, `@room`, `@booking`, etc.

### UI Tests (`@ui`)

UI tests use **Selenium 4** via Page Object pattern. All pages extend `BasePage<T>` which provides wait-based interactions.

**Tags**: `@ui`, plus domain tags like `@login`, `@management`, `@booking`, `@contact`, etc.

### Tags Reference

| Tag | Purpose |
|-----|---------|
| `@api` | REST API test |
| `@ui` | UI/Selenium test |
| `@pet`, `@store`, `@login`, `@management`, `@room-management`, `@booking`, `@contact` | Domain/feature tags |
| `@blocker` | Highest severity — core functionality |
| `@critical` | High severity — important flows |
| `@normal` | Standard severity — expected behavior |
| `@minor` | Low severity — edge cases |
| `@sanity` | Sanity suite — quick smoke tests |
| `@bug` | Known bug — excluded from default runs |
| `@pdf`, `@html`, `@image` | Embedding type tags (for report artifacts) |

### Hooks (Lifecycle)

Hooks in `src/test/java/com/levi9/functionaltests/hooks/Hooks.java` manage test lifecycle:

```
@Before(order = 0)              → Scenario start (all tests) — sets scenario metadata
@Before(value = "@ui", order = 1) → WebDriver setup (UI only) — initialize, set timeouts, maximize
@After(value = "@ui", order = 3)   → Screenshot on failure (UI only)
@After(order = 2)                → Cleanup — deletes created rooms via API
@After(value = "@ui", order = 1)   → WebDriver teardown (UI only) — close browser
@After(order = 0)                → Scenario end logging (all tests)
```

**Key**: Cleanup hooks delete test data (rooms) automatically. No manual cleanup needed in tests.

## Common Patterns

### 1. Service Layer Pattern (REST API Tests)

Services encapsulate business logic, make REST calls, validate responses, and update Storage.

**Template**:
```java
@Slf4j
@Component
@Scope("cucumber-glue")
public class {Domain}Service {
    
    @Autowired
    private {System}RestClient client;
    
    @Autowired
    private Storage storage;
    
    public void performAction(String param) {
        log.info("Performing action with param: {}", param);
        
        // 1. Build request DSO
        {Action}{Resource}DSO requestBody = {Action}{Resource}DSO.builder()
            .field(param)
            .build();
        
        // 2. Call REST client
        Response response = client.post(requestBody, null, "/path/to/endpoint");
        
        // 3. Validate response
        assertThat(response.statusCode())
            .as("Status code should be 200")
            .isEqualTo(200);
        
        // 4. Extract response
        {Resource}DSO responseBody = response.as({Resource}DSO.class);
        
        // 5. Update storage
        {Resource}Entity entity = {Resource}Entity.builder()
            .id(responseBody.getId())
            .field(responseBody.getField())
            .build();
        
        storage.get{Resource}s().add(entity);
        
        log.info("{Resource} created with ID: {}", entity.getId());
    }
}
```

**Key points**:
- Always use `@Slf4j` for logging
- Use `@Autowired` for dependency injection
- Use `@Scope("cucumber-glue")` for scenario-scoped lifecycle
- Validate responses with AssertJ assertions using `.as()` for descriptive messages
- Update Storage with entities after successful operations
- Log important actions and results

### 2. Page Object Pattern (UI Tests)

Page objects extend `BasePage<T>` and use wait-based Selenium interactions.

**Template**:
```java
@Slf4j
@Component
@Scope("cucumber-glue")
public class {Page}Page extends BasePage<{Page}Page> {
    
    // Page locator for load/isLoaded checks
    private final By page = By.xpath("//*[@data-testid='{page}-header']");
    
    // Locators as private final fields
    private final By fieldInput = By.id("fieldId");
    private final By submitButton = By.cssSelector(".submit-btn");
    private final By successMessage = By.xpath("//div[@class='success']");
    private final By errorMessages = By.cssSelector("div.alert.alert-danger");
    
    protected {Page}Page(final BaseDriver baseDriver) {
        super(baseDriver);
    }
    
    /**
     * Checks if page is loaded.
     *
     * @return true if yes, otherwise false
     */
    public boolean isLoaded() {
        return isElementVisible(page, 5);
    }
    
    /**
     * Load Page.
     */
    public void load() {
        openPage(getRestfulBookerPlatformUrl() + "#/{path}", page);
    }
    
    /**
     * Fills the form with provided data. Null parameters skip the field (for negative tests).
     *
     * @param field field value, nullable for validation tests
     */
    public void fillForm(@Nullable final String field) {
        if (null != field) {
            waitAndSendKeys(fieldInput, field);
        }
    }
    
    /**
     * Clicks submit button
     */
    public void clickSubmit() {
        waitAndClick(submitButton);
        log.info("Clicked submit button");
    }
    
    /**
     * Checks if success message is displayed
     */
    public boolean isSuccessMessageDisplayed() {
        return isElementVisible(successMessage, 10);
    }
    
    /**
     * Get list of error messages displayed on the page.
     *
     * @return list of error message strings
     */
    public List<String> getErrorMessages() {
        return waitAndGetWebElement(errorMessages).findElements(By.cssSelector("p")).stream().map(WebElement::getText).toList();
    }
}
```

**Key points**:
- Extend `BasePage<T>` where `T` is the page class itself
- Constructor takes `BaseDriver`, calls `super(baseDriver)` — `@Autowired` on constructors is optional (Spring infers single-constructor injection)
- **Every page MUST have**: `isLoaded()` (calls `isElementVisible(pageLocator, 5)`) and `load()` (calls `openPage(url, pageLocator)`)
- Use `getRestfulBookerPlatformUrl()` for base URL (provided by BasePage via `@Value`)
- Use descriptive locator names as `private final By` fields
- Use wait-based methods from BasePage: `waitAndClick()`, `waitAndSendKeys()`, `waitAndSelectByValue()`, `waitAndGetText()`, `waitAndGetAttribute()`, etc.
- **Return type**: Use `void` for action methods (this project's convention, not fluent API)
- Use `@Nullable` on parameters for methods used in negative/validation tests (skip interaction when null)
- Error messages: retrieve via `findElements(By.cssSelector("p")).stream().map(WebElement::getText).toList()`
- Log actions for debugging

### 3. DSO (Data Service Object) Pattern

DSOs are request/response DTOs using Lombok.

**Template**:
```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class {Action}{Resource}DSO {
    
    @JsonProperty("field_name")
    private String fieldName;
    
    private Integer count;
    
    private List<String> items;
}
```

**Key points**:
- Use Lombok annotations for boilerplate reduction
- Use `@JsonProperty` when JSON field names differ from Java conventions
- Use `@Builder(toBuilder = true)` for immutability patterns

### 4. Entity Pattern

Entities represent test-side domain objects stored in Storage.

**Template**:
```java
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class {Resource}Entity {
    
    private Integer id;
    private String name;
    private {Status}Enum status;  // Use enums for status fields
    
    @Builder.Default
    private boolean deleted = false;
}
```

**Key points**:
- Use `@Builder.Default` for default values
- Use enums for status/type fields (not Strings)
- Include a `deleted` flag if the resource can be deleted

### 5. Storage Pattern

`Storage` is a Spring `@Component` with `@Scope("cucumber-glue")` that maintains test state. It uses Lombok `@Getter` to auto-generate getters — there are no explicit getter or add methods.

**Adding a new entity to Storage**:

1. Add a field: `private final List<{Resource}Entity> {resource}s = new ArrayList<>();`
2. Lombok `@Getter` auto-generates `get{Resource}s()` — do NOT add explicit getters.
3. Add convenience method:
```java
public {Resource}Entity getLast{Resource}() {
    return {resource}s.stream().reduce((first, last) -> last)
        .orElseThrow(() -> new FunctionalTestsException("Last {Resource} not found!"));
}
```

**How callers add entities** (no dedicated add methods exist):
```java
storage.get{Resource}s().add(entity);
```

### 6. Step Definitions

Step definitions are in `src/test/java/com/levi9/functionaltests/stepdefs/{system}/`.

This project uses **two step annotation styles**:

#### Style A: Cucumber Expressions (Restful Booker tests)

Used with `{string}`, `{int}`, and custom types like `{roomType}`, `{accessible}`. Gherkin uses **single quotes** for `{string}` values.

```java
@Slf4j
public class {Domain}Stepdef {
    
    @Autowired
    private Storage storage;
    
    @Autowired
    private {Domain}Service service;
    
    @Given("User has created {roomType} type {accessible} room {string} priced at {int} GBP with {string}")
    public void userCreatedRoom(final RoomType roomType, final boolean accessible, final String roomName, final int roomPrice, final String features) {
        log.info("Step: user created room '{}'", roomName);
        service.createRoom(roomName, roomType, accessible, Integer.toString(roomPrice), new RoomAmenities(features));
    }
}
```

Matching Gherkin (single quotes):
```gherkin
Given User has created Single type Accessible room '1408' priced at 50 GBP with 'WiFi, TV and Safe'
```

#### Style B: Regex patterns (Pet Store tests)

Used with `^...$` anchors, `"([^"]*)"` or `(.*)` for captures. Gherkin uses **double quotes** for string values.

```java
@Slf4j
public class {Domain}Stepdef {
    
    @Autowired
    private Storage storage;
    
    @Autowired
    private {Domain}Service service;
    
    @Given("^[Uu]ser add(?:s|ed) pet \"(.*)\" to the pet store$")
    public void addPet(final String petName) {
        log.info("Step: user adds pet '{}'", petName);
        service.addPetToStore(petName);
    }
    
    @Then("^[Ii]t (?:will be|is)? possible to sell it$")
    public void validatePossibleToSell() {
        log.info("Step: validate possible to sell");
        final PetEntity expectedPet = storage.getLastPet();
        final PetDSO actualPet = petService.getPet(expectedPet);
        assertThat(actualPet.getStatus()).as("Pet is not available!").isEqualTo(AVAILABLE.getValue());
    }
}
```

Matching Gherkin (double quotes):
```gherkin
Given User added pet "Beagle" to the pet store
```

**Both styles are equally valid.** Match the style of the system you're extending.

### 7. Custom Parameter Types

Define custom parameter types in `src/test/java/com/levi9/functionaltests/typeregistry/ParameterTypes.java`.

**Template**:
```java
@ParameterType("Value1|Value2|Value3")
public {Type} {type}(final String value) {
    return {Type}.getEnum(value);
}
```

**Note**: The method parameter is always `String` — the conversion to the target type happens inside the method body.

This allows Gherkin steps like: `When user creates a Single type room` where `Single` is auto-converted to `RoomType.SINGLE`.

### 8. FunctionalTestsException

The project's custom exception uses SLF4J-style `{}` placeholder formatting:

```java
throw new FunctionalTestsException("Order with ID {} not found!", orderId);
throw new FunctionalTestsException("Expected status {} but got {}", expectedStatus, actualStatus);
```

### 9. Soft Assertions

Use `assertSoftly` for multi-field validations — all assertions run even if early ones fail:

```java
assertSoftly(softly -> {
    softly.assertThat(actualRoom.getName()).as("Room Name is wrong!").isEqualTo(expectedName);
    softly.assertThat(actualRoom.getType()).as("Room Type is wrong!").isEqualTo(expectedType);
    softly.assertThat(actualRoom.getPrice()).as("Room Price is wrong!").isEqualTo(expectedPrice);
});
```

### 10. BaseRestClient Methods

All REST clients extend `BaseRestClient` which provides:

```java
Response post(Object requestBody, CookieFilter auth, String path)
Response put(Object requestBody, CookieFilter auth, String path)
Response get(CookieFilter auth, String path)
Response delete(CookieFilter auth, String path)
Response uploadFile(String filePath, CookieFilter auth, String path)
```

Pass `null` for `auth` when authentication is not needed.

### 11. Cucumber Alternative Text

Gherkin supports `validation/mandatory` syntax to match either word:

```gherkin
Then User will get validation/mandatory error message: 'Room name must be set'
```

This matches step definition: `@Then("User will get validation/mandatory error message: {string}")`

## Naming Conventions

| Artifact | Pattern | Example |
|----------|---------|---------|
| **Feature files** | `kebab-case.feature` | `room-management.feature` |
| **Step defs** | `{Domain}Stepdef` | `RoomManagementStepdef` |
| **Services** | `{Domain}Service` | `RoomService`, `PetService` |
| **REST clients** | `{System}RestClient` | `RestfulBookerRestClient` |
| **DSOs** | `{Action}{Resource}DSO` or `{Resource}DSO` | `CreateRoomDSO`, `PetDSO` |
| **Entities** | `{Resource}Entity` | `PetEntity`, `RoomEntity` |
| **Pages** | `{Page}Page` | `RoomsPage`, `AdminPage` |
| **Enums** | `{Type}` or `{Type}Enum` | `RoomType`, `PetStatus` |

## Workflows

### Creating a New REST API Test

**Step 1: Create or extend feature file**

Location: `src/test/resources/features/{system}/{domain}.feature`

```gherkin
@api @{domain}
Feature: {Domain} Management

  {Description of what the feature covers.}

  Background: {Descriptive background title}
    # Common setup if needed

  @blocker @sanity
  Scenario: User can create a {resource}
    Given user creates a {resource} with name "Test {Resource}"
    When user retrieves the {resource}
    Then the {resource} should have status "ACTIVE"
  
  @critical
  Scenario Outline: User can create multiple {resource}s
    Given user creates a {resource} with name "<name>"
    Then the {resource} should be created successfully
    
    Examples:
      | name      |
      | Resource1 |
      | Resource2 |
```

**Note**: Background sections should always have descriptive text (e.g., `Background: User has a pet in the store`).

**Step 2: Create step definition**

Location: `src/test/java/com/levi9/functionaltests/stepdefs/{system}/{Domain}Stepdef.java`

```java
@Slf4j
public class {Domain}Stepdef {
    
    @Autowired
    private Storage storage;
    
    @Autowired
    private {Domain}Service service;
    
    @Given("^user creates a {resource} with name \"([^\"]*)\"$")
    public void userCreates{Resource}(String name) {
        log.info("Step: user creates a {resource} with name '{}'", name);
        service.create{Resource}(name);
    }
    
    @When("^user retrieves the {resource}$")
    public void userRetrievesThe{Resource}() {
        log.info("Step: user retrieves the {resource}");
        {Resource}Entity entity = storage.getLast{Resource}();
        service.retrieve{Resource}(entity.getId());
    }
    
    @Then("^the {resource} should have status \"([^\"]*)\"$")
    public void the{Resource}ShouldHaveStatus(String status) {
        log.info("Step: the {resource} should have status '{}'", status);
        {Resource}Entity entity = storage.getLast{Resource}();
        
        assertThat(entity.getStatus().getValue())
            .as("{Resource} status should be {}", status)
            .isEqualTo(status);
    }
    
    @Then("^the {resource} should be created successfully$")
    public void the{Resource}ShouldBeCreatedSuccessfully() {
        log.info("Step: the {resource} should be created successfully");
        {Resource}Entity entity = storage.getLast{Resource}();
        
        assertThat(entity.getId())
            .as("{Resource} should have an ID")
            .isNotNull();
    }
}
```

**Step 3: Create service**

Location: `src/main/java/com/levi9/functionaltests/rest/service/{system}/{Domain}Service.java`

```java
@Slf4j
@Component
@Scope("cucumber-glue")
public class {Domain}Service {
    
    private static final String {RESOURCE}_PATH = "/api/{resources}";
    
    @Autowired
    private {System}RestClient client;
    
    @Autowired
    private Storage storage;
    
    public void create{Resource}(String name) {
        log.info("Creating {resource} with name: {}", name);
        
        Create{Resource}DSO requestBody = Create{Resource}DSO.builder()
            .name(name)
            .status({Status}.PENDING.getValue())
            .build();
        
        Response response = client.post(requestBody, null, {RESOURCE}_PATH);
        
        assertThat(response.statusCode())
            .as("Status code should be 201")
            .isEqualTo(201);
        
        {Resource}DSO responseBody = response.as({Resource}DSO.class);
        
        {Resource}Entity entity = {Resource}Entity.builder()
            .id(responseBody.getId())
            .name(responseBody.getName())
            .status({Status}.getEnum(responseBody.getStatus()))
            .build();
        
        storage.get{Resource}s().add(entity);
        
        log.info("{Resource} created successfully with ID: {}", entity.getId());
    }
    
    public void retrieve{Resource}(Integer id) {
        log.info("Retrieving {resource} with ID: {}", id);
        
        Response response = client.get(null, {RESOURCE}_PATH + "/" + id);
        
        assertThat(response.statusCode())
            .as("Status code should be 200")
            .isEqualTo(200);
        
        {Resource}DSO responseBody = response.as({Resource}DSO.class);
        
        // Update storage with retrieved data
        {Resource}Entity entity = storage.getLast{Resource}();
        entity.setName(responseBody.getName());
        entity.setStatus({Status}.getEnum(responseBody.getStatus()));
        
        log.info("Retrieved {resource}: {}", responseBody);
    }
}
```

**Step 4: Create DSOs**

Location: `src/main/java/com/levi9/functionaltests/rest/data/{system}/`

**Tip**: If the API has an OpenAPI spec, use the helper script to generate DSO templates:
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py generate-dso \
  src/main/resources/openapi/{system}/{domain}-open-api.json \
  {SchemaName} \
  com.levi9.functionaltests.rest.data.{system}
```

```java
// Create{Resource}DSO.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Create{Resource}DSO {
    private String name;
    private String status;
}

// {Resource}DSO.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class {Resource}DSO {
    private Integer id;
    private String name;
    private String status;
}
```

**Step 5: Create entity and add to Storage**

Location: `src/main/java/com/levi9/functionaltests/storage/domain/{system}/{Resource}Entity.java`

```java
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class {Resource}Entity {
    private Integer id;
    private String name;
    private {Status} status;
    
    @Builder.Default
    private boolean deleted = false;
}
```

Then update `Storage.java`:
```java
private final List<{Resource}Entity> {resource}s = new ArrayList<>();

// @Getter generates get{Resource}s() automatically — do NOT add explicit getter

public {Resource}Entity getLast{Resource}() {
    return {resource}s.stream().reduce((first, last) -> last)
        .orElseThrow(() -> new FunctionalTestsException("Last {Resource} not found!"));
}
```

Callers add entities via:
```java
storage.get{Resource}s().add(entity);
```

**Step 6: Verify step definitions are properly linked (dry-run)**

```bash
mvn test -Dtest=DryRunRunnerIT
```

This validates that all Gherkin steps have matching step definitions with correct regex patterns. Fix any "Undefined step" errors.

**Step 7: Run the test**

```bash
mvn clean verify -Dtags='@{domain}'
```

If the test fails, investigate the failure, fix the issue, and run again.

### Creating a New UI Test

**Step 1: Create feature file**

Location: `src/test/resources/features/{system}/{domain}.feature`

```gherkin
@ui @{domain}
Feature: {Domain} UI

  Background: User is on the {Page} Page
    Given user is on the {Page} Page
  
  @blocker @sanity
  Scenario: User can perform action via UI
    When user fills form with 'Test Data'
    And user clicks submit
    Then success message should be displayed
```

**Note**: UI feature files use Cucumber Expressions with **single quotes** for `{string}` values. Background sections should have descriptive text.

**Step 2: Create page object**

Location: `src/main/java/com/levi9/functionaltests/ui/pages/{system}/{Page}Page.java`

```java
@Slf4j
@Component
@Scope("cucumber-glue")
public class {Page}Page extends BasePage<{Page}Page> {
    
    private final By page = By.xpath("//*[@data-testid='{page}-header']");
    private final By formInput = By.id("input-id");
    private final By submitButton = By.cssSelector(".submit-btn");
    private final By successMessage = By.xpath("//div[@class='success']");
    
    protected {Page}Page(final BaseDriver baseDriver) {
        super(baseDriver);
    }
    
    /**
     * Checks if page is loaded.
     *
     * @return true if yes, otherwise false
     */
    public boolean isLoaded() {
        return isElementVisible(page, 5);
    }
    
    /**
     * Load Page.
     */
    public void load() {
        openPage(getRestfulBookerPlatformUrl() + "#/{path}", page);
    }
    
    /**
     * Fills the form with provided data
     */
    public void fillForm(final String data) {
        log.info("Filling form with: {}", data);
        waitAndSendKeys(formInput, data);
    }
    
    /**
     * Clicks the submit button
     */
    public void clickSubmit() {
        log.info("Clicking submit button");
        waitAndClick(submitButton);
    }
    
    /**
     * Checks if success message is displayed
     */
    public boolean isSuccessMessageDisplayed() {
        return isElementVisible(successMessage, 10);
    }
}
```

**Step 3: Create step definitions**

Location: `src/test/java/com/levi9/functionaltests/stepdefs/{system}/{Domain}Stepdef.java`

```java
@Slf4j
public class {Domain}Stepdef {
    
    @Autowired
    private {Page}Page page;
    
    @Autowired
    private BannerPage bannerPage;
    
    @Given("User is on the {Page} Page")
    public void userIsOnPage() {
        page.load();
        bannerPage.closeBanner();
        assertThat(page.isLoaded()).as("User is not on the {Page} Page!").isTrue();
        log.info("User is on {Page} Page");
    }
    
    @When("User fills form with {string}")
    public void userFillsForm(final String data) {
        log.info("Step: user fills form with '{}'", data);
        page.fillForm(data);
    }
    
    @When("User clicks submit")
    public void userClicksSubmit() {
        page.clickSubmit();
    }
    
    @Then("Success message should be displayed")
    public void successMessageShouldBeDisplayed() {
        assertThat(page.isSuccessMessageDisplayed()).as("Success message is not displayed!").isTrue();
        log.info("Success message is displayed");
    }
}
```

**Important**: Always inject `BannerPage` and call `bannerPage.closeBanner()` after loading any Restful Booker page — the welcome banner blocks interactions.

**Step 4: Verify step definitions (dry-run)**

```bash
mvn test -Dtest=DryRunRunnerIT
```

**Step 5: Run the test**

```bash
mvn clean verify -Dtags='@{domain} and @ui'
```

### Testing Error Handling and Validation

When creating tests for error scenarios and validation:

**Negative API Tests**:
```gherkin
@api @validation @{domain}
Feature: {Domain} Validation

  @critical
  Scenario Outline: API rejects invalid {resource} data
    When user attempts to create a {resource} with <field> "<value>"
    Then the API should return status code <statusCode>
    And the error message should contain "<errorText>"
    
    Examples:
      | field       | value           | statusCode | errorText        |
      | empty name  |                 | 400        | name is required |
      | invalid status | INVALID_STATUS | 400        | invalid status   |
      | null ID     | null            | 400        | ID cannot be null|
```

**Service layer error handling**:
```java
public void createResourceWithInvalidData(String field, String value) {
    log.info("Attempting to create resource with invalid {}: {}", field, value);
    
    // Build invalid request
    RequestDSO request = buildInvalidRequest(field, value);
    
    try {
        Response response = client.post(request, null, RESOURCE_PATH);
        
        // For negative tests, expect 400
        if (response.statusCode() == HttpStatus.SC_BAD_REQUEST) {
            log.info("Received expected 400 error: {}", response.body().asString());
            storage.setLastErrorResponse(response);  // Store for assertion
        } else {
            throw new FunctionalTestsException(
                "Expected 400 Bad Request but got {}", response.statusCode());
        }
    } catch (Exception e) {
        log.error("Error during invalid request: {}", e.getMessage());
        throw new FunctionalTestsException("Failed to handle invalid request: {}", e.getMessage());
    }
}
```

**Step definition for error cases**:
```java
@When("^user attempts to create a {resource} with (.*) \"([^\"]*)\"$")
public void userAttemptsInvalidCreate(String field, String value) {
    log.info("Step: user attempts to create {resource} with {} '{}'", field, value);
    service.createResourceWithInvalidData(field, value);
}

@Then("^the API should return status code (\\d+)$")
public void apiShouldReturnStatusCode(int expectedStatus) {
    log.info("Step: verifying API returned status code {}", expectedStatus);
    Response errorResponse = storage.getLastErrorResponse();
    
    assertThat(errorResponse.statusCode())
        .as("API should return status code {}", expectedStatus)
        .isEqualTo(expectedStatus);
}

@Then("^the error message should contain \"([^\"]*)\"$")
public void errorMessageShouldContain(String expectedText) {
    log.info("Step: verifying error message contains '{}'", expectedText);
    Response errorResponse = storage.getLastErrorResponse();
    String errorBody = errorResponse.body().asString();
    
    assertThat(errorBody.toLowerCase())
        .as("Error message should contain '{}'", expectedText)
        .contains(expectedText.toLowerCase());
}
```

**UI validation testing**:
```gherkin
@ui @validation @{domain}
Feature: {Page} Form Validation

  Background:
    Given user is on {page} page
  
  @critical
  Scenario: Required fields show validation errors
    When user clicks submit without filling required fields
    Then validation error "Name is required" should be displayed
    And validation error "Email is required" should be displayed
    And the form should not be submitted
```

**Page object validation methods**:
```java
public void clickSubmitWithoutFilling() {
    log.info("Clicking submit without filling required fields");
    waitAndClick(submitButton);
}

public boolean isValidationErrorDisplayed(String errorMessage) {
    log.info("Checking if validation error '{}' is displayed", errorMessage);
    By errorLocator = By.xpath(String.format("//span[contains(text(), '%s')]", errorMessage));
    return isElementDisplayed(errorLocator);
}
```

### Debugging Failing Tests

When a test fails, follow this systematic approach:

1. **Read the error message carefully** — Look at the stack trace, assertion failure message, or Selenium error.

2. **Check logs** — Examine `logs/functional-tests-{user}.{date}.log` for detailed logging.

3. **Review screenshots** — For UI tests, check `target/test-artifacts/{ScenarioName}/screenshots/` for failure screenshots.

4. **Run dry-run** — Verify step definitions are properly linked:
   ```bash
   mvn test -Dtest=DryRunRunnerIT
   ```

5. **Common issues**:
   - **Undefined steps**: Step definition regex doesn't match Gherkin
   - **NullPointerException**: Storage entity not created or autowiring failed
   - **Assertion failure**: Response/UI state doesn't match expectation
   - **Timeout**: Element not found (UI) or slow response (API)
   - **404/500 errors**: Wrong endpoint path or server issue

6. **Fix and verify** — After fixing, run the specific test again:
   ```bash
   mvn clean verify -Dtags='@{specific-tag}'
   ```

### Code Review Checklist

When reviewing test code, verify:

**Feature Files**:
- [ ] Scenarios have appropriate tags (`@api`/`@ui`, severity `@blocker/@critical/@normal/@minor`, domain)
- [ ] Background is used for common setup with descriptive text
- [ ] Scenario Outlines are used for data-driven tests
- [ ] Gherkin is readable and follows Given/When/Then structure
- [ ] No implementation details in Gherkin (keep it business-focused)
- [ ] String quoting matches step style: single quotes for Cucumber Expressions, double for regex

**Step Definitions**:
- [ ] Uses `@Slf4j` and logs each step
- [ ] Annotation style matches the system (Cucumber Expressions for Restful Booker, regex for Pet Store)
- [ ] Delegates to services (API) or pages (UI) — stepdefs should be thin orchestrators
- [ ] Assertions use AssertJ with `.as()` descriptions; `assertSoftly` for multi-field checks
- [ ] Parameters are properly captured and typed

**Services** (API):
- [ ] Annotated with `@Component` and `@Scope("cucumber-glue")`
- [ ] Uses `@Autowired` for dependencies
- [ ] Validates response status codes
- [ ] Updates Storage after operations
- [ ] Logs important actions and results
- [ ] Constants for endpoint paths

**Page Objects** (UI):
- [ ] Extends `BasePage<T>` with self-type
- [ ] Constructor takes `BaseDriver`, calls `super(baseDriver)`
- [ ] Has `load()` and `isLoaded()` methods
- [ ] Locators are `private final By` fields with descriptive names
- [ ] Uses wait-based methods (`waitAndClick`, `waitAndSendKeys`, etc.)
- [ ] Action methods return `void` (project convention — not fluent API)
- [ ] Verification methods return `boolean` or specific types
- [ ] Uses `@Nullable` on parameters for negative/validation test methods
- [ ] Assertions done in step definitions, not page objects

**DSOs**:
- [ ] Uses Lombok annotations correctly
- [ ] `@JsonProperty` for non-standard field names
- [ ] `@Builder(toBuilder = true)` for immutability

**Entities**:
- [ ] Uses enums for status/type fields (not Strings)
- [ ] Includes `deleted` flag if applicable
- [ ] Uses `@Builder.Default` for defaults

**General**:
- [ ] Consistent naming conventions
- [ ] No code duplication
- [ ] Proper package organization
- [ ] No hardcoded values (use properties or constants)
- [ ] Error handling where appropriate

## Running Tests

### Basic Execution

```bash
# Run all sanity tests (default)
mvn clean verify

# Run specific tags
mvn clean verify -Dtags='@api'
mvn clean verify -Dtags='@ui'
mvn clean verify -Dtags='@pet'

# Complex tag expressions
mvn clean verify -Dtags='(@ui or @api) and (not @skip and not @bug)'
mvn clean verify -Dtags='@management and not @room-management'
```

### Parallelization

```bash
# Run with 5 scenarios in parallel
mvn clean verify -DparallelCount=5

# Run with 10 scenarios in parallel
mvn clean verify -DparallelCount=10
```

### Environment Selection

```bash
# Use development environment
mvn clean verify -Denv=development

# Use staging environment
mvn clean verify -Denv=staging
```

### Browser Configuration (UI tests)

```bash
# Run with Chrome (default)
mvn clean verify -Dbrowser=chrome

# Run with Firefox
mvn clean verify -Dbrowser=firefox

# Run in headless mode
mvn clean verify -Dheadless

# Run on Selenium Grid
mvn clean verify -Dremote=true -DremoteUrl=http://localhost:4444/wd/hub
```

### Combined Example

```bash
mvn clean verify \
  -Dtags='(@api or @ui) and @sanity and not @bug' \
  -DparallelCount=5 \
  -Denv=development \
  -Dbrowser=chrome \
  -Dheadless \
  -Dremote=false
```

### Viewing Reports

After test execution, view reports:
- **Cluecumber**: `target/cucumber/cluecumber-report/index.html`
- **Cucumber HTML**: `target/cucumber/cucumber-html-reports/overview-features.html`
- **Allure**: Run `allure serve target/cucumber/allure-results`

## OpenAPI Model Generation

This project uses OpenAPI Generator to create model classes from OpenAPI specifications.

### OpenAPI Helper Script

The skill includes a Python script (`scripts/openapi_helper.py`) to help work with OpenAPI specifications during test development.

**List all available endpoints:**
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py list-endpoints \
  src/main/resources/openapi/restfulbooker/room-open-api.json
```
This displays a table of all API endpoints with their methods, paths, operation IDs, and tags.

**Get endpoint details:**
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py endpoint-details \
  src/main/resources/openapi/restfulbooker/room-open-api.json \
  /room/{id}
```
This shows detailed information about a specific endpoint: parameters, request body schemas, response codes, and descriptions.

**Generate DSO template:**
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py generate-dso \
  src/main/resources/openapi/restfulbooker/room-open-api.json \
  Room \
  com.levi9.functionaltests.rest.data.restfulbooker
```
This generates a complete Java DSO class with:
- Lombok annotations (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
- Correct Java types based on OpenAPI schema (String, Integer, List, etc.)
- `@JsonProperty` annotations for non-standard field names
- JavaDoc comments from OpenAPI descriptions

Use this when creating new DSOs — it ensures consistency with the OpenAPI spec and saves time.

### Adding New OpenAPI Specs

1. **Place spec file** in `src/main/resources/openapi/{system}/{domain}-open-api.json`

2. **Add execution** in `pom.xml`:
```xml
<execution>
    <id>generate-{domain}-client</id>
    <goals>
        <goal>generate</goal>
    </goals>
    <configuration>
        <inputSpec>${project.basedir}/src/main/resources/openapi/{system}/{domain}-open-api.json</inputSpec>
        <generatorName>java</generatorName>
        <output>${project.build.directory}/generated-sources/generated</output>
        <modelPackage>{system}.model.{domain}</modelPackage>
        <generateApis>false</generateApis>
        <generateModels>true</generateModels>
        <configOptions>
            <sourceFolder>.</sourceFolder>
            <library>rest-assured</library>
            <useJakartaEe>true</useJakartaEe>
            <additionalModelTypeAnnotations>@lombok.experimental.SuperBuilder</additionalModelTypeAnnotations>
        </configOptions>
    </configuration>
</execution>
```

3. **Generate models**:
```bash
mvn clean compile
```

4. **Mark as sources** in IntelliJ:
- Right-click `target/generated-sources/generated`
- Select "Mark Directory as" → "Generated Sources Root"

## Best Practices Summary

1. **Always run tests after creation/modification** — Verify they work, fix if they fail.

2. **Study existing code first** — Find similar tests and match their patterns exactly.

3. **Use descriptive names** — Make code self-documenting.

4. **Log everything** — Use `@Slf4j` and log steps, actions, and important data.

5. **Assertions with descriptions** — Use `.as("description")` in AssertJ assertions. Use `assertSoftly` for multi-field checks.

6. **Update Storage consistently** — Every create/update operation should call `storage.get{Resource}s().add(entity)`.

7. **Wait-based UI interactions** — Always use `waitAndClick()`, `waitAndSendKeys()`, etc. for stability. Never use `Thread.sleep()`.

8. **Page Object conventions** — Every page needs `load()` and `isLoaded()`. Use `void` for action methods, `boolean`/specific types for verification methods. Use `@Nullable` for validation test parameters.

9. **Close the banner** — Always call `bannerPage.closeBanner()` after loading a Restful Booker page.

10. **Verify with dry-run** — Always run `mvn test -Dtest=DryRunRunnerIT` to validate step definitions before running full tests.

11. **Comprehensive coverage** — Happy path + rainy day + edge cases.

12. **Clean Gherkin** — Business-focused, no implementation details. Use descriptive Background text.

13. **Match step annotation style** — Use Cucumber Expressions (with single-quoted strings) for Restful Booker, regex (with double-quoted strings) for Pet Store.

14. **Minimal effort maintenance** — Write code that's easy to understand and modify.

## When to Use This Skill

Use this skill whenever working with tests in this project:

- Writing new Cucumber feature files
- Creating Gherkin scenarios
- Writing step definitions
- Building REST API tests (services, REST clients, DSOs)
- Building UI tests (page objects, Selenium interactions)
- Debugging failing tests
- Fixing test failures
- Refactoring test code
- Reviewing test code for quality
- Adding test coverage
- Generating test data
- Creating entities and updating Storage
- Adding custom parameter types
- Extending REST clients
- Working with OpenAPI models
- Improving test architecture

Even if the user just mentions "write a test", "add coverage", "this test is failing", or "review this test", trigger this skill immediately.

## Final Note

The goal is to create tests that are **indistinguishable from existing code** in terms of quality, style, and patterns. Always prioritize consistency, readability, and reusability. When in doubt, look at existing similar tests and follow their exact approach.
