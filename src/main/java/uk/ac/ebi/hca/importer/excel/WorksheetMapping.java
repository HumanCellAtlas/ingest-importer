package uk.ac.ebi.hca.importer.excel;

import java.util.HashMap;
import java.util.Map;

import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING;

public class WorksheetMapping {

    protected Map<String, CellMapping> mapping = new HashMap<>();

    public WorksheetMapping() {}

    private WorksheetMapping(Map<String, CellMapping> mapping) {
        this();
        this.mapping.putAll(mapping);
    }

    public WorksheetMapping map(String header, String jsonProperty, CellDataType type) {
        mapping.put(header, new CellMapping(jsonProperty, type));
        return this;
    }

    public WorksheetMapping map(String header, String jsonProperty, CellDataType type, String ref) {
        mapping.put(header, new CellMapping(jsonProperty, type, ref));
        return this;
    }

    public CellMapping getMappingFor(String header) {
        CellMapping cellMapping = mapping.get(header);
        if (cellMapping == null) {
            String jsonProperty = header.toLowerCase().replaceAll(" ", "_");
            cellMapping = new CellMapping(jsonProperty, STRING, "");
        }
        return cellMapping;
    }

    public WorksheetMapping copy() {
        return new WorksheetMapping(mapping);
    }

}
