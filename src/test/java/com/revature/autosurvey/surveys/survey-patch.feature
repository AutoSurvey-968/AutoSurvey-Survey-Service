#Author: your.email@your.domain.com
#Keywords Summary :
#Feature: List of scenarios.
#Scenario: Business rule through list of steps with arguments.
#Given: Some precondition step
#When: Some key actions
#Then: To observe outcomes or validation
#And,But: To enumerate more Given,When,Then steps
#Scenario Outline: List of steps for data-driven as an Examples and <placeholder>
#Examples: Container for s table
#Background: List of steps run before each of the scenarios
#""" (Doc Strings)
#| (Data Tables)
#@ (Tags/Labels):To group Scenarios
#<> (placeholder)
#""
## (Comments)
#Sample Feature Definition Template
@survey-patch
Feature: PATCH /surveys/:id - partially updates survey via JSON (eg: fixing a typo in a question)

  @patch_survey
  Scenario: Take in an ID, patch the data into the db
   Given path <name>
    And ## we have json
    When method PATCH
     And <value>
    Then status 200
    And ## other requirements
    	| name  | value |
      | name1 |       |
      | name2 |       |