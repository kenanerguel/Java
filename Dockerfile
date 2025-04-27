FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM tomcat:11.0-jdk17
ENV CATALINA_HOME /usr/local/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH

# Remove default Tomcat webapps
RUN rm -rf $CATALINA_HOME/webapps/*

# Copy the WAR file to Tomcat's webapps directory
COPY --from=build /app/target/*.war $CATALINA_HOME/webapps/ROOT.war

# Create context.xml for database configuration
RUN mkdir -p $CATALINA_HOME/conf/Catalina/localhost
COPY <<EOF $CATALINA_HOME/conf/Catalina/localhost/ROOT.xml
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <Resource name="jdbc/co2db"
              auth="Container"
              type="javax.sql.DataSource"
              maxTotal="100"
              maxIdle="30"
              maxWaitMillis="10000"
              username="co2user"
              password="123"
              driverClassName="com.mysql.cj.jdbc.Driver"
              url="jdbc:mysql://like_hero_mysql:3306/co2db?useSSL=false"/>
</Context>
EOF

# Copy MySQL driver to Tomcat's lib directory
COPY --from=build /app/target/like-hero-to-zero/WEB-INF/lib/mysql-connector-j-*.jar $CATALINA_HOME/lib/

EXPOSE 8080
CMD ["catalina.sh", "run"] 