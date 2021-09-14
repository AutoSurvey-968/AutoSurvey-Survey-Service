#!/bin/sh

# Check if autosurvey-network network exists
if [ -z "$(docker network ls -q -f name=autosurvey-network)" ]; then
    docker network create autosurvey-network
fi

# rm survey-service container if it exists
if [ -n "$(docker container ls -aqf name=survey-service)" ]; then
    echo "Removing survey-service"
    docker container stop survey-service
    docker container rm survey-service
fi

#start survey-service container
docker container run -d --name survey-service --network autosurvey-network -e EUREKA_URL -e CREDENTIALS_JSON -e CREDENTIALS_JSON_ENCODED -e FIREBASE_API_KEY -e SERVICE_ACCOUNT_ID -e AWS_PASS -e AWS_USER -e TRUSTSTORE_PASS -e TRUSTSTORE_ENCODED autosurvey/survey-service