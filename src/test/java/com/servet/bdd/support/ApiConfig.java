package com.servet.bdd.support;

public final class ApiConfig {

    /** The Spotify Clone API (github.com/Albatros46/Spotify_Clone), started separately. */
    public static final String API_URL = System.getProperty("apiUrl", "http://localhost:8042");

    private ApiConfig() {
    }
}
