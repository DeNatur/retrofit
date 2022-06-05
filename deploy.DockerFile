FROM app-compiler:latest

RUN cat samples/build.gradle

RUN ./gradlew samples:run
