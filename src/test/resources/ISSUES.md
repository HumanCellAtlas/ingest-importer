# Issues
Processing [Q4DemoSS2Metadata_v5.xlsx](https://github.com/HumanCellAtlas/metadata-schema/blob/master/examples/spreadsheets/v5/filled/SmartSeq2/Q4DemoSS2Metadata_v5.xlsx?raw=true)

## Code
* ~schema_type is set to name of schema not base schema type~
* Ontology fields are not processed
  * ~any unknown field types default to string~ - replaced with exception to force dealing with 
* ~specimen_from_organism sheet is not generated~
    * Typo: speciment_from_organism
* ~unable to load: biomaterial_collection_protocol~
    * All protocols did not have coreSubTypes set
* types not supported:
  * ~boolean~
  * ~integer~
  * ~enum~
  * object
  * object array

## Spreadsheet
* biomaterial.biological_sex is missing on donor_organism
  * field is required should be resolved to unknown but is skipped because empty on spreadsheet
  * what should be the behaviour when a required field is missing?