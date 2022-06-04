FROM dependencies:latest
WORKDIR retrofit
RUN ./gradlew assemble