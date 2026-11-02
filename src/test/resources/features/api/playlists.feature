@api @playlists
Feature: Playlist privacy and ownership
  A private playlist belongs to its owner alone. Other users must not even learn
  that it exists, so they get 404 (not 403). Only the owner may change a playlist.

  Background:
    Given a registered user "Anna"
    And a registered user "Bob"
    And "Anna" uploaded the songs "Morning Rain", "Night Drive" and "Blue Hour"
    And "Anna" created a private playlist "Anna's Mix"

  @smoke
  Scenario: The owner sees her private playlist
    When "Anna" opens the playlist "Anna's Mix"
    Then the response status is 200
    And the response field "isPublic" is false
    And the response field "appUserName" is "Anna"

  Scenario Outline: Another user cannot <action> a private playlist
    When "Bob" tries to <action> the playlist "Anna's Mix"
    Then the response status is 404
    And the error message says that the playlist "Anna's Mix" was not found

    Examples:
      | action |
      | open   |
      | rename |
      | delete |

  Scenario: A public playlist can be read by others, but still not changed
    Given "Anna" made the playlist "Anna's Mix" public
    When "Bob" opens the playlist "Anna's Mix"
    Then the response status is 200
    When "Bob" tries to rename the playlist "Anna's Mix"
    Then the response status is 404

  Scenario: Only public playlists are listed for everyone
    Given "Bob" created a public playlist "Bob's Hits"
    When "Bob" lists the public playlists
    Then the list contains "Bob's Hits"
    And the list does not contain "Anna's Mix"

  Scenario: Without a token nothing is shown
    When an anonymous visitor opens the playlist "Anna's Mix"
    Then the response status is 401

  Scenario: A song cannot be in a playlist twice
    Given "Anna" added "Morning Rain" to "Anna's Mix"
    When "Anna" adds "Morning Rain" to "Anna's Mix"
    Then the response status is 409
    And the error message is "Song <Morning Rain> is already in this playlist"

  Scenario: Removing a song closes the gap in the order
    Given "Anna" added "Morning Rain", "Night Drive" and "Blue Hour" to "Anna's Mix"
    When "Anna" removes "Night Drive" from "Anna's Mix"
    Then the response status is 200
    And the playlist "Anna's Mix" has these songs in this order:
      | position | title        |
      | 1        | Morning Rain |
      | 2        | Blue Hour    |

  Scenario Outline: The playlist name is validated: <case>
    When "Anna" creates a playlist named <name>
    Then the response status is 400
    And the validation errors contain "<message>"

    Examples:
      | case     | name                | message                                         |
      | empty    | ""                  | Playlist name is required.                      |
      | too long | of 101 characters   | Playlist name must not exceed 100 characters.   |
