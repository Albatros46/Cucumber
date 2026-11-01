package com.servet.bdd.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {

    private static final By NAMES = By.cssSelector("[data-test='inventory-item-name']");
    private static final By CHECKOUT = By.id("checkout");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public List<String> names() {
        visible(CHECKOUT);
        return driver.findElements(NAMES).stream().map(e -> e.getText()).toList();
    }

    public void remove(String product) {
        click(By.xpath("//div[@data-test='inventory-item'][.//div[text()='" + product + "']]//button"));
    }

    public CheckoutPage checkout() {
        click(CHECKOUT);
        return new CheckoutPage(driver);
    }
}
