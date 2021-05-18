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
@survey-get
Feature: 
		GET /surveys - returns array of every survey as JSON
		GET /surveys/:id - returns single survey as JSON

## Assuming our path is "http://localhost:8080 
  @get_all_surveys
  Scenario: return an array of all surveys
    Given ## we have json
    When method GET
    Then status 201
    And ## other requirements
  
  @get_survey_by_id
  Scenario: Take in an ID, return the survey
    Given path <name>
    And ## we have json
    When method GET
    And <value>
    Then status 200
    And ## other requirements
    	| name  | value |
      | name1 |       |
      | name2 |       |