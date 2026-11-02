package com.servet.bdd.support;

import static io.restassured.RestAssured.given;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * REST Assured for the API scenarios. One instance per scenario (PicoContainer), so every scenario
 * has its own "last response" — parallel scenarios never read each other's answers.
 */
public class ApiClient {

    private static final RequestSpecification BASE = new RequestSpecBuilder()
            .setBaseUri(ApiConfig.API_URL)
            .setAccept("application/json")
            .setContentType(ContentType.JSON)
            .build();

    private Response lastResponse;

    public RequestSpecification anonymous() {
        return given().spec(BASE);
    }

    public RequestSpecification as(String accessToken) {
        return given().spec(BASE).auth().oauth2(accessToken);
    }

    /** Every step that calls the API stores the answer here; the Then steps check it. */
    public Response remember(Response response) {
        lastResponse = response;
        return response;
    }

    public Response last() {
        if (lastResponse == null) {
            throw new IllegalStateException("No API call was made in this scenario yet");
        }
        return lastResponse;
    }

    public boolean hasResponse() {
        return lastResponse != null;
    }
}
