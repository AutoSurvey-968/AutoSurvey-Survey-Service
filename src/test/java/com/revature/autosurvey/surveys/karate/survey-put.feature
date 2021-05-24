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
@survey-put
Feature: PUT /surveys/:id - updates entire survey via JSON (eg: redoing all questions)
	Background:
		* def kittens = read('test.json')
		* url "http://localhost:8081"
   @update_survey
  Scenario: Take in an ID, put the data into the db
  Given path "surveys/ee38aad0-ba60-11eb-ab7f-27b80cf05a16"
  And request kittens
    When method PUT
    Then status 200
    And match response contains { uuid: '#present', title: 'This is a second title' }