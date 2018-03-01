package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

public class WorkbookImporter {

    private final ObjectMapper objectMapper;

    private final Map<String, WorksheetImporter> registry = new HashMap<>();

    public WorkbookImporter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode importFrom(Workbook workbook) {
        ObjectNode json = objectMapper.createObjectNode();
        StreamSupport.stream(workbook.spliterator(), false)
                .filter(this::hasImporter)
                .forEach(worksheet -> {
                    String sheetName = worksheet.getSheetName();
                    /*
                    JsonNode worksheetJson = registry.get(sheetName).importFrom(worksheet);
                    worksheetJson.fieldNames().forEachRemaining(fieldName -> {
                        json.set(fieldName, worksheetJson.get(fieldName));
                    });
                    */
                });
        return json;
    }

    public WorkbookImporter register(String sheetName, WorksheetImporter worksheetImporter) {
        registry.put(sheetName, worksheetImporter);
        return this;
    }

    private boolean hasImporter(Sheet worksheet) {
        return registry.containsKey(worksheet.getSheetName());
    }

}
