FROM adoptopenjdk/openjdk11:alpine

RUN addgroup -S spring && adduser -S spring -G spring

# Create tokens directory and give user ownership
RUN mkdir -p /tokens
RUN chown spring:spring /tokens

# Run with user privileges to mitigate risks (see https://security.stackexchange.com/a/106861)
USER spring:spring

# Expose the port for the web server
ARG PORT=8080
EXPOSE ${PORT}

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]