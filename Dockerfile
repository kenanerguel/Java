FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:17-jdk-jammy
ENV PAYARA_PATH /opt/payara
ENV DOMAIN_NAME domain1
ENV INSTALL_DIR /opt/payara
ENV PAYARA_VERSION 6.2024.1

RUN apt-get update && \
    apt-get install -y wget unzip && \
    wget --no-verbose -O /tmp/payara.zip https://nexus.payara.fish/repository/payara-community/fish/payara/distributions/payara/${PAYARA_VERSION}/payara-${PAYARA_VERSION}.zip && \
    unzip -q /tmp/payara.zip -d /opt && \
    mv /opt/payara6 ${PAYARA_PATH} && \
    rm /tmp/payara.zip && \
    apt-get remove -y wget unzip && \
    apt-get autoremove -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.war ${PAYARA_PATH}/glassfish/domains/${DOMAIN_NAME}/autodeploy/

# Create a script to initialize the JDBC resources
RUN echo '#!/bin/sh\n\
./asadmin start-domain --verbose ${DOMAIN_NAME} && \
./asadmin create-jdbc-connection-pool \
    --datasourceclassname com.mysql.cj.jdbc.MysqlDataSource \
    --restype javax.sql.DataSource \
    --property user=root:password=root:serverName=like_hero_mysql:portNumber=3306:databaseName=co2db:useSSL=false \
    co2Pool && \
./asadmin create-jdbc-resource --connectionpoolid co2Pool jdbc/co2db && \
./asadmin stop-domain ${DOMAIN_NAME} && \
echo "JDBC resources created successfully"' > ${PAYARA_PATH}/bin/init-jdbc.sh && \
chmod +x ${PAYARA_PATH}/bin/init-jdbc.sh

EXPOSE 8080 4848 8181
WORKDIR ${PAYARA_PATH}/bin

# Run the initialization script and then start the domain
CMD ["sh", "-c", "./init-jdbc.sh && ./asadmin start-domain --verbose"] 