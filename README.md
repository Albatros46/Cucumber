# Cucumber BDD · UI (Selenium) + API (REST Assured) · Parallel with PicoContainer

[![BDD Tests](https://github.com/Albatros46/Cucumber/actions/workflows/bdd-tests.yml/badge.svg)](https://github.com/Albatros46/Cucumber/actions/workflows/bdd-tests.yml)
![Java](https://img.shields.io/badge/Java-21-orange) ![Cucumber](https://img.shields.io/badge/Cucumber-8.0-23D96C) ![Selenium](https://img.shields.io/badge/Selenium-4.51-43B02A) ![JUnit Platform](https://img.shields.io/badge/JUnit%20Platform-6.1-25A162) ![TestNG](https://img.shields.io/badge/TestNG-7.12-CD6E00) ![PicoContainer](https://img.shields.io/badge/DI-PicoContainer-6A5ACD) ![REST Assured](https://img.shields.io/badge/REST%20Assured-6.2-blue) ![Scenarios](https://img.shields.io/badge/scenarios-16%20UI%20%2B%2018%20API-brightgreen)

Behaviour-driven tests in one project: the [Sauce Demo](https://www.saucedemo.com/) shop through the **browser** (Selenium) and my [Spotify Clone](https://github.com/Albatros46/Spotify_Clone) through its **REST API** (REST Assured). The **feature files** describe the behaviour in plain English (Gherkin); Java **step definitions** make it executable.

Part of my 52-week QA Automation roadmap:

| Week | Topic | Release |
|---|---|---|
| 17 | Gherkin, hooks, tags | [v1.0.0](https://github.com/Albatros46/Cucumber/releases/tag/v1.0.0) |
| 18 | Parallel scenarios, PicoContainer, TestNG runner | [v1.1.0](https://github.com/Albatros46/Cucumber/releases/tag/v1.1.0) |
| 19 | API scenarios with REST Assured | [v1.2.0](https://github.com/Albatros46/Cucumber/releases/tag/v1.2.0) |

## What a scenario looks like

```gherkin
@smoke @e2e
Scenario: Buying two products
  Given I added "Sauce Labs Backpack" and "Sauce Labs Bike Light" to the cart
  When I check out with these details:
    | first name | Anna    |
    | last name  | Schmidt |
    | zip code   | 10115   |
  Then the item total is $39.98
  And the total is item total plus tax
  When I finish the order
  Then I see the confirmation "Thank you for your order!"
```

A product owner can read (and review) this; the same text is an executable test.

## Gherkin features used

| Feature | Where | Why |
|---|---|---|
| `Background` | every feature | the shared first step (login page / logged in) is written once |
| `Scenario Outline` + `Examples` | 5 login errors · 4 sort orders | one scenario, many data rows — each row is its own test in the report |
| DataTable → `List<String>` | "I add these products to the cart:" | a list of products, readable as a list |
| DataTable → `Map<String, String>` | "I check out with these details:" | key/value form data; an **empty cell** arrives as `null` = field left empty |
| Cucumber expressions | `{string}`, `{int}`, `${double}` | typed parameters instead of regular expressions |
| `{password}` placeholder | login examples | the password is **not** in the feature file or the report; it comes from `SAUCE_PASSWORD` or the public demo value |

## Hooks

| Hook | Does |
|---|---|
| `@BeforeAll` / `@AfterAll` | measures the whole run ("All scenarios finished in 53.9 s on 4 thread(s)") |
| `@Before(order = 0)` | starts a fresh browser for every scenario — no state leaks between scenarios |
| `@Before(value = "@slow")` | **tagged hook**: runs only for `@slow` scenarios and writes a note into the report |
| `@AfterStep` | if the step failed, attaches a **screenshot** of the page right then |
| `@After` | quits the browser, also after a failure |

**Control run:** the expected item total was changed to `$39.99`. Result: the step `failed`, the three following steps `skipped`, and the report contains the PNG screenshot attached by `@AfterStep`. The feature file was then restored.

## Tags

`@login` `@cart` `@checkout` (per feature) · `@smoke` `@e2e` `@negative` `@slow` (per scenario)

```bash
mvn test                                              # all 16 UI scenarios, 4 at a time
mvn test -Dcucumber.filter.tags="@smoke"              # the 3 most important ones
mvn test -Dcucumber.filter.tags="@smoke and not @e2e" # 2 scenarios (verified)
mvn test -Dcucumber.filter.tags="not @slow"           # 15 scenarios (verified)
mvn test -Dheadless=true                              # without a visible browser
```

## Reports

After every run: `target/cucumber-report.html` (self-contained HTML, with screenshots) and `target/cucumber.json`. In CI both are uploaded as the artifact `cucumber-report-ui`.

## Structure

```
src/test/resources/
├── junit-platform.properties     glue, plugins (summary, html, json), parallel execution
└── features/                     login · cart · checkout (@ui)
    └── api/                      auth · playlists (@api)
src/test/java/com/servet/bdd/
├── RunCucumberTest               JUnit Platform @Suite -> the Cucumber engine (default profile)
├── RunCucumberTestNG             TestNG runner with a parallel DataProvider (-Ptestng)
├── hooks/Hooks                   @BeforeAll · @Before("@ui") · tagged @slow · @AfterStep · @After("@ui") · @AfterAll
├── hooks/ApiHooks                @AfterStep("@api"): last response (tokens masked) on failure
├── steps/                        LoginSteps · CartSteps · CheckoutSteps (constructor injection)
├── steps/api/                    UserSteps · PlaylistSteps · ResponseSteps
├── pages/                        page objects (Login, Inventory, Cart, Checkout)
└── support/                      Browser · ScenarioState · Config · ApiClient · ApiState · ApiConfig · TestAudio
```

## Week 18: parallel scenarios and PicoContainer

### Why the Week 17 design breaks in parallel (control run)

In Week 17 the browser lived in a **static field**. Before changing it, the scenarios were switched to parallel (4 threads) with that static field still in place:

| | Result |
|---|---|
| Week 17 code, 1 thread | 15 / 15 green |
| Week 17 code, **4 threads** | **14 of 15 errors**: `NoSuchElementException` 9 · `TimeoutException` 6 · `StaleElementReferenceException` 4 · `WebDriverException` 4 — and **11 chromedriver processes left running**, because each new scenario overwrote the static field and the older browsers were never closed |

### The fix: dependency injection with PicoContainer

```java
public class CartSteps {
    private final Browser browser;
    private final ScenarioState state;

    public CartSteps(Browser browser, ScenarioState state) {   // PicoContainer calls this
        this.browser = browser;
        this.state = state;
    }
```

With `cucumber-picocontainer` on the classpath, Cucumber creates **new objects for every scenario** and fills the constructors. Within one scenario, `Hooks`, `LoginSteps`, `CartSteps` and `CheckoutSteps` all get the **same** `Browser` and `ScenarioState`; another scenario on another thread gets its own. No `static`, no `ThreadLocal`.

`ScenarioState` shows the second use: **sharing data between step classes**. `CartSteps` remembers each product with the price shown on the product list; `CheckoutSteps` checks *"the item total is the sum of the added products"*. The step first asserts that the state is not empty — so it would fail if the two classes did not share the same object.

### Two runners, same features and steps

| | JUnit Platform (default) | TestNG (`-Ptestng`) |
|---|---|---|
| Runner | `@Suite @IncludeEngines("cucumber") @SelectPackages("features")` | `extends AbstractTestNGCucumberTests`, `@DataProvider(parallel = true)` |
| Parallel | `cucumber.execution.parallel.enabled=true`, strategy `fixed` | every scenario is a DataProvider row, `dataproviderthreadcount` |
| Thread count | `-Dthreads=N` (pom → system property) | `-Dthreads=N` (pom → surefire property) |
| Options | `junit-platform.properties` | `@CucumberOptions` (TestNG does not read the properties file) |
| Status | recommended by Cucumber | **deprecated for removal since Cucumber 8.0** |

```bash
mvn test                      # JUnit Platform, 4 threads
mvn test -Dthreads=1          # one after another
mvn test -Ptestng             # TestNG, 4 threads
mvn test -Ptestng -Dcucumber.filter.tags="@smoke"
```

CI runs both runners (matrix `junit` / `testng`), headless, 4 threads.

### Measured (16 scenarios, headless, my laptop: 8 logical CPUs)

| Runner | 1 thread | 4 threads |
|---|---|---|
| JUnit Platform | 64.9 s | 52.2 – 55.1 s (3 runs) |
| TestNG | 69.1 s | 54.9 – 58.8 s |

Only about **15–20 % faster** here — the per-scenario times in `cucumber.json` explain it: a scenario that takes ~4 s alone takes ~10 s when four Chromes compete for the same CPU. The total scenario time (166 s) did fit into 54 s, so the scenarios really ran side by side; the machine was the limit. More threads are not automatically faster.

### Flakiness found and handled

One run with 2 threads failed with `SessionNotCreatedException: unable to connect to renderer` — **Chrome did not start**, the test itself never ran. `Browser.start()` now tries the start once more and writes *"Chrome did not start on the first attempt…"* into the report. Only the browser start is retried, never a test result. 3 further runs with 4 threads: 16 / 16 green each, no retry needed.

## Week 19: API scenarios with REST Assured

Business rules of the Spotify Clone API, written so a product owner can check them:

```gherkin
Scenario Outline: Another user cannot <action> a private playlist
  When "Bob" tries to <action> the playlist "Anna's Mix"
  Then the response status is 404
  And the error message says that the playlist "Anna's Mix" was not found

  Examples:
    | action |
    | open   |
    | rename |
    | delete |
```

| Feature | Scenarios | Rules |
|---|---|---|
| `api/auth.feature` | 7 | register (201, role `USER`) and login (JWT-shaped tokens) · duplicate e-mail → 409 · failed login gives the **same** 401 message for a wrong password and an unknown e-mail · validation messages for name, e-mail, password |
| `api/playlists.feature` | 11 | private playlist: owner 200, others **404 not 403** for open / rename / delete · public playlist: others may read, still not change · public list never contains private playlists · no token → 401 · same song twice → 409 · removing a song renumbers the positions (DataTable as `List<Map>`) · name validation |

### How it fits the Week 17–18 design

- **Tags decide the setup:** UI features are tagged `@ui`, API features `@api`. The browser hooks are tagged hooks (`@Before("@ui")`, `@After("@ui")`), so API scenarios start no browser at all — 18 API scenarios take about 10 s.
- **PicoContainer again:** `ApiClient` (REST Assured spec + the last response) and `ApiState` (users, playlists, songs by the names the feature uses) are new per scenario and shared by `UserSteps`, `PlaylistSteps`, `ResponseSteps` and `ApiHooks`. API scenarios run in parallel like the UI ones.
- **Independent data:** every scenario registers its own users with a random part in the e-mail (`anna.6ee2ec9d@bdd.example.com`), uploads its own songs (a generated 0.1 s WAV) and creates its own playlists — no shared test data, no order dependency, repeatable against the same database.
- **Names, not ids, in the feature:** `"Anna's Mix"` and `"Morning Rain"` are mapped to the real ids; `Song <Morning Rain> is already in this playlist` becomes `Song 17 …` before comparing.
- **The API counterpart of the screenshot:** `@AfterStep("@api")` attaches the last response to the report when a step fails — with `accessToken` and `refreshToken` replaced by `***`.

### Control runs

| Change | Result |
|---|---|
| Expected `403` instead of `404` for Bob opening Anna's private playlist | step **failed**, report shows `404 {"message":"Playlist 30 not found", …}` |
| Expected `201` instead of `200` right after a login | step **failed**, report shows the login response with `"accessToken":"***","refreshToken":"***"` |
| Search for JWTs (`eyJ…`) in `cucumber.json` and the HTML report | **0** |

The control feature was deleted afterwards.

### Run

```bash
mvn test                                                    # UI scenarios only (default filter "not @api")
mvn test -Dcucumber.filter.tags=@api                        # API scenarios, Spotify Clone API on :8042
mvn test -Dcucumber.filter.tags=@api -DapiUrl=http://host:8042
mvn test -Ptestng -Dcucumber.filter.tags=@api               # the same with TestNG
```

**CI** (every push and every night): the UI job runs both runners; the API job starts PostgreSQL, builds and starts the Spotify Clone API at **v1.1.0** with a per-run JWT secret and runs the `@api` scenarios.

## Notes from the build
- Cucumber 8 no longer brings a JSON library: without Jackson the run fails with *"Cucumber needs a JSON library to write reports"*. Added `tools.jackson.core:jackson-databind` 3.x.
- Cucumber 8 deprecates `cucumber-testng` for removal and changed `scenarios()` to `scenarios(ITestContext)`; the JUnit Platform engine warns about `@SelectClasspathResource("features")` and wants `@SelectPackages("features")`.
- Surefire's `configurationParameters` did not reach the suite's Cucumber engine (*"Configuration parameter 'fixed.parallelism' must be set"*); passing them as system properties works.
- Chrome shows a "password found in a data breach" dialog for the public demo password; it is switched off in `ChromeOptions`, otherwise it covers the page.

## What I learned
- _TODO: 2–3 sentences in your own words_
