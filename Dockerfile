FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

FROM quay.io/wildfly/wildfly:latest-jdk17
COPY --from=build /app/target/autorizacao.war /opt/jboss/wildfly/standalone/deployments/
EXPOSE 8080
