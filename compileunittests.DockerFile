FROM dependencies:latest
WORKDIR retrofit
RUN ./gradlew compileTestJava