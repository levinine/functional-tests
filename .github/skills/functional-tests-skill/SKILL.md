---
name: functional-tests-skill
description: Expert testing skill for the Levi9 functional-tests project — a Java 23 / Maven / Cucumber BDD framework using REST Assured for API tests and Selenium 4 for UI tests with Spring DI. Use this skill whenever the user wants to write, create, add, extend, debug, review, or refactor any kind of test in this project. Trigger when the user mentions writing tests, adding test coverage, creating feature files, Gherkin scenarios, step definitions, stepdefs, REST API tests, UI tests, Selenium, page objects, debugging failing tests, fixing test failures, test architecture improvements, generating test data, reviewing test code, fetching Jira tickets, fetching GitHub issues, creating tests from Jira/GitHub/board tickets, or anything related to testing in this Cucumber/REST Assured/Selenium codebase. Even if the user just says "write a test" or "add coverage for X" or "this test is failing" or "write tests for PROJ-123" or "write tests for owner/repo#42", this skill should activate immediately.
---

# Functional Tests Skill

This skill guides you in working with the **Levi9 functional-tests** project — a Java 23 / Maven / Cucumber BDD framework that tests two systems:
- **Pet Store API** (REST API tests only) — uses hand-crafted DSO classes for request/response bodies
- **Restful Booker Platform** (REST API + UI tests) — uses OpenAPI-generated models from `restfulbooker.model.*` for request/response bodies, plus a few hand-crafted DSOs

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
DSOs / OpenAPI Models                    | Storage (Entity classes)
```

### Key Packages

```
src/main/java/com/levi9/functionaltests/
├── exceptions/           # FunctionalTestsException
├── rest/
│   ├── client/          # BaseRestClient, PetStoreRestClient, RestfulBookerRestClient, RandomDogRestClient
│   ├── data/            # DSO classes, enums, helper classes
│   │   ├── petstore/    # PetDSO, OrderDSO, CategoryDSO, TagDSO, MessageDSO, PetStatus, OrderStatus
│   │   ├── restfulbooker/ # BookingsDSO, RoomAmenities, RoomType
│   │   └── randomdogimage/ # RandomDogImageDSO
│   └── service/         # Service layer (@Component with business logic)
│       ├── petstore/    # PetService, StoreOrderService
│       ├── restfulbooker/ # AuthService, BookingService, RoomService
│       └── randomdogimage/ # RandomDogImageService
├── storage/             # Storage.java + Entity classes
│   ├── ScenarioEntity   # Test scenario metadata + embed helpers
│   └── domain/
│       ├── petstore/    # PetEntity, OrderEntity
│       └── restfulbooker/ # RoomEntity
├── ui/
│   ├── base/           # BaseDriver, BaseDriverListener, BasePage<T>, Browser enum
│   ├── helpers/        # WaitHelper, ActionsHelper, UploadHelper
│   └── pages/
│       └── restfulbooker/ # AdminPage, BannerPage, FrontPage, HeaderPage, RoomsPage
└── util/               # FakeUtil (random emails/phones), FileUtil

src/test/java/com/levi9/functionaltests/
├── config/             # SpringConfig (@PropertySource, @ComponentScan)
├── hooks/              # Hooks.java (@Before/@After with ordering)
├── runners/            # DryRunRunnerIT (validation)
├── stepdefs/           # Step definitions by domain
│   ├── petstore/       # PetStepdef, StoreStepdef
│   └── restfulbooker/  # LoginStepdef, RoomManagementStepdef, BookingStepdef, ContactStepdef
└── typeregistry/       # ParameterTypes.java — custom Cucumber parameter types

src/test/resources/features/
├── pet-store/                          # Pet Store features
│   ├── pet.feature
│   └── store.feature
└── restful-booker-platform/            # Restful Booker features
    ├── admin-panel/
    │   ├── login.feature
    │   └── room-management.feature
    └── front-page/
        ├── book-a-room.feature
        ├── book-a-room-invalid-validation.feature
        ├── contact-hotel.feature
        └── contact-hotel-invalid-validation.feature
