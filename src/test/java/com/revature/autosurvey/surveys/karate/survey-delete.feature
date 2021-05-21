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
@survey-delete
Feature: DELETE /surveys - deletes survey
Background:
		* url "http://localhost:8081"
  @delete_survey
  Scenario: Take in an ID, delete the survey from the db
    Given path "/surveys/8c07b9d0-ba62-11eb-ab7f-27b80cf05a16"
    When method DELETE
    Then status 204
