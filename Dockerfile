FROM docker.io/maven:3.8.5-openjdk-17 as builder
# 其他指令...


WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

CMD ["java","-jar","/app/target/demo1-0.0.1-SNAPSHOT.jar","--spring.profiles.active-prod"]