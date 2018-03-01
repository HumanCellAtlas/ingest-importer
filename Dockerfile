FROM anapsix/alpine-java:9_jdk

WORKDIR /opt

ADD gradle ./gradle
ADD src ./src

COPY gradlew build.gradle ./

RUN ./gradlew assemble

CMD java -jar build/libs/*.jar