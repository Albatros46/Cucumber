package com.servet.bdd.steps.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.servet.bdd.support.ApiClient;
import com.servet.bdd.support.ApiState;
import com.servet.bdd.support.TestAudio;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

public class PlaylistSteps {

    private final ApiClient api;
    private final ApiState state;

    public PlaylistSteps(ApiClient api, ApiState state) {
        this.api = api;
        this.state = state;
    }

    private String token(String user) {
        return state.user(user).accessToken();
    }

    private Response createPlaylist(String user, String name, boolean isPublic) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("isPublic", isPublic);
        return api.remember(api.as(token(user)).body(body).post("/api/playlists"));
    }

    // ---------------------------------------------------------------- arrange

    @Given("{string} uploaded the songs {string}, {string} and {string}")
    public void uploadedSongs(String user, String first, String second, String third) {
        for (String title : List.of(first, second, third)) {
            Response response = api.remember(api.as(token(user))
                    .contentType("multipart/form-data")
                    .multiPart("title", title)
                    .multiPart("artist", "BDD Artist")
                    .multiPart("album", "BDD Album")
                    .multiPart("file", title.replace(' ', '-') + ".wav", TestAudio.silentWav(), "audio/wav")
                    .post("/api/songs"));
            assertThat(response.statusCode()).as("upload %s", title).isEqualTo(201);
            state.song(title, ((Number) response.path("id")).longValue());
        }
    }

    @Given("{string} created a private playlist {string}")
    public void createdPrivate(String user, String name) {
        created(user, name, false);
    }

    @Given("{string} created a public playlist {string}")
    public void createdPublic(String user, String name) {
        created(user, name, true);
    }

    private void created(String user, String name, boolean isPublic) {
        Response response = createPlaylist(user, name, isPublic);
        assertThat(response.statusCode()).as("create %s", name).isEqualTo(201);
        state.playlist(name, ((Number) response.path("id")).longValue());
    }

    @Given("{string} made the playlist {string} public")
    public void madePublic(String user, String name) {
        Response response = api.remember(api.as(token(user))
                .body(Map.of("name", name, "isPublic", true))
                .put("/api/playlists/{id}", state.playlist(name)));
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Given("{string} added {string} to {string}")
    public void addedSong(String user, String song, String playlist) {
        assertThat(addSong(user, song, playlist).statusCode()).isEqualTo(200);
    }

    @Given("{string} added {string}, {string} and {string} to {string}")
    public void addedSongs(String user, String first, String second, String third, String playlist) {
        for (String song : List.of(first, second, third)) {
            addedSong(user, song, playlist);
        }
    }

    // ---------------------------------------------------------------- act

    @When("{string} opens the playlist {string}")
    public void opens(String user, String name) {
        api.remember(api.as(token(user)).get("/api/playlists/{id}", state.playlist(name)));
    }

    @When("an anonymous visitor opens the playlist {string}")
    public void anonymousOpens(String name) {
        api.remember(api.anonymous().get("/api/playlists/{id}", state.playlist(name)));
    }

    @When("{string} tries to {word} the playlist {string}")
    public void triesTo(String user, String action, String name) {
        long id = state.playlist(name);
        api.remember(switch (action) {
            case "open" -> api.as(token(user)).get("/api/playlists/{id}", id);
            case "rename" -> api.as(token(user)).body(Map.of("name", "Taken over")).put("/api/playlists/{id}", id);
            case "delete" -> api.as(token(user)).delete("/api/playlists/{id}", id);
            default -> throw new IllegalArgumentException("Unknown action: " + action);
        });
    }

    @When("{string} lists the public playlists")
    public void listsPublic(String user) {
        api.remember(api.as(token(user)).queryParam("size", 100).get("/api/playlists/public"));
    }

    @When("{string} adds {string} to {string}")
    public void adds(String user, String song, String playlist) {
        addSong(user, song, playlist);
    }

    private Response addSong(String user, String song, String playlist) {
        return api.remember(api.as(token(user))
                .post("/api/playlists/{id}/songs/{songId}", state.playlist(playlist), state.song(song)));
    }

    @When("{string} removes {string} from {string}")
    public void removes(String user, String song, String playlist) {
        api.remember(api.as(token(user))
                .delete("/api/playlists/{id}/songs/{songId}", state.playlist(playlist), state.song(song)));
    }

    @When("{string} creates a playlist named {string}")
    public void createsNamed(String user, String name) {
        createPlaylist(user, name, false);
    }

    @When("{string} creates a playlist named of {int} characters")
    public void createsNamedOfLength(String user, int length) {
        createPlaylist(user, "x".repeat(length), false);
    }

    // ---------------------------------------------------------------- assert

    @Then("the error message says that the playlist {string} was not found")
    public void playlistNotFound(String name) {
        assertThat((String) api.last().path("message")).isEqualTo("Playlist " + state.playlist(name) + " not found");
    }

    @Then("the list contains {string}")
    public void listContains(String name) {
        assertThat(publicIds()).contains(state.playlist(name));
    }

    @Then("the list does not contain {string}")
    public void listDoesNotContain(String name) {
        assertThat(publicIds()).doesNotContain(state.playlist(name));
    }

    private List<Long> publicIds() {
        List<Number> ids = api.last().path("content.id");
        return ids.stream().map(Number::longValue).toList();
    }

    /** DataTable -> List<Map>: each row is one expected entry, compared field by field. */
    @Then("the playlist {string} has these songs in this order:")
    public void songsInOrder(String name, List<Map<String, String>> expected) {
        List<Map<String, Object>> songs = api.last().path("songs");
        assertThat(songs).hasSize(expected.size());
        for (int i = 0; i < expected.size(); i++) {
            assertThat(songs.get(i).get("position").toString()).isEqualTo(expected.get(i).get("position"));
            assertThat(songs.get(i).get("title")).isEqualTo(expected.get(i).get("title"));
        }
        assertThat(((Number) api.last().path("id")).longValue()).isEqualTo(state.playlist(name));
    }
}
