FROM dependencies:latest

WORKDIR /vol-in

RUN ./gradlew build