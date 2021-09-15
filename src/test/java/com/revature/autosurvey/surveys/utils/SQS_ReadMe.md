### Messaging Queues for Surveys SQS ###
Used for decoupling interactions between Analytics, Surveys, Submissions and
the repository.

## Features and Usage (Backend) ##
* Analytics can initiate a request to Surveys by wrapping a Survey UUID in a message and sending to the Survey Queue on AWS (https://sqs.us-east-1.amazonaws.com/855430746673/SurveyQueue).
* Survey queue listener receives the message (when service is up) and queries the database on behalf of analytics. 
* Survey service sends a response back to the Analytics queue on AWS (https://sqs.us-east-1.amazonaws.com/855430746673/AnalyticsQueue) with a response based on query:
** Survey as JSON object if found
** "Survey ID: {Survey UUID} not found" if no matches returned from DB
** "Invalid UUID" if listener failed to parse UUID

## Environment Variables Used ##
* AWS_ACCESS_KEY_ID
* AWS_PASS
* AWS_SECRET_KEY
* AWS_USER
* CREDENTIALS_JSON
* FIREBASE_API_KEY
* SERVICE_ACCOUNT_ID
* SQS_PASS
* SQS_USER

Notes by: Stephen G (2107 Reactive Batch)
Date: 9/15/2021