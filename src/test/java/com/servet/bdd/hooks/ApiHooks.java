package com.servet.bdd.hooks;

import com.servet.bdd.support.ApiClient;

import io.cucumber.java.AfterStep;
import io.cucumber.java.Scenario;

public class ApiHooks {

    private final ApiClient api;

    public ApiHooks(ApiClient api) {
        this.api = api;
    }

    /**
     * The API counterpart of the screenshot: after a failed step the last response goes into the report.
     * Tokens are masked first — reports are shared, tokens must not be.
     */
    @AfterStep("@api")
    public void attachResponseOnFailure(Scenario scenario) {
        if (scenario.isFailed() && api.hasResponse()) {
            String body = api.last().asString()
                    .replaceAll("(\"(accessToken|refreshToken)\"\\s*:\\s*\")[^\"]*\"", "$1***\"");
            scenario.attach(api.last().getStatusCode() + " " + body, "text/plain", "Last API response");
        }
    }
}
