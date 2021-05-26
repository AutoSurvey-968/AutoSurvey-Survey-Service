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

@survey-tests
Feature: tests all the functions for Surveys

Background:
* def kittens = read('test.json')
* url "http://localhost:8080"


Scenario: 
Take in JSON object, Create a new survey
Take in an ID, return the survey
return an array of all surveys
Take in an ID, delete the survey from the db

##POST /surveys - creates new survey via JSON
Given path "surveys"
And request kittens
When method POST
Then status 201
And match response contains { uuid: '#present', title: 'This is a second title' }

* def uuid = response.uuid

## GET /surveys/:id - returns single survey as JSON
Given path "/surveys/" + uuid
When method GET
Then status 200
And match response contains { uuid: '#present', title: 'This is a second title' }
    
##GET /surveys - returns array of every survey as JSON
Given path "/surveys"
When method GET
Then status 200
And match response == '#notnull'

##DELETE /surveys - deletes survey
Given path "/surveys", uuid
When method DELETE
Then status 204
    
