FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM payara/micro:6.2024.1
COPY --from=build /app/target/*.war /opt/payara/deployments/ 