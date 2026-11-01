package com.servet.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.servet.bdd.pages.InventoryPage;
import com.servet.bdd.pages.LoginPage;
import com.servet.bdd.support.Browser;
import com.servet.bdd.support.Config;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginSteps {

    @Given("I am on the login page")
    public void onLoginPage() {
        new LoginPage(Browser.driver()).open();
    }

    @Given("I am logged in as {string}")
    public void loggedInAs(String username) {
        onLoginPage();
        logInAs(username);
        assertThat(new InventoryPage(Browser.driver()).title()).isEqualTo("Products");
    }

    /** The password is not in the feature file: it comes from Config (env SAUCE_PASSWORD or the demo value). */
    @When("I log in as {string}")
    public void logInAs(String username) {
        new LoginPage(Browser.driver()).logIn(username, Config.password());
    }

    /** {password} in the examples table means "the real password", anything else is used as written. */
    @When("I log in as {string} with password {string}")
    public void logInWithPassword(String username, String password) {
        new LoginPage(Browser.driver()).logIn(username, "{password}".equals(password) ? Config.password() : password);
    }

    @Then("I see the products page")
    public void productsPage() {
        assertThat(new InventoryPage(Browser.driver()).title()).isEqualTo("Products");
    }

    @Then("I see the error {string}")
    public void error(String message) {
        String shown = Browser.driver().getCurrentUrl().contains("checkout")
                ? new com.servet.bdd.pages.CheckoutPage(Browser.driver()).error()
                : new LoginPage(Browser.driver()).error();
        assertThat(shown).isEqualTo(message);
    }
}
