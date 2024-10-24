Feature: Create Team Sheet
  This feature provides basic functionality for creating a Team Sheet

  Scenario: Happy path
    Given A team
    And An opponent
    When I create a team sheet
    Then It should be in the list of teams sheets for that team