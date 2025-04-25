FROM openjdk:17.0.12-jdk-slim as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

CMD ["java","-jar","/app/target/demo1-0.0.1-SNAPSHOT.jar","--spring.profiles.active-prod"]