```

### Generated Models vs Hand-Crafted DSOs

This is a critical distinction:

**Pet Store** uses hand-crafted DSOs in `com.levi9.functionaltests.rest.data.petstore`:
```java
PetDSO, OrderDSO, CategoryDSO, TagDSO, MessageDSO
```

**Restful Booker** uses OpenAPI-generated models in `restfulbooker.model.*`:
```java
restfulbooker.model.room.Room, Rooms
restfulbooker.model.auth.Auth, Token
restfulbooker.model.booking.Booking, Bookings
// etc.
```
Plus a few hand-crafted classes in `com.levi9.functionaltests.rest.data.restfulbooker`:
```java
BookingsDSO, RoomAmenities, RoomType
```

When creating new Restful Booker tests, prefer using the generated models for API request/response bodies. When creating Pet Store tests, create hand-crafted DSOs.

## Test Types

### REST API Tests (`@api`)

API tests use **REST Assured** via service layer pattern. Services are Spring `@Component` beans with `@Scope("cucumber-glue")`.

**Tags**: `@api`, plus domain tags like `@pet`, `@store`, `@room`, `@booking`, etc.

### UI Tests (`@ui`)

UI tests use **Selenium 4** via Page Object pattern. All pages extend `BasePage<T>` which provides wait-based interactions.

**Tags**: `@ui`, plus domain tags like `@login`, `@management`, `@room-management`, `@booking`, `@contact`, etc.

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

Services encapsulate business logic, make REST calls, validate responses, and update Storage. Services use **constructor injection** (not field `@Autowired`) and **throw `FunctionalTestsException`** on bad status codes (not AssertJ assertions).

**Real example from PetService:**
```java
@Slf4j
@Component
@Scope("cucumber-glue")
public class PetService {

    public static final String REST_PATH = "v2/pet/";

    private final PetStoreRestClient petStoreRestClient;
    private final Storage storage;

    @Autowired
    public PetService(final PetStoreRestClient petStoreRestClient, final Storage storage) {
        this.petStoreRestClient = petStoreRestClient;
        this.storage = storage;
    }

    public void addPetToStore(final String petName) {
        final PetDSO body = PetDSO.builder()
            .id(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE))
            .name(petName)
            .status(PENDING.getValue())
            .build();

        final Response response = petStoreRestClient.post(body, null, REST_PATH);
        if (response.statusCode() != HttpStatus.SC_OK) {
            throw new FunctionalTestsException("Pet can not be added. Expected {}, but actual {}. Response message: {}",
                HttpStatus.SC_OK, response.statusCode(), response.getBody().prettyPrint());
        }

        final PetEntity pet = PetEntity.builder()
            .id(body.getId())
            .name(body.getName())
            .status(PENDING)
            .build();

        storage.getPets().add(pet);
    }
}
```

**Real example from RoomService (uses OpenAPI generated model):**
```java
@Slf4j
@Component
@Scope("cucumber-glue")
public class RoomService {

    public static final String REST_PATH = "room/";

    private final RestfulBookerRestClient restfulBookerRestClient;
    private final BookingService bookingService;
    private final Storage storage;

    @Autowired
    public RoomService(final RestfulBookerRestClient restfulBookerRestClient, final BookingService bookingService, final Storage storage) {
        this.restfulBookerRestClient = restfulBookerRestClient;
        this.bookingService = bookingService;
        this.storage = storage;
    }

    public void createRoom(final String roomName, final RoomType roomType, final boolean accessible,
        final String roomPrice, final RoomAmenities roomAmenities) {

        // Uses OpenAPI-generated Room model, not a hand-crafted DSO
        final Room body = Room.builder()
            .roomName(roomName)
            .roomPrice(Integer.parseInt(roomPrice))
            .type(roomType.getValue())
            .description("Created with Java Cucumber E2E Test Automation Framework")
            .accessible(accessible)
            .features(roomAmenities.getAmenitiesAsList())
            .image(getImageUrl(roomType))
            .build();

        final Response response = restfulBookerRestClient.post(body, null, REST_PATH);
        if (response.statusCode() != HttpStatus.SC_CREATED) {
            throw new FunctionalTestsException("Room can not be created. Expected {}, but actual {}. Response message: {}",
                HttpStatus.SC_OK, response.statusCode(), response.getBody().prettyPrint());
        }

        final Room createdRoom = response.as(Room.class);
        final RoomEntity roomEntity = new RoomEntity(body);
        roomEntity.setRoomId(createdRoom.getRoomid());

        storage.getRooms().add(roomEntity);
    }
}
```

**Key patterns for services:**
- Always use `@Slf4j`, `@Component`, `@Scope("cucumber-glue")`
- **Constructor injection** with `@Autowired` on constructor, storing dependencies in `private final` fields
- **Throw `FunctionalTestsException`** on unexpected status codes — do NOT use AssertJ assertions in services
- Use `HttpStatus.SC_OK`, `HttpStatus.SC_CREATED`, `HttpStatus.SC_ACCEPTED`, etc. from `org.apache.http.HttpStatus`
- Use `response.getBody().prettyPrint()` in exception messages for debugging
- `public static final String REST_PATH` constant for endpoint base path
- Update Storage with entities after successful operations
- Log important actions and results

### 2. Page Object Pattern (UI Tests)

Page objects extend `BasePage<T>` and use wait-based Selenium interactions.

**Real example from AdminPage:**
```java
@Slf4j
@Component
@Scope("cucumber-glue")
public class AdminPage extends BasePage<AdminPage> {

    private final By page = By.xpath("//*[@data-testid='login-header']");
    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("doLogin");

    protected AdminPage(final BaseDriver baseDriver) {
        super(baseDriver);
    }

    public boolean isLoaded() {
        return isElementVisible(page, 5);
    }

    public void load() {
        openPage(getRestfulBookerPlatformUrl() + "#/admin", page);
    }

    public void login(final String username, final String password) {
        waitAndSendKeys(usernameField, username);
        waitAndSendKeys(passwordField, password);
        waitAndClick(loginButton);
        log.info("Login via UI using username: '{}' and password '{}'", username, password);
    }
}
```

**Key points**:
- Extend `BasePage<T>` where `T` is the page class itself
- Constructor takes `BaseDriver`, calls `super(baseDriver)` — `@Autowired` on constructors is optional (Spring infers single-constructor injection)
- **Every page MUST have**: `isLoaded()` (calls `isElementVisible(pageLocator, 5)`) and `load()` (calls `openPage(url, pageLocator)`)
- Use `getRestfulBookerPlatformUrl()` for base URL (provided by BasePage via `@Value`)
- Use descriptive locator names as `private final By` fields
- Use wait-based methods from BasePage: `waitAndClick()`, `waitAndSendKeys()`, `waitAndSelectByValue()`, `waitAndSelectByVisibleText()`, `waitAndSelectByIndex()`, `waitAndGetText()`, `waitAndGetAttribute()`, `waitAndGetWebElement()`, etc.
- **Return type**: Use `void` for action methods (this project's convention, not fluent API)
- Use `@Nullable` (from `javax.annotation.Nullable`) on parameters for methods used in negative/validation tests (skip interaction when null)
- Error messages: retrieve via `findElements(By.cssSelector("p")).stream().map(WebElement::getText).toList()`
- Log actions for debugging
- For drag-and-drop use `getActionsHelper().dragAndDrop(fromElement, toElement)` (from BasePage)

### 3. BaseRestClient Method Signatures

All REST clients extend `BaseRestClient`. The **actual** method signatures are:

```java
Response post(Object body, Map<String, String> parameters, String path)
Response put(Object body, Map<String, String> parameters, String path)
Response get(Map<String, String> parameters, String path)
Response delete(Map<String, String> parameters, String path)
Response uploadFile(File file, Map<String, String> parameters, String path)
```

Pass `null` for `parameters` when query parameters are not needed. Pass `null` for `body` when no request body is needed.

**Example with query parameters (from BookingService):**
```java
final Map<String, String> parameters = new HashMap<>();
parameters.put("roomId", Integer.toString(room.getRoomId()));
final Response response = restfulBookerRestClient.get(parameters, REST_PATH);
```

**Example without parameters (most common):**
```java
final Response response = petStoreRestClient.post(body, null, REST_PATH);
final Response response = petStoreRestClient.get(null, REST_PATH + pet.getId());
final Response response = petStoreRestClient.delete(null, REST_PATH + pet.getId());
```

### 4. REST Client Pattern

REST clients are simple wrappers that extend `BaseRestClient` and pass the base URL from properties:

```java
@Component
@Scope("cucumber-glue")
public class PetStoreRestClient extends BaseRestClient {

    public PetStoreRestClient(@Value("${pet-store.url}") final String serviceUrl) {
        super(serviceUrl);
    }
}
```

Base URLs are defined in `src/test/resources/application-{env}.properties`:
```properties
restful-booker-platform.url=http://localhost/
pet-store.url=https://petstore.swagger.io/
random.dog.url=https://random.dog/
```

### 5. DSO (Data Service Object) Pattern

DSOs are request/response DTOs using Lombok. Used for Pet Store and some Restful Booker classes.

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

### 6. Enum Pattern

Enums follow a consistent pattern with a `String` value and a `getEnum()` factory method:

```java
public enum RoomType {
    SINGLE("Single"),
    TWIN("Twin"),
    DOUBLE("Double"),
    FAMILY("Family"),
    SUITE("Suite");

    @Getter(AccessLevel.PUBLIC)
    private final String value;

    RoomType(final String value) {
        this.value = value;
    }

    public static RoomType getEnum(final String value) {
        for (final RoomType roomType : values()) {
            if (roomType.getValue().equals(value)) {
                return roomType;
            }
        }
        throw new FunctionalTestsException("Room Type Enum with value {} not found!", value);
    }
}
```

### 7. Entity Pattern

Entities represent test-side domain objects stored in Storage.

**Pet Store entities use builder pattern:**
```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PetEntity {

    private Integer id;
    private CategoryDSO category;
    private String name;
    private List<String> photoUrls;
    private List<TagDSO> tags;
    private PetStatus status;
    @Default
    private boolean deleted = false;
}
```

**Restful Booker entities may have a constructor from generated model:**
```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RoomEntity {

    private Integer roomId;
    private String roomName;
    private Integer roomPrice;
    private String description;
    private RoomType type;
    private Boolean accessible;
    private String image;
    private RoomAmenities amenities;

    // Constructor from OpenAPI-generated model
    public RoomEntity(final Room room) {
        this.roomId = room.getRoomid();
        this.roomName = room.getRoomName();
        // ... mapping from generated model fields
    }
}
```

### 8. Storage Pattern

`Storage` is a Spring `@Component` with `@Scope("cucumber-glue")` that maintains test state. It uses Lombok `@Getter` to auto-generate getters — there are no explicit getter or add methods.

**Current Storage fields:**
```java
@Getter
@Component
@Scope("cucumber-glue")
public class Storage {

    // Test Scenario
    private final ScenarioEntity testScenario = new ScenarioEntity();

    // Used for REST API tests
    private final List<PetEntity> pets = new ArrayList<>();
    private final List<OrderEntity> orders = new ArrayList<>();

    // Used for UI tests
    private final List<RoomEntity> rooms = new ArrayList<>();

    public PetEntity getLastPet() {
        return pets.stream().reduce((first, last) -> last)
            .orElseThrow(() -> new FunctionalTestsException("Last Pet not found!"));
    }

    public OrderEntity getLastOrder() { /* same pattern */ }
    public RoomEntity getLastRoom() { /* same pattern */ }
}
```

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

### 9. ScenarioEntity — Embedding Artifacts

`ScenarioEntity` provides methods to embed artifacts into Cucumber reports. Step definitions call these to attach PDFs, images, or HTML:

```java
storage.getTestScenario().embedPdfToScenario();    // Embeds a static dummy PDF
storage.getTestScenario().embedPicture(imageUrl);   // Embeds an image from URL
storage.getTestScenario().embedHtml(htmlContent);   // Embeds HTML content
```

These are used in stepdefs with the `@pdf`, `@image`, `@html` tags to demonstrate report embedding.

### 10. Step Definitions

Step definitions are in `src/test/java/com/levi9/functionaltests/stepdefs/{system}/`.

This project uses **two step annotation styles**:

#### Style A: Cucumber Expressions (Restful Booker tests)

Used with `{string}`, `{int}`, and custom types like `{roomType}`, `{accessible}`. Gherkin uses **single quotes** for `{string}` values. Supports alternative text with `/` (e.g., `Validation/Mandatory`).

```java
@Slf4j
public class RoomManagementStepdef {

    @Autowired
    private Storage storage;

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomsPage roomsPage;

    @Autowired
    private BannerPage bannerPage;

    @Given("User has created {roomType} type {accessible} room {string} priced at {int} GBP with {string}")
    public void userCreatedRoom(final RoomType roomType, final boolean accessible, final String roomName, final int roomPrice, final String features) {
        final RoomAmenities roomAmenities = new RoomAmenities(features);
        roomService.createRoom(roomName, roomType, accessible, Integer.toString(roomPrice), roomAmenities);
    }

    @Then("User will get validation/mandatory error message: {string}")
    public void assertValidationOrMandatoryErrorMessage(final String message) {
        assertThat(roomsPage.getValidationOrMandatoryErrorMessages()).as("Message '" + message + "' is not displayed!").contains(message);
        log.info("Room Creation Validation / Mandatory Error Message '{}' is displayed", message);
    }
}
```
 
Matching Gherkin (single quotes):
```gherkin
Given User has created Single type Accessible room '1408' priced at 50 GBP with 'WiFi, TV and Safe'
Then User will get mandatory error message: 'Room name must be set'
```

**Cucumber Expression alternative text**: Use `/` to match either word in Gherkin. Example:
- Step: `@Then("Visitor will get Booking Validation/Mandatory Error Message: {string}")`
- Gherkin: `Then Visitor will get Booking Validation Error Message: 'some error'`
- Or: `Then Visitor will get Booking Mandatory Error Message: 'some error'`

**Cucumber Expression optional text**: Use `(invalid )` (with space inside parentheses) to optionally match text:
- Step: `@When("Visitor {string} {string} with an (invalid )email {string} and phone number {string} tries to book a room {string}")`
- Matches both: `with an email 'x@y.com'` and `with an invalid email 'bad'`

#### Style B: Regex patterns (Pet Store tests)

Used with `^...$` anchors, `"([^"]*)"` or `(.*)` for captures. Gherkin uses **double quotes** for string values.

```java
@Slf4j
public class PetStepdef {

    @Autowired
    private Storage storage;

    @Autowired
    private PetService petService;

    @Given("^[Uu]ser add(?:s|ed) pet \"(.*)\" to the pet store$")
    public void addPet(final String petName) {
        petService.addPetToStore(petName);
        log.info("Pet " + petName + " added to the store.");
    }

    @When("^[Pp]et status is set to \"(available|pending|sold)\"$")
    public void setPetStatus(final String petStatus) {
        final PetEntity pet = storage.getLastPet();
        final PetStatus status = PetStatus.getEnum(petStatus);
        petService.updatePetStatus(pet, status);
        log.info("Pet status is set to " + petStatus);
    }

    @Then("^[Ii]t (?:will be|is)? possible to sell it$")
    public void validatePossibleToSell() {
        final PetEntity expectedPet = storage.getLastPet();
        final PetDSO actualPet = petService.getPet(expectedPet);
        assertThat(actualPet.getStatus()).as("Pet is not available!").isEqualTo(AVAILABLE.getValue());
    }
}
```

**Both styles are equally valid.** Match the style of the system you're extending.

**Pet Store regex patterns**: Use `[Uu]`, `[Pp]`, `[Ii]`, `[Oo]`, `[Rr]` for case-insensitive first letter. Use `(?:s|ed)` for tense flexibility. Use `(?:will be|is)?` for assertion flexibility.

### 11. Custom Parameter Types

Define custom parameter types in `src/test/java/com/levi9/functionaltests/typeregistry/ParameterTypes.java`.

**Current types:**
```java
public class ParameterTypes {

    @ParameterType("Single|Twin|Double|Family|Suite")
    public RoomType roomType(final String roomType) {
        return RoomType.getEnum(roomType);
    }

    @ParameterType("Accessible|Not Accessible")
    public boolean accessible(final String accessible) {
        return !accessible.toLowerCase().contains("not");
    }
}
```

This allows Gherkin steps like: `When user creates a Single type Accessible room` where `Single` → `RoomType.SINGLE` and `Accessible` → `true`.

### 12. FunctionalTestsException

The project's custom exception uses SLF4J-style `{}` placeholder formatting:

```java
throw new FunctionalTestsException("Order with ID {} not found!", orderId);
throw new FunctionalTestsException("Expected status {} but got {}", expectedStatus, actualStatus);
```

Also supports wrapping another exception:
```java
throw new FunctionalTestsException(e);
```

### 13. Soft Assertions

Use `assertSoftly` for multi-field validations — all assertions run even if early ones fail:

```java
assertSoftly(softly -> {
    softly.assertThat(actualRoom.getName()).as("Room Name is wrong!").isEqualTo(expectedName);
    softly.assertThat(actualRoom.getType()).as("Room Type is wrong!").isEqualTo(expectedType);
    softly.assertThat(actualRoom.getPrice()).as("Room Price is wrong!").isEqualTo(expectedPrice);
});
```

### 14. FakeUtil and RandomStringUtils

For generating test data, use the existing utility classes:

**FakeUtil** (`com.levi9.functionaltests.util.FakeUtil`):
```java
FakeUtil.getRandomEmail()       // e.g., "abcXyz123@mail.com"
FakeUtil.getRandomPhoneNumber() // e.g., "123-456-7890"
```

**RandomStringUtils** (`org.apache.commons.lang3.RandomStringUtils`) — used directly in stepdefs:
```java
RandomStringUtils.randomAlphabetic(firstNameLength)   // Random letters
RandomStringUtils.randomAlphanumeric(200)              // Random letters+digits
RandomStringUtils.randomNumeric(phoneNumberLength)     // Random digits
```

### 15. RoomAmenities Helper

`RoomAmenities` is a helper class that converts between string representations and structured amenity data:

```java
// From comma-separated string (used in Gherkin steps)
final RoomAmenities amenities = new RoomAmenities("WiFi, TV and Safe");
amenities.isWifi();  // true
amenities.isTv();    // true
amenities.isSafe();  // true

// To list of strings (for API requests)
amenities.getAmenitiesAsList();  // ["WiFi", "TV", "Safe"]

// To display string (for UI assertions)
amenities.getRoomDetailsFromAmenities();  // "WiFi, TV, Safe" or "No features added to the room"
```

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

Match the annotation style of the system:
- **Pet Store**: Regex patterns with `^...$`, double-quoted strings in Gherkin
- **Restful Booker**: Cucumber Expressions with `{string}`, `{int}`, single-quoted strings in Gherkin

**Step 3: Create service**

Location: `src/main/java/com/levi9/functionaltests/rest/service/{system}/{Domain}Service.java`

Follow the constructor injection pattern. Throw `FunctionalTestsException` on bad status codes, NOT AssertJ assertions. Use the appropriate REST client for the system.

**Step 4: Create DSOs (if Pet Store) or use generated models (if Restful Booker)**

For Pet Store: create DSOs in `src/main/java/com/levi9/functionaltests/rest/data/petstore/`
For Restful Booker: check if OpenAPI-generated models exist in `restfulbooker.model.*`. If not, either add to the OpenAPI spec and regenerate, or create a hand-crafted DSO in `src/main/java/com/levi9/functionaltests/rest/data/restfulbooker/`.

**Tip**: If the API has an OpenAPI spec, use the helper script to generate DSO templates:
```bash
python3 .github/skills/functional-tests-skill/scripts/openapi_helper.py generate-dso \
  src/main/resources/openapi/{system}/{domain}-open-api.json \
  {SchemaName} \
  com.levi9.functionaltests.rest.data.{system}
```

**Step 5: Create entity and add to Storage**

Location: `src/main/java/com/levi9/functionaltests/storage/domain/{system}/{Resource}Entity.java`

Then update `Storage.java` with the new entity list and `getLast{Resource}()` convenience method.

**Step 6: Verify step definitions are properly linked (dry-run)**

```bash
mvn test -Dtest=DryRunRunnerIT
```

This validates that all Gherkin steps have matching step definitions. Fix any "Undefined step" errors.

**Step 7: Run the test**

```bash
mvn clean verify -Dtags='@{domain}'
```

If the test fails, investigate the failure, fix the issue, and run again.

### Creating a New UI Test

**Step 1: Create feature file**

Location: `src/test/resources/features/{system}/{subdirectory}/{domain}.feature`

```gherkin
@ui @{domain}
Feature: {Domain} UI

  Background: {Descriptive background title}
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

Follow the `BasePage<T>` pattern. Every page needs `isLoaded()` and `load()`. Use `@Nullable` on parameters for negative/validation tests.

**Step 3: Create step definitions**

Location: `src/test/java/com/levi9/functionaltests/stepdefs/{system}/{Domain}Stepdef.java`

**Important**: Always inject `BannerPage` and call `bannerPage.closeBanner()` after loading any Restful Booker page — the welcome banner blocks interactions.

```java
@Given("User is on the {Page} Page")
public void userIsOnPage() {
    page.load();
    bannerPage.closeBanner();
    assertThat(page.isLoaded()).as("User is not on the {Page} Page!").isTrue();
    log.info("User is on {Page} Page");
}
```

**Step 4: Verify step definitions (dry-run)**

```bash
mvn test -Dtest=DryRunRunnerIT
```

**Step 5: Run the test**

```bash
mvn clean verify -Dtags='@{domain} and @ui'
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
   - **Undefined steps**: Step definition regex/expression doesn't match Gherkin
   - **NullPointerException**: Storage entity not created or autowiring failed
   - **Assertion failure**: Response/UI state doesn't match expectation
   - **Timeout**: Element not found (UI) or slow response (API)
   - **404/500 errors**: Wrong endpoint path or server issue
   - **Banner blocking**: Forgot `bannerPage.closeBanner()` after loading page

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
- [ ] Uses **constructor injection** with `@Autowired` on constructor (not field injection)
- [ ] Dependencies stored in `private final` fields
- [ ] **Throws `FunctionalTestsException`** on bad status codes (not AssertJ assertions)
- [ ] Uses `HttpStatus.SC_*` constants from `org.apache.http.HttpStatus`
- [ ] Updates Storage after operations
- [ ] Logs important actions and results
- [ ] `public static final String REST_PATH` constant for endpoint paths

