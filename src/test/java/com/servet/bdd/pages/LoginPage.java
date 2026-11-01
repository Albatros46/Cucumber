package com.servet.bdd.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.servet.bdd.support.Config;

public class LoginPage extends BasePage {

    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN = By.id("login-button");
    private static final By ERROR = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(Config.BASE_URL);
        visible(LOGIN);
        return this;
    }

    public void logIn(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        click(LOGIN);
    }

    public String error() {
        return visible(ERROR).getText();
    }
}
