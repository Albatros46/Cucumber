package com.servet.bdd.support;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * What the API scenario created, by the names the feature file uses ("Anna", "Anna's Mix").
 * E-mail addresses get a random part, so scenarios never collide — not when they run in parallel
 * and not when they run again against the same database.
 */
public class ApiState {

    public record User(String name, String email, String password, String accessToken) {
        @Override
        public String toString() {
            return "User[" + name + ", " + email + "]"; // no password, no token in logs or assertion messages
        }
    }

    private final String runId = UUID.randomUUID().toString().substring(0, 8);
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Long> playlists = new HashMap<>();
    private final Map<String, Long> songs = new HashMap<>();

    public String emailFor(String name) {
        return name.toLowerCase().replaceAll("[^a-z]", "") + "." + runId + "@bdd.example.com";
    }

    /** A throw-away password for a throw-away user, different for every scenario. */
    public String passwordFor(String name) {
        return "Bdd-" + runId + "-" + name;
    }

    public void user(User user) {
        users.put(user.name(), user);
    }

    public User user(String name) {
        User user = users.get(name);
        if (user == null) {
            throw new IllegalStateException("Unknown user \"" + name + "\": add a 'Given a registered user' step");
        }
        return user;
    }

    public void playlist(String name, long id) {
        playlists.put(name, id);
    }

    public long playlist(String name) {
        Long id = playlists.get(name);
        if (id == null) {
            throw new IllegalStateException("Unknown playlist \"" + name + "\"");
        }
        return id;
    }

    public void song(String title, long id) {
        songs.put(title, id);
    }

    public long song(String title) {
        Long id = songs.get(title);
        if (id == null) {
            throw new IllegalStateException("Unknown song \"" + title + "\"");
        }
        return id;
    }

    /** "Song <Morning Rain> is already ..." -> "Song 17 is already ...": the feature names, the API uses ids. */
    public String withIds(String text) {
        String result = text;
        for (Map.Entry<String, Long> song : songs.entrySet()) {
            result = result.replace("<" + song.getKey() + ">", String.valueOf(song.getValue()));
        }
        return result;
    }
}
