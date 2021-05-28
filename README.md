# AutoSurvey Survey Service

The survey microservices for AutoSurvey-968

## Technologies Used

* Java - SE1.8
* Java Spring
  - Reactive Web
* SonarCloud
* Lombok
* Swagger
* DataStax
* Eureka
* Karate
* JUnit
* Jacoco

## Features

* Create and Maintain Surveys, including their Question Sets.
* Search for one or more Surveys.
* Get a list of all available Surveys.

## Getting started

**See [Primary README.md](https://github.com/AutoSurvey-968/AutoSurvey-back) for full program setup instructions.**

`git clone https://github.com/AutoSurvey-968/AutoSurvey-Survey-Service`

Set environment variables:

* AWS_USER - Keyspaces username
* AWS_PASS - Keyspaces password
* TRUSTSTORE_PASS - Local Truststore password
* FIREBASE_API_KEY - Firebase API key for authentication calls
* SERVICE_ACCOUNT_ID - Firebase service account id
* CREDENTIALS_JSON - Name of credentials json file to be placed in src/main/resources

## Usage

```
{base-url}/
```
### GET:
**Authorization level**: ANY

Gets all surveys in the database.

### POST:
**Authorization level**: ADMIN-ONLY

Creates a new survey from JSON.

```
{base-url}/:id
```

### GET:
**Authorization level**: ANY

Gets a specific survey from the database.

### PUT:
**Authorization level**: ADMIN-ONLY

Updates a specific survey in the database.

### DELETE:
**Authorization level**: ADMIN-ONLY

Removes a specific survey from the database.

## Contributors

- [Robert Bierly](https://github.com/rnbiv45) - Primary
- [Arieh Gennello](https://github.com/MoldedPixels)
- [Benjamin Wood](https://github.com/lwood-benjamin)
