Feature Login

  The user has to login before he can look at articles

  Background:
    Given The user isn't logged on

  Scenario: The user enters his data and will be logged on
    When  The user enters his correct username and password
    Then  The user should be logged on

  Scenario: The user shouldn't be logged on when he enters incorrect data
    When The user enters his username and password
    And  The user enters wrong data
    Then The user shouldn't be logged on

  Scenario: Timeout when the login action takes longer then 500ms
    Given The user has entered his username and password
    When  The login actions takes longer then 500ms
    Then  Show an error message with a option to retry the action

  Scenario: Retry the login action when it fails before the timeout
    Given The user has entered his username and password
    When  The result of the login action is an error
    And   The duration of the action is below 500ms
    Then  Retry the login action