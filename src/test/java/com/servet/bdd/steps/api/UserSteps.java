package com.servet.bdd.steps.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import com.servet.bdd.support.ApiClient;
import com.servet.bdd.support.ApiState;
import com.servet.bdd.support.ApiState.User;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

public class UserSteps {

    private final ApiClient api;
    private final ApiState state;

    public UserSteps(ApiClient api, ApiState state) {
        this.api = api;
        this.state = state;
    }

    private Response register(String name, String email, String password) {
        return api.remember(api.anonymous()
                .body(Map.of("name", name, "email", email, "password", password))
                .post("/api/auth/register"));
    }

    private Response login(String email, String password) {
        return api.remember(api.anonymous()
                .body(Map.of("email", email, "password", password))
                .post("/api/auth/login"));
    }

    @When("a new user {string} registers")
    public void newUserRegisters(String name) {
        Response response = register(name, state.emailFor(name), state.passwordFor(name));
        if (response.statusCode() == 201) {
            state.user(new User(name, state.emailFor(name), state.passwordFor(name), null));
        }
    }

    /** Arrange step: register and log in, keep the token for the following steps. */
    @Given("a registered user {string}")
    public void registeredUser(String name) {
        String email = state.emailFor(name);
        String password = state.passwordFor(name);
        assertThat(register(name, email, password).statusCode()).as("register %s", name).isEqualTo(201);
        Response login = login(email, password);
        assertThat(login.statusCode()).as("login %s", name).isEqualTo(200);
        state.user(new User(name, email, password, login.path("accessToken")));
    }

    @When("{string} logs in")
    public void logsIn(String name) {
        User user = state.user(name);
        login(user.email(), user.password());
    }

    @When("someone registers again with the e-mail address of {string}")
    public void registersAgain(String name) {
        register("Someone Else", state.user(name).email(), "Another-Password-1");
    }

    @When("someone logs in with the e-mail of {string} and a wrong password")
    public void wrongPassword(String name) {
        login(state.user(name).email(), state.user(name).password() + "-wrong");
    }

    @When("someone logs in with an unknown e-mail and the password of {string}")
    public void unknownEmail(String name) {
        login(state.emailFor("nobody"), state.user(name).password());
    }

    @When("someone registers with name {string}, e-mail {string} and password {string}")
    public void registersWith(String name, String email, String password) {
        register(name, email, password);
    }

    @Then("the response contains an access token and a refresh token")
    public void containsTokens() {
        // only the shape is checked; the values never go into the report
        assertThat((String) api.last().path("accessToken")).matches("[\\w-]+\\.[\\w-]+\\.[\\w-]+");
        assertThat((String) api.last().path("refreshToken")).isNotBlank();
    }
}
