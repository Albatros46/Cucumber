@cart
Feature: Shopping cart
  The cart badge and the cart page must always show what was added.

  Background:
    Given I am logged in as "standard_user"

  @smoke
  Scenario: Adding products updates the badge and the cart
    When I add these products to the cart:
      | Sauce Labs Backpack     |
      | Sauce Labs Bike Light   |
      | Sauce Labs Bolt T-Shirt |
    Then the cart badge shows 3
    And the cart contains exactly:
      | Sauce Labs Backpack     |
      | Sauce Labs Bike Light   |
      | Sauce Labs Bolt T-Shirt |

  Scenario: Removing a product from the cart page
    Given I added "Sauce Labs Backpack" and "Sauce Labs Onesie" to the cart
    When I remove "Sauce Labs Onesie" in the cart
    Then the cart contains exactly:
      | Sauce Labs Backpack |
    And the cart badge shows 1

  Scenario Outline: Sorting the products: <order>
    When I sort the products by "<order>"
    Then the first product is "<first>"

    Examples:
      | order               | first                             |
      | Name (A to Z)       | Sauce Labs Backpack               |
      | Name (Z to A)       | Test.allTheThings() T-Shirt (Red) |
      | Price (low to high) | Sauce Labs Onesie                 |
      | Price (high to low) | Sauce Labs Fleece Jacket          |
