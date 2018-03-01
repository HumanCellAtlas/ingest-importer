[![Build Status](https://travis-ci.org/HumanCellAtlas/ingest-importer.svg?branch=master)](https://travis-ci.org/HumanCellAtlas/ingest-importer)
[![Docker Repository on Quay](https://quay.io/repository/humancellatlas/ingest-importer/status "Docker Repository on Quay")](https://quay.io/repository/humancellatlas/ingest-importer)

# Ingest Importer

Imports spreadsheets into Ingest

You can build and run the app with docker. To run the web application with docker for build the docker image with: 

```
docker build . -t ingest-importer:latest
```

then run the docker container.

```
docker run -p 5000:5000  ingest-importer:latest
```