package com.servet.bdd.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class InventoryPage extends BasePage {

    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By NAMES = By.cssSelector("[data-test='inventory-item-name']");
    private static final By SORT = By.cssSelector("[data-test='product-sort-container']");
    private static final By BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
    private static final By CART = By.cssSelector("[data-test='shopping-cart-link']");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public String title() {
        return visible(TITLE).getText();
    }

    /** Finds the product by its name, not its position: the list order may change. */
    public InventoryPage add(String product) {
        click(By.xpath("//div[@data-test='inventory-item'][.//div[text()='" + product + "']]//button"));
        return this;
    }

    /** The price the product list shows for this product, e.g. 29.99 for "$29.99". */
    public double priceOf(String product) {
        return price(visible(By.xpath("//div[@data-test='inventory-item'][.//div[text()='" + product
                + "']]//div[@data-test='inventory-item-price']")).getText());
    }

    public void sortBy(String visibleText) {
        new Select(visible(SORT)).selectByVisibleText(visibleText);
    }

    public List<String> names() {
        return texts(NAMES);
    }

    public int badge() {
        return driver.findElements(BADGE).isEmpty() ? 0 : Integer.parseInt(visible(BADGE).getText());
    }

    public CartPage openCart() {
        click(CART);
        return new CartPage(driver);
    }
}
