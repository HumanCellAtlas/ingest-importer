[![Build Status](https://travis-ci.org/HumanCellAtlas/ingest-importer.svg?branch=master)](https://travis-ci.org/HumanCellAtlas/ingest-importer)
[![Docker Repository on Quay](https://quay.io/repository/humancellatlas/ingest-importer/status "Docker Repository on Quay")](https://quay.io/repository/humancellatlas/ingest-importer)

# Ingest Importer

Imports spreadsheets as JSON formatted data into Ingest (Core).

## Development

### Environment

Ingest Importer is built as a Spring Boot application that is packaged into a single JAR that can
 be deployed in a container (or any up-to-date Java environment in general). This project uses 
 Java 9, although, at the time of writing, much of the code base is still at the JDK 8 level of 
 compliance as no notable Java 9 feature is used.
 
Below are some general guidelines on how to set up Java 9 for development. For more detailed information, it is recommended to consult documentation for the specific local environment on how to install and troubleshoot.

#### SDKMAN!

[SDKMAN!](http://sdkman.io/) helps ease much of the burden of setting up development environment on *nix systems, especially for Java related technologies. It allows users to install different tools of different versions at the same time and leaves it up to them to choose which of the available utilities to use at any given context. At the time of writing, SDKMAN! hosts several versions of the JDK from 7 up to 9.

### Gradle

Ingest Importer uses *Gradle* for most development related tasks. As the Gradle wrapper is checked into the version control, no further set up is necessary. Gradle wrapper can be run on the project root using `./gradlew`. For example to see all defined tasks:

    ./gradlew tasks

## Deployment

You can build and run the app with docker. To run the web application with docker for build the docker image with: 

```
docker build . -t ingest-importer:latest
```

then run the docker container.

```
docker run -p 5000:5000  ingest-importer:latest
```