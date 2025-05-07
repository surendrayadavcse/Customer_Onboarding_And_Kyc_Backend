# Use an official OpenJDK image as base
FROM openjdk:17-jdk-slim

# Add metadata

WORKDIR /app
# Argument for JAR file name
COPY target/customerkycandonboarding-0.0.1-SNAPSHOT.jar /app/customerkycandonboarding-0.0.1-SNAPSHOT.jar




# Expose the port the app runs on
EXPOSE 9999

# Run the JAR file
ENTRYPOINT ["java", "-jar", "customerkycandonboarding-0.0.1-SNAPSHOT.jar"]