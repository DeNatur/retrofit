FROM app-compiler:latest
WORKDIR retrofit
RUN ./gradlew samples:run