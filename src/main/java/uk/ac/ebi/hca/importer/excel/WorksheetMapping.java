package uk.ac.ebi.hca.importer.excel;

import java.util.HashMap;
import java.util.Map;

import static uk.ac.ebi.hca.importer.excel.SchemaDataType.STRING;
import static uk.ac.ebi.hca.importer.excel.SchemaDataType.STRING_ARRAY;

public class WorksheetMapping {

    protected Map<String, CellMapping> mapping = new HashMap<>();

    public WorksheetMapping() {}

    private WorksheetMapping(Map<String, CellMapping> mapping) {
        this();
        this.mapping.putAll(mapping);
    }

    public WorksheetMapping map(String header, String jsonProperty, SchemaDataType type) {
        mapping.put(header, new CellMapping(jsonProperty, type));
        return this;
    }

    public WorksheetMapping map(String header, String jsonProperty, SchemaDataType type, String ref, boolean isLink) {
        mapping.put(header, new CellMapping(jsonProperty, type, ref, isLink));
        return this;
    }

    public CellMapping getMappingFor(String header) {
        CellMapping cellMapping = mapping.get(header);
        if (cellMapping == null) {
            //If not found assume a link
            String jsonProperty = header.toLowerCase().replaceAll(" ", "_");
            cellMapping = new CellMapping(jsonProperty, STRING_ARRAY, "", true);
        }
        return cellMapping;
    }

    public WorksheetMapping copy() {
        return new WorksheetMapping(mapping);
    }

}
