FROM openjdk:17-alpine

WORKDIR /app

COPY target/ms-profile-impl*.jar /app/app.jar
COPY target/otel/opentelemetry-javaagent*.jar /app/otel.jar

ENTRYPOINT ["java","-javaagent:/app/otel.jar","-jar","/app/app.jar"]
