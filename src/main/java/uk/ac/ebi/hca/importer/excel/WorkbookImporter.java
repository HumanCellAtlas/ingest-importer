package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

public class WorkbookImporter {

    //TODO remove unused reference!
    private final ObjectMapper objectMapper;

    private final Map<String, WorksheetImporter> registry = new HashMap<>();

    public WorkbookImporter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<JsonNode> importFrom(Workbook workbook) {
        List<JsonNode> workbookRecords = new ArrayList<>();
        StreamSupport.stream(workbook.spliterator(), false)
                .filter(this::hasImporter)
                .forEach(worksheet -> {
                    String sheetName = worksheet.getSheetName();
                    List<JsonNode> worksheetRecords = registry.get(sheetName).importFrom(worksheet);
                    workbookRecords.addAll(worksheetRecords);
                });
        return workbookRecords;
    }

    public WorkbookImporter register(String sheetName, WorksheetImporter worksheetImporter) {
        registry.put(sheetName, worksheetImporter);
        return this;
    }

    private boolean hasImporter(Sheet worksheet) {
        return registry.containsKey(worksheet.getSheetName());
    }

}
