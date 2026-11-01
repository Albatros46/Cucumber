@login
Feature: Login
  As a shopper I want to log in, so that I can buy products.
  Locked or unknown users must get a clear message instead.

  Background:
    Given I am on the login page

  @smoke
  Scenario: A standard user logs in
    When I log in as "standard_user"
    Then I see the products page

  @negative
  Scenario Outline: Login is refused: <case>
    When I log in as "<username>" with password "<password>"
    Then I see the error "<message>"

    Examples:
      | case            | username        | password       | message                                                                   |
      | locked out user | locked_out_user | {password}     | Epic sadface: Sorry, this user has been locked out.                       |
      | wrong password  | standard_user   | wrong_password | Epic sadface: Username and password do not match any user in this service |
      | unknown user    | nobody          | {password}     | Epic sadface: Username and password do not match any user in this service |
      | empty username  |                 | {password}     | Epic sadface: Username is required                                        |
      | empty password  | standard_user   |                | Epic sadface: Password is required                                        |

  @slow
  Scenario: A slow user still gets in
    When I log in as "performance_glitch_user"
    Then I see the products page
