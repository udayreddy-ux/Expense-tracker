FROM openjdk:19-jdk-alpine
WORKDIR /app
COPY . .
RUN ./mvnw package
CMD ["java","-jar","target/expense_tracker.jar"]