FROM amazoncorretto:17
EXPOSE 8080

# the JAR file path
ARG JAR_FILE=target/*.jar

# Copy the JAR file from the build context into the Docker image
COPY ${JAR_FILE} app.jar

# Set the default command to run the Java application
ENTRYPOINT ["java", "-jar", "/app.jar"]
CMD ["-DAPP.ENV", "container"]
