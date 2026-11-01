package com.servet.bdd.hooks;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
 * The Browser is injected by PicoContainer: the same object the step classes of this scenario get.
 */
public class Hooks {

    private static long suiteStart;
    private static final Set<String> threads = ConcurrentHashMap.newKeySet();

    private final Browser browser;

    public Hooks(Browser browser) {
        this.browser = browser;
    }

    @BeforeAll
    public static void beforeAll() {
        suiteStart = System.currentTimeMillis();
    }

    @Before(order = 0)
    public void startBrowser(Scenario scenario) {
        threads.add(Thread.currentThread().getName());
        browser.start();
        if (browser.startRetried()) {
            scenario.log("Chrome did not start on the first attempt and was started again");
        }
    }

    /** Tagged hook: only for scenarios tagged @slow — they are allowed to take longer. */
    @Before(value = "@slow", order = 1)
    public void noteSlowScenario(Scenario scenario) {
        scenario.log("Slow scenario: performance_glitch_user takes about 5 s to log in");
    }

    /** After a failed step the page still shows the problem: attach it to the report right there. */
    @AfterStep
    public void screenshotOnFailedStep(Scenario scenario) {
        if (scenario.isFailed() && browser.isStarted()) {
            byte[] png = ((TakesScreenshot) browser.driver()).getScreenshotAs(OutputType.BYTES);
            scenario.attach(png, "image/png", "Screenshot after the failed step");
        }
    }

    @After(order = 0)
    public void quitBrowser() {
        browser.quit();
    }

    @AfterAll
    public static void afterAll() {
        System.out.printf("All scenarios finished in %.1f s on %d thread(s)%n",
                (System.currentTimeMillis() - suiteStart) / 1000.0, threads.size());
    }
}