**Page Objects** (UI):
- [ ] Extends `BasePage<T>` with self-type
- [ ] Constructor takes `BaseDriver`, calls `super(baseDriver)`
- [ ] Has `load()` and `isLoaded()` methods
- [ ] Locators are `private final By` fields with descriptive names
- [ ] Uses wait-based methods (`waitAndClick`, `waitAndSendKeys`, etc.)
- [ ] Action methods return `void` (project convention — not fluent API)
- [ ] Verification methods return `boolean` or specific types
- [ ] Uses `@Nullable` (`javax.annotation.Nullable`) on parameters for negative/validation test methods
- [ ] Assertions done in step definitions, not page objects

**DSOs**:
- [ ] Uses Lombok annotations correctly
- [ ] `@JsonProperty` for non-standard field names
- [ ] `@Builder(toBuilder = true)` for immutability

**Entities**:
- [ ] Uses enums for status/type fields (not Strings)
- [ ] Includes `deleted` flag if applicable (Pet Store entities)
- [ ] Uses `@Builder.Default` for defaults
- [ ] Consider constructor from generated model (Restful Booker entities)

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
# Use development environment (default)
mvn clean verify -Denv=development

# Use local environment
mvn clean verify -Denv=local
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
This generates a complete Java DSO class with Lombok annotations and correct types.

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

## Jira & GitHub Ticket Integration

The skill includes a script to fetch ticket details from **Jira** or **GitHub Issues** and extract feature requirements, acceptance criteria, and linked issues — so you can generate tests directly from tickets.

### Setup

Copy the `.env.example` file and fill in your credentials:

```bash
cp .github/skills/functional-tests-skill/scripts/.env.example \
   .github/skills/functional-tests-skill/scripts/.env
```

Or set environment variables directly — see `.env.example` for the full list.

**Jira:**
```bash
export JIRA_BASE_URL=https://yourcompany.atlassian.net
export JIRA_USER_EMAIL=your-email@company.com
export JIRA_API_TOKEN=your-api-token
# OR for on-prem: export JIRA_PAT=your-personal-access-token
```

**GitHub:**
```bash
export GITHUB_TOKEN=ghp_your-personal-access-token
```

### Usage

The script auto-detects the source from the ticket reference format:
- **Jira**: `PROJ-123`
- **GitHub**: `owner/repo#123`

**Fetch ticket details** (description, acceptance criteria, subtasks, linked issues):
```bash
python3 .github/skills/functional-tests-skill/scripts/jira_ticket_fetcher.py fetch PROJ-123
python3 .github/skills/functional-tests-skill/scripts/jira_ticket_fetcher.py fetch owner/repo#42
```

**Fetch with all child/subtask details expanded** (Jira only):
```bash
python3 .github/skills/functional-tests-skill/scripts/jira_ticket_fetcher.py fetch PROJ-123 --include-children
```

**Output as JSON** (useful for piping into other tools):
```bash
python3 .github/skills/functional-tests-skill/scripts/jira_ticket_fetcher.py fetch PROJ-123 --format json
```

**Generate a Gherkin feature skeleton** from the ticket's acceptance criteria:
```bash
python3 .github/skills/functional-tests-skill/scripts/jira_ticket_fetcher.py generate-gherkin PROJ-123
python3 .github/skills/functional-tests-skill/scripts/jira_ticket_fetcher.py generate-gherkin owner/repo#42
```

### Workflow: From Ticket to Tests

When the user provides a ticket reference (e.g., "write tests for PROJ-123" or "write tests for owner/repo#42"):

