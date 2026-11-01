# Cucumber BDD · Gherkin, Hooks, Tags

[![BDD Tests](https://github.com/Albatros46/Cucumber/actions/workflows/bdd-tests.yml/badge.svg)](https://github.com/Albatros46/Cucumber/actions/workflows/bdd-tests.yml)
![Java](https://img.shields.io/badge/Java-21-orange) ![Cucumber](https://img.shields.io/badge/Cucumber-8.0-23D96C) ![Selenium](https://img.shields.io/badge/Selenium-4.51-43B02A) ![JUnit Platform](https://img.shields.io/badge/JUnit%20Platform-6.1-25A162) ![Scenarios](https://img.shields.io/badge/scenarios-15-brightgreen)

Behaviour-driven tests for the [Sauce Demo](https://www.saucedemo.com/) shop: the **feature files** describe the behaviour in plain English (Gherkin), Java **step definitions** drive the browser with Selenium.

Part of my 52-week QA Automation roadmap — **Week 17: Cucumber basics**.

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
| `@BeforeAll` / `@AfterAll` | measures the whole run ("All scenarios finished in 55.1 s") |
| `@Before(order = 0)` | starts a fresh browser for every scenario — no state leaks between scenarios |
| `@Before(value = "@slow")` | **tagged hook**: runs only for `@slow` scenarios and writes a note into the report |
| `@AfterStep` | if the step failed, attaches a **screenshot** of the page right then |
| `@After` | quits the browser, also after a failure |

**Control run:** the expected item total was changed to `$39.99`. Result: the step `failed`, the three following steps `skipped`, and the report contains the PNG screenshot attached by `@AfterStep`. The feature file was then restored.

## Tags

`@login` `@cart` `@checkout` (per feature) · `@smoke` `@e2e` `@negative` `@slow` (per scenario)

```bash
mvn test                                              # all 15 scenarios
mvn test -Dcucumber.filter.tags="@smoke"              # the 3 most important ones
mvn test -Dcucumber.filter.tags="@smoke and not @e2e" # 2 scenarios (verified)
mvn test -Dcucumber.filter.tags="not @slow"           # 14 scenarios (verified)
mvn test -Dheadless=true                              # without a visible browser
```

## Reports

After every run: `target/cucumber-report.html` (self-contained HTML, with screenshots) and `target/cucumber.json`. In CI both are uploaded as the artifact `cucumber-report-ui`.

## Structure

```
src/test/resources/
├── junit-platform.properties     glue, plugins (pretty, html, json)
└── features/                     login.feature · cart.feature · checkout.feature
src/test/java/com/servet/bdd/
├── RunCucumberTest               JUnit Platform @Suite -> the Cucumber engine
├── hooks/Hooks                   @BeforeAll · @Before · tagged @Before · @AfterStep · @After · @AfterAll
├── steps/                        LoginSteps · CartSteps · CheckoutSteps
├── pages/                        page objects (Login, Inventory, Cart, Checkout)
└── support/                      Browser (static driver holder) · Config
```

### A known limitation (on purpose)

The browser is shared through a **static field** (`Browser.driver()`). That is simple and fine while scenarios run one after another — but two scenarios in parallel would share one browser. Week 18 replaces it with dependency injection (PicoContainer) and runs the scenarios in parallel.

## Notes from the build
- Cucumber 8 no longer brings a JSON library: without Jackson the run fails with *"Cucumber needs a JSON library to write reports"*. Added `tools.jackson.core:jackson-databind` 3.x.
- Chrome shows a "password found in a data breach" dialog for the public demo password; it is switched off in `ChromeOptions`, otherwise it covers the page.

## What I learned
- _TODO: 2–3 sentences in your own words_
