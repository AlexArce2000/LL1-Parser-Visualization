# Etapa 1: compilar el WAR con Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: correr el WAR en Tomcat
FROM tomcat:10.1-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/LL1.war /usr/local/tomcat/webapps/ROOT.war
ENV PORT=8080
EXPOSE 8080
CMD ["catalina.sh", "run"]