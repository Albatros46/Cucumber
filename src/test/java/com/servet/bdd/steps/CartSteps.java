package com.servet.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.servet.bdd.pages.CartPage;
import com.servet.bdd.pages.InventoryPage;
import com.servet.bdd.support.Browser;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CartSteps {

    private InventoryPage inventory() {
        return new InventoryPage(Browser.driver());
    }

    /** DataTable with one column -> List<String>. */
    @When("I add these products to the cart:")
    public void addProducts(List<String> products) {
        products.forEach(inventory()::add);
    }

    @Given("I added {string} to the cart")
    public void addedOne(String product) {
        inventory().add(product);
    }

    @Given("I added {string} and {string} to the cart")
    public void addedTwo(String first, String second) {
        inventory().add(first).add(second);
    }

    @When("I remove {string} in the cart")
    public void removeInCart(String product) {
        inventory().openCart().remove(product);
    }

    @When("I sort the products by {string}")
    public void sortBy(String order) {
        inventory().sortBy(order);
    }

    @Then("the first product is {string}")
    public void firstProduct(String name) {
        assertThat(inventory().names().getFirst()).isEqualTo(name);
    }

    @Then("the cart badge shows {int}")
    public void badge(int count) {
        assertThat(inventory().badge()).isEqualTo(count);
    }

    @Then("the cart contains exactly:")
    public void cartContains(List<String> products) {
        CartPage cart = Browser.driver().getCurrentUrl().contains("cart") ? new CartPage(Browser.driver())
                : inventory().openCart();
        assertThat(cart.names()).containsExactlyElementsOf(products);
    }
}
