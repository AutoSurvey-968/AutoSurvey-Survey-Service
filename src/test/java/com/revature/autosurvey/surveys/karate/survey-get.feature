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
		
		Background:
		* def kittens = read('test.json')
		* url "http://localhost:8081"

## Assuming our path is "http://localhost:8080"
  @get_all_surveys
  Scenario: return an array of all surveys
    Given path "/surveys"
    When method GET
    Then status 200
    And match response contains { uuid: '#present'}

  
  @get_survey_by_id
  Scenario: Take in an ID, return the survey
    Given path "/surveys/77b08d50-ba3e-11eb-b89f-2f5a5b95c9be"
    When method GET
    Then status 200
    And match response contains { uuid: '#present', title: 'This is a second title' }