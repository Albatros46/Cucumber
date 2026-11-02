@api @auth
Feature: Registration and login
  Anyone can register; the API answers with clear messages and never tells
  whether an e-mail address exists when a login fails.

  @smoke
  Scenario: A new user registers and logs in
    When a new user "Clara" registers
    Then the response status is 201
    And the response field "role" is "USER"
    When "Clara" logs in
    Then the response status is 200
    And the response contains an access token and a refresh token

  Scenario: The same e-mail address cannot be registered twice
    Given a registered user "Clara"
    When someone registers again with the e-mail address of "Clara"
    Then the response status is 409
    And the error message is "Email is already registered"

  Scenario Outline: A failed login does not reveal which part was wrong: <case>
    Given a registered user "Clara"
    When someone logs in with <email> and <password>
    Then the response status is 401
    And the error message is "Invalid email or password"

    Examples:
      | case               | email                 | password               |
      | wrong password     | the e-mail of "Clara" | a wrong password       |
      | unknown e-mail     | an unknown e-mail     | the password of "Clara" |

  Scenario Outline: Registration is validated: <field>
    When someone registers with name "<name>", e-mail "<email>" and password "<password>"
    Then the response status is 400
    And the validation errors contain "<message>"

    Examples:
      | field    | name | email              | password   | message                                    |
      | name     | Al   | al@example.com     | Secret-123 | Name must be between 3 and 50 characters!  |
      | e-mail   | Alex | not-an-email       | Secret-123 | Email should be valid.                     |
      | password | Alex | alex@example.com   | short      | Password must be between 8 and 72 characters. |