1. **Check if credentials are configured** — Run the fetch script. If it fails with an authentication or missing env var error, **do NOT ask the user to provide tokens or credentials to you**. Instead, give them clear setup instructions:

   **If Python is not installed** (script fails with "command not found" or similar):

   > The ticket fetcher script requires **Python 3.10+**. It doesn't appear to be installed. Here's how to install it:
   >
   > **macOS** (using Homebrew):
   > ```bash
   > brew install python3
   > ```
   > If you don't have Homebrew: https://brew.sh
   >
   > **Windows**:
   > Download from https://www.python.org/downloads/ and run the installer. Make sure to check "Add Python to PATH".
   >
   > **Linux (Debian/Ubuntu)**:
   > ```bash
   > sudo apt update && sudo apt install python3
   > ```
   >
   > After installing, verify with `python3 --version` and try again.

   **Do NOT install Python for the user** — only provide the instructions above and let them do it.

   **If Jira/GitHub credentials are missing:**

   > For **Jira** tickets, you need to set up credentials before I can fetch ticket data. Here's how:
   >
   > 1. Copy the example env file:
   >    ```bash
   >    cp .github/skills/functional-tests-skill/scripts/.env.example .github/skills/functional-tests-skill/scripts/.env
   >    ```
   > 2. Edit `.github/skills/functional-tests-skill/scripts/.env` and fill in your Jira credentials:
   >    ```
   >    JIRA_BASE_URL=https://yourcompany.atlassian.net
   >    JIRA_USER_EMAIL=your-email@company.com
   >    JIRA_API_TOKEN=your-api-token
   >    ```
   >    Generate an API token at: https://id.atlassian.com/manage-profile/security/api-tokens
   >
   > Once configured, ask me again and I'll fetch the ticket.

   > For **GitHub** issues (private repos), you need a GitHub token:
   >
   > 1. Copy the example env file (if not already done):
   >    ```bash
   >    cp .github/skills/functional-tests-skill/scripts/.env.example .github/skills/functional-tests-skill/scripts/.env
   >    ```
   > 2. Edit `.github/skills/functional-tests-skill/scripts/.env` and add:
   >    ```
   >    GITHUB_TOKEN=ghp_your-personal-access-token
   >    ```
   >    Generate a token at: https://github.com/settings/tokens (needs `repo` scope for private repos)
   >
   > For public repos, no token is needed — the script works without it.

   **Important**: Never ask the user to paste tokens or credentials into the chat. Always direct them to edit the `.env` file themselves.

2. **Fetch the ticket** using the script to get the full context — summary, description, acceptance criteria, subtasks, and linked issues.
3. **Analyze the output** to understand what feature is being described, what the expected behavior is, and what edge cases exist.
4. **Optionally generate a Gherkin skeleton** as a starting point using `generate-gherkin`.
5. **Follow the standard test creation workflow** (described in the Workflows section) to implement the full test — feature file, step definitions, services, page objects, etc.
6. **Map acceptance criteria to scenarios** — each acceptance criterion should become at least one scenario (happy path), plus rainy-day variants where applicable.

The script supports Jira Cloud, Jira Server/Data Center (via PAT), and GitHub Issues (public and private repos). For GitHub, it also fetches issue comments and parses task list checkboxes as subtasks.

## Spring Configuration

The Spring context is configured in `src/test/java/com/levi9/functionaltests/config/SpringConfig.java`:

```java
@Configuration
@Scope("cucumber-glue")
@PropertySource("classpath:application-${env:development}.properties")
@ComponentScan({ "com.levi9.functionaltests" })
public class SpringConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
```

The `env` system property defaults to `development`. Property files are in `src/test/resources/`:
- `application-development.properties`
- `application-local.properties`
- `application-acceptance.properties`

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

14. **Constructor injection in services** — Use `@Autowired` on constructor with `private final` fields. Throw `FunctionalTestsException` on failures (not AssertJ assertions).

15. **Minimal effort maintenance** — Write code that's easy to understand and modify.

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
- Fetching Jira or GitHub ticket details to generate tests
- Creating tests from Jira/GitHub tickets or acceptance criteria

Even if the user just mentions "write a test", "add coverage", "this test is failing", "review this test", "write tests for PROJ-123", "write tests for owner/repo#42", or references any ticket key, trigger this skill immediately.

## Final Note

The goal is to create tests that are **indistinguishable from existing code** in terms of quality, style, and patterns. Always prioritize consistency, readability, and reusability. When in doubt, look at existing similar tests and follow their exact approach.
