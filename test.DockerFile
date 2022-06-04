FROM builder:latest

WORKDIR /vol-in

RUN ./gradlew test