FROM ubuntu:20.04

RUN apt-get update
RUN apt-get install -y openjre-11-jre-headless 

RUN mv retrofit/build/libs/* ./retrofit.jar
RUN mv retrofit.jar vol-out/retrofit.jar
