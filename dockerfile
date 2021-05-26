FROM openjdk:8-jdk-alpine
WORKDIR /app

COPY target/survey-service.jar .
COPY src/main/resources/cassandra_truststore.jks src/main/resources/
EXPOSE 8081

CMD [ "java", "-jar", "survey-service.jar" ]