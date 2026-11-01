package com.servet.bdd.support;

import java.util.Map;

import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * One browser per scenario. PicoContainer creates one Browser object per scenario and passes the
 * same object to the hooks and to every step class that asks for it in its constructor.
 * Parallel scenarios therefore never share a browser — no static field, no ThreadLocal.
 */
public class Browser {

    private WebDriver driver;
    private boolean startRetried;

    /**
     * Starting several Chromes at the same moment sometimes fails on Windows with
     * "session not created: unable to connect to renderer". That is the browser not starting,
     * not the test failing, so the start (only the start) is tried a second time.
     */
    public void start() {
        try {
            driver = new ChromeDriver(options());
        } catch (SessionNotCreatedException firstAttempt) {
            startRetried = true;
            driver = new ChromeDriver(options());
        }
    }

    public boolean startRetried() {
        return startRetried;
    }

    private static ChromeOptions options() {
        ChromeOptions options = new ChromeOptions();
        if (Config.HEADLESS) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080");
        // the demo password is public, so Chrome would show a "password leaked" dialog over the page
        options.setExperimentalOption("prefs", Map.of("credentials_enable_service", false,
                "profile.password_manager_enabled", false, "profile.password_manager_leak_detection", false));
        return options;
    }

    public WebDriver driver() {
        if (driver == null) {
            throw new IllegalStateException("No browser: is the scenario missing the @Before hook?");
        }
        return driver;
    }

    public boolean isStarted() {
        return driver != null;
    }

    public void quit() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
