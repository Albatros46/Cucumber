package com.servet.bdd.support;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Week 17: the simplest way to share the browser between hooks and step classes — a static field.
 * It works because the scenarios run one after another. It would NOT survive parallel scenarios
 * (they would share one browser); Week 18 replaces it with dependency injection (PicoContainer).
 */
public final class Browser {

    private static WebDriver driver;

    private Browser() {
    }

    public static void start() {
        ChromeOptions options = new ChromeOptions();
        if (Config.HEADLESS) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080");
        // the demo password is public, so Chrome would show a "password leaked" dialog over the page
        options.setExperimentalOption("prefs", Map.of("credentials_enable_service", false,
                "profile.password_manager_enabled", false, "profile.password_manager_leak_detection", false));
        driver = new ChromeDriver(options);
    }

    public static WebDriver driver() {
        if (driver == null) {
            throw new IllegalStateException("No browser: is the scenario missing the @Before hook?");
        }
        return driver;
    }

    public static void quit() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
