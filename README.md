# Ingest Importer

## Spreadsheet Importer CLI

To run the quick demo of the spreadsheet importer, first the application needs to be built using Gradle:

    ./gradlew build
    
The runnable JAR file will be in `$PROJECT_ROOT/build/libs` directory. To run it with the sample excel file in this repository, the following command may be used:

    java -jar build/libs/ingest-importer-0.0.1-SNAPSHOT.jar ./src/test/resources/spreadsheets/v5.xlsx