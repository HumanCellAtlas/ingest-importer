package uk.ac.ebi.hca.importer.excel;

import java.util.HashMap;
import java.util.Map;

public class WorksheetMapping {

    private Map<String, CellMapping> mapping = new HashMap<>();

    public WorksheetMapping map(String header, String jsonProperty, CellDataType type) {
        mapping.put(header, new CellMapping(jsonProperty, type));
        return this;
    }

    public CellMapping getMappingFor(String header) {
        //TODO make this return a default CellMapping when none is specified?
        return mapping.get(header);
    }

}
