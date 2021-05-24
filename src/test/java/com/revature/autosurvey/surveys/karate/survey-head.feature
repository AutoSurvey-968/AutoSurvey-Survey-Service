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
@survey-head
Feature: HEAD /surveys - returns header of GET /surveys
		HEAD /surveys/:id - returns header of GET /surveys/:id

   @get_all_surveys_head
  Scenario: return an array of all surveys
    Given path "surveys"
    When method HEAD
    Then status 200
  
  @get_survey_by_id
  Scenario: Take in an ID, return the survey
    Given path "surveys", id
    And ## we have json
    When method HEAD
    Then status 200