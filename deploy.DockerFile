FROM app-compiler:latest

RUN cat samples/build.gradle
RUN ./gradlew samples:tasks
RUN ./gradlew samples:runWithJavaExec