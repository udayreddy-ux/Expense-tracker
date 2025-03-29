#Build base image
FROM maven:3.9.4-eclipse-temurin-21 AS build

#Setting work directory
WORKDIR /app

#Dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

# Package the application
RUN mvn clean package -DskipTests


FROM gcr.io/distroless/java21-debian12

WORKDIR /app

#Copy the built jar file from previous stage to the container
COPY --from=build /app/target/application-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]