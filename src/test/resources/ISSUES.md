# Issues
Processing [Q4DemoSS2Metadata_v5.xlsx](https://github.com/HumanCellAtlas/metadata-schema/blob/master/examples/spreadsheets/v5/filled/SmartSeq2/Q4DemoSS2Metadata_v5.xlsx?raw=true)

## Code
* ~~schema_type is set to name of schema not base schema type~~
* ~~Ontology fields are not processed~~
  * ~~any unknown field types default to string~~ 
* ~~specimen_from_organism sheet is not generated~
    * Typo: speciment_from_organism
* ~~unable to load: biomaterial_collection_protocol~~
    * All protocols did not have coreSubTypes set
* types not supported:
  * ~~boolean~~
  * ~~integer~~
  * ~~enum~~
  * ~~object~~
  * ~~object array is not showed in an array~~
* ~~An extra, blank donor_organism is created~~ (checked for empty row)
* id fields are retained
* ~~schema_type of processes is incorrect~~

## Spreadsheet
* biomaterial.biological_sex is missing on donor_organism
  * field is required should be resolved to unknown but is skipped because empty on spreadsheet
  * what should be the behaviour when a required field is missing?
* read_index is missing on files

## General process
* Create mappings of column headers to id, schema type and any reference schema. [MappingUtils](../../main/java/uk/ac/ebi/hca/importer/util/MappingUtil.java)
* Go through the cells on each worksheet, retrieve the content and convert it into the correct JSON using the mapping [CellMapping](../../main/java/uk/ac/ebi/hca/importer/excel/CellMapping.java)

## Tips for going quickly
* Use Pomodoro method for focusing effort:
  * Timebox 25 minutes and work on achieving one goal then take a 5 minute break and repeat.
  * After 4 cycles (2 hours) take a 15 minute break away from the computer.
* If you get stuck, explain the problem to someone as if they were a 5 year old. Alternatively explain it to a rubber duck (seriously) or write it down.
* Imagine you are doing an interview tasks, you only have an hour and have to have a working result. Get the result first and then improve code.
* Write a test with the end in mind. 
  * If you are taking a file and creating another file test output with a manually produce a "best guess" of the file and improve the example with time. 
  * Use the test result to list what features are not working and work from that as the input to pomodoro.
  * Keep going until there are not errors. Error driven development?
  * If at any point you want to refactor just use a test with the current output to test against. You then know you are not breaking where you have got to.
* With a test you should not need to use the debugger that much but it is a useful tool so don't be afraid to use it, step through and set breakpoints.
* Attempt to keep the README.md up-to-date with how the code works. If you can't explain easily it is likely to be over-complex.
* Start off with simple cases and a catch all. When you want to work on a specific case make it and error and then pass the error. Replete until the catch-all is not needed.
* If you feel the need to write a comment it is probally an indication that you can extract method/s with the name of what the comment would have been.
* Only start optimising when you have working output.
    * If you find yourself doing something twice it may be time to make code more generic but not before YAGNI.
    * Remove redundant code and use source control. Do not comment out.

