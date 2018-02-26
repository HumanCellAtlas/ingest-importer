package uk.ac.ebi.hca.importer.excel;

import java.util.HashMap;
import java.util.Map;

import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING;

public class WorksheetMapping {

    private final String name;

    private Map<String, CellMapping> mapping = new HashMap<>();

    public WorksheetMapping() {
        name = "";
    }

    public WorksheetMapping(String name) {
        this.name = name;
    }

    private WorksheetMapping(Map<String, CellMapping> mapping) {
        this();
        this.mapping.putAll(mapping);
    }

    public String getName() {
        return name;
    }

    public WorksheetMapping map(String header, String jsonProperty, CellDataType type) {
        mapping.put(header, new CellMapping(jsonProperty, type));
        return this;
    }

    public CellMapping getMappingFor(String header) {
        CellMapping cellMapping = mapping.get(header);
        if (cellMapping == null) {
            String jsonProperty = header.toLowerCase().replaceAll(" ", "_");
            cellMapping = new CellMapping(jsonProperty, STRING);
        }
        return cellMapping;
    }

    public WorksheetMapping copy() {
        return new WorksheetMapping(mapping);
    }

    public boolean hasName() {
        return !"".equals(name);
    }

}
