package com.servet.bdd.hooks;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.servet.bdd.support.Browser;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;

/**
 * Hooks run around every scenario (or step). order: lower runs first for @Before, last for @After.
 */
public class Hooks {

    private static long suiteStart;

    @BeforeAll
    public static void beforeAll() {
        suiteStart = System.currentTimeMillis();
    }

    @Before(order = 0)
    public void startBrowser() {
        Browser.start();
    }

    /** Tagged hook: only for scenarios tagged @slow — they are allowed to take longer. */
    @Before(value = "@slow", order = 1)
    public void noteSlowScenario(Scenario scenario) {
        scenario.log("Slow scenario: performance_glitch_user takes about 5 s to log in");
    }

    /** After a failed step the page still shows the problem: attach it to the report right there. */
    @AfterStep
    public void screenshotOnFailedStep(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] png = ((TakesScreenshot) Browser.driver()).getScreenshotAs(OutputType.BYTES);
            scenario.attach(png, "image/png", "Screenshot after the failed step");
        }
    }

    @After(order = 0)
    public void quitBrowser() {
        Browser.quit();
    }

    @AfterAll
    public static void afterAll() {
        System.out.printf("All scenarios finished in %.1f s%n", (System.currentTimeMillis() - suiteStart) / 1000.0);
    }
}
