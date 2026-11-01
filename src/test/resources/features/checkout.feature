@checkout
Feature: Checkout
  A shopper pays for the cart; the totals must add up.

  Background:
    Given I am logged in as "standard_user"

  @smoke @e2e
  Scenario: Buying two products
    Given I added "Sauce Labs Backpack" and "Sauce Labs Bike Light" to the cart
    When I check out with these details:
      | first name | Anna   |
      | last name  | Schmidt |
      | zip code   | 10115  |
    Then the item total is $39.98
    And the total is item total plus tax
    When I finish the order
    Then I see the confirmation "Thank you for your order!"

  @negative
  Scenario: The first name is required
    Given I added "Sauce Labs Backpack" to the cart
    When I check out with these details:
      | first name |        |
      | last name  | Schmidt |
      | zip code   | 10115  |
    Then I see the error "Error: First Name is required"
