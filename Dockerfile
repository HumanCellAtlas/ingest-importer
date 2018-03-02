FROM anapsix/alpine-java:9_jdk

WORKDIR /opt

ENV INGEST_API=http://localhost:8080

ADD gradle ./gradle
ADD src ./src

COPY gradlew build.gradle ./

RUN ./gradlew assemble

CMD java -jar build/libs/*.jar --ingest.api.url=$INGEST_API
