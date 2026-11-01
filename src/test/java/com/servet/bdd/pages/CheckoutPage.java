package com.servet.bdd.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Step one (your information), step two (overview) and the confirmation. */
public class CheckoutPage extends BasePage {

    private static final By FIRST = By.id("first-name");
    private static final By LAST = By.id("last-name");
    private static final By ZIP = By.id("postal-code");
    private static final By CONTINUE = By.id("continue");
    private static final By ERROR = By.cssSelector("[data-test='error']");
    private static final By ITEM_TOTAL = By.cssSelector("[data-test='subtotal-label']");
    private static final By TAX = By.cssSelector("[data-test='tax-label']");
    private static final By TOTAL = By.cssSelector("[data-test='total-label']");
    private static final By FINISH = By.id("finish");
    private static final By COMPLETE = By.cssSelector("[data-test='complete-header']");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public void enter(String first, String last, String zip) {
        type(FIRST, first);
        type(LAST, last);
        type(ZIP, zip);
        click(CONTINUE);
    }

    public String error() {
        return visible(ERROR).getText();
    }

    public double itemTotal() {
        return price(visible(ITEM_TOTAL).getText());
    }

    public double tax() {
        return price(visible(TAX).getText());
    }

    public double total() {
        return price(visible(TOTAL).getText());
    }

    public String finish() {
        click(FINISH);
        return visible(COMPLETE).getText();
    }
}
