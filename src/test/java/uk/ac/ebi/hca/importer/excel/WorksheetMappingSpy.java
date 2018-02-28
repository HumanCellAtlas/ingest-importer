package uk.ac.ebi.hca.importer.excel;

public class WorksheetMappingSpy extends WorksheetMapping {

    public int getNumberOfMappings() {
        return mapping.size();
    }

    @Override
    public String toString() {
        return "WorksheetMappingSpy{" +
                "mapping=" + mapping +
                '}';
    }
}
