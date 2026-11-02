package com.servet.bdd.steps.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.servet.bdd.support.ApiClient;
import com.servet.bdd.support.ApiState;

import io.cucumber.java.en.Then;

/** Generic checks on the last response, used by every API feature. */
public class ResponseSteps {

    private final ApiClient api;
    private final ApiState state;

    public ResponseSteps(ApiClient api, ApiState state) {
        this.api = api;
        this.state = state;
    }

    @Then("the response status is {int}")
    public void status(int expected) {
        assertThat(api.last().statusCode()).as("HTTP status").isEqualTo(expected);
    }

    @Then("the response field {string} is {string}")
    public void fieldIs(String field, String expected) {
        assertThat(String.valueOf((Object) api.last().path(field))).isEqualTo(expected);
    }

    @Then("the response field {string} is true")
    public void fieldIsTrue(String field) {
        assertThat((Boolean) api.last().path(field)).isTrue();
    }

    @Then("the response field {string} is false")
    public void fieldIsFalse(String field) {
        assertThat((Boolean) api.last().path(field)).isFalse();
    }

    /** "Song <Morning Rain> is already ..." -> the id of that song, see ApiState.withIds. */
    @Then("the error message is {string}")
    public void errorMessage(String expected) {
        assertThat((String) api.last().path("message")).isEqualTo(state.withIds(expected));
    }

    @Then("the validation errors contain {string}")
    public void validationErrors(String expected) {
        List<String> errors = api.last().path("errors");
        assertThat(errors).as("validation errors").anySatisfy(error -> assertThat(error).contains(expected));
    }
}
