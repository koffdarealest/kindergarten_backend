FROM openjdk:17

ARG JAR_FILE=build/libs/kindergarten-0.0.1-SNAPSHOT.jar

#ARG GOOGLE_APPLICATION_CREDENTIALS='koffee-keys.json'

COPY ${JAR_FILE} app.jar

#COPY ${GOOGLE_APPLICATION_CREDENTIALS} koffee-keys.json

#ENV GOOGLE_APPLICATION_CREDENTIALS = koffee-keys.json

ENTRYPOINT ["java","-jar","/app.jar"]