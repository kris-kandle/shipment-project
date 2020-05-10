Feature: search journeys
Actor: Client

  @tag1
  Scenario: search for journeys by origin
    Given a Client "Andrei" with address "259 Lyngby" email "Andrei@roumania" and ref person "Yann"
    And the port of Copenhagen which has 100 containers
    And a registered journey from Copenhagen to Hong Kong with "oranges"
    When client searches for journeys coming from "Copenhagen"
    Then return the journey coming from Copenhagen

  @tag2
  Scenario: search for journeys by destination
    Given a Client "Andrei" with address "259 Lyngby" email "Andrei@roumania" and ref person "Yann"
    And the port of Copenhagen which has 100 containers
    And a registered journey from Copenhagen to Hong Kong with "oranges"
    When client searches for journeys bound for "Hong Kong"
    Then return the journeys bound for Hong Kong

  @tag3
  Scenario: search for journeys by cargo
    Given a Client "Andrei" with address "259 Lyngby" email "Andrei@roumania" and ref person "Yann"
    And the port of Copenhagen which has 100 containers
    And a registered journey from Copenhagen to Hong Kong with "oranges"
    When client searches for journeys with cargo of "oranges"
    Then return the journeys with cargo of "oranges"
    
	@tag4
  Scenario: search for journeys by keyword
    Given a Client "Andrei" with address "259 Lyngby" email "Andrei@roumania" and ref person "Yann"
    And the port of Copenhagen which has 100 containers
    And a registered journey from Copenhagen to Hong Kong with "oranges"
    When client searches for journeys with keyword "Copenhagen"
    Then return the journeys related to "Copenhagen"