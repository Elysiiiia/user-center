FROM maven:3.5-jdk-8-alphe as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

CMD ["java","-jar","/app/target/demo1-0.0.1-SNAPSHOT.jar","--spring.profiles.active-prod"]