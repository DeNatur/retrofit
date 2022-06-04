FROM builder:latest
WORKDIR retrofit
RUN ./gradlew test