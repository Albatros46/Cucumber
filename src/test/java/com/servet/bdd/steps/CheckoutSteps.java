package com.servet.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.Map;

import com.servet.bdd.pages.CheckoutPage;
import com.servet.bdd.pages.InventoryPage;
import com.servet.bdd.support.Browser;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CheckoutSteps {

    private CheckoutPage checkout() {
        return new CheckoutPage(Browser.driver());
    }

    /** Two-column DataTable -> Map: readable for a business person, typed for the code. */
    @When("I check out with these details:")
    public void checkOutWith(Map<String, String> details) {
        new InventoryPage(Browser.driver()).openCart().checkout()
                .enter(value(details, "first name"), value(details, "last name"), value(details, "zip code"));
    }

    @Then("the item total is ${double}")
    public void itemTotal(double expected) {
        assertThat(checkout().itemTotal()).isCloseTo(expected, within(0.001));
    }

    @Then("the total is item total plus tax")
    public void totalAddsUp() {
        CheckoutPage page = checkout();
        assertThat(page.total()).isCloseTo(page.itemTotal() + page.tax(), within(0.001));
    }

    @When("I finish the order")
    public void finish() {
        // the confirmation text is checked by the next step
    }

    @Then("I see the confirmation {string}")
    public void confirmation(String text) {
        assertThat(checkout().finish()).isEqualTo(text);
    }

    /** An empty table cell arrives as null: the shopper left the field empty. */
    private static String value(Map<String, String> details, String key) {
        String value = details.get(key);
        return value == null ? "" : value;
    }
}
