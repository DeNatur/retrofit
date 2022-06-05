FROM app-compiler:latest

RUN ./gradlew jar

RUN mv retrofit/build/libs/* ./retrofit.jar
RUN mv retrofit.jar vol-out/retrofit.jar
