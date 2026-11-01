package com.servet.bdd.support;

import java.time.Duration;

public final class Config {

    public static final String BASE_URL = System.getProperty("baseUrl", "https://www.saucedemo.com/");
    public static final boolean HEADLESS = Boolean.parseBoolean(System.getProperty("headless", "false"));
    public static final Duration WAIT = Duration.ofSeconds(Long.getLong("waitSeconds", 10));

    /** The public demo password; features only say {password}, the value never appears in a report. */
    public static String password() {
        String fromEnv = System.getenv("SAUCE_PASSWORD");
        return fromEnv != null && !fromEnv.isBlank() ? fromEnv : "secret_sauce";
    }

    private Config() {
    }
}
