package com.servet.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.servet.bdd.pages.CartPage;
import com.servet.bdd.pages.InventoryPage;
import com.servet.bdd.support.Browser;
import com.servet.bdd.support.ScenarioState;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CartSteps {

    private final Browser browser;
    private final ScenarioState state;

    /** PicoContainer sees the constructor and passes this scenario's Browser and ScenarioState. */
    public CartSteps(Browser browser, ScenarioState state) {
        this.browser = browser;
        this.state = state;
    }

    private InventoryPage inventory() {
        return new InventoryPage(browser.driver());
    }

    /** Adds the product and remembers the price the product list showed for it. */
    private void add(String product) {
        InventoryPage page = inventory();
        state.added(product, page.priceOf(product));
        page.add(product);
    }

    /** DataTable with one column -> List<String>. */
    @When("I add these products to the cart:")
    public void addProducts(List<String> products) {
        products.forEach(this::add);
    }

    @Given("I added {string} to the cart")
    public void addedOne(String product) {
        add(product);
    }

    @Given("I added {string} and {string} to the cart")
    public void addedTwo(String first, String second) {
        add(first);
        add(second);
    }

    @When("I remove {string} in the cart")
    public void removeInCart(String product) {
        inventory().openCart().remove(product);
        state.removed(product);
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
        CartPage cart = browser.driver().getCurrentUrl().contains("cart") ? new CartPage(browser.driver())
                : inventory().openCart();
        assertThat(cart.names()).containsExactlyElementsOf(products);
    }
}
