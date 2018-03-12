package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

public class WorkbookImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkbookImporter.class);

    //TODO remove this unused property
    private final ObjectMapper objectMapper;

    private final Map<String, WorksheetImporter> registry = new HashMap<>();

    public WorkbookImporter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<ObjectNode> importFrom(Workbook workbook) {
        List<ObjectNode> workbookRecords = new ArrayList<>();
        StreamSupport.stream(workbook.spliterator(), false)
                .filter(this::hasImporter)
                .forEach(worksheet -> {
                    String sheetName = worksheet.getSheetName();
                    ObjectNode worksheetRecords = registry.get(sheetName).importFrom(worksheet);
                    LOGGER.info("- Processing worksheet: " + sheetName);
                    workbookRecords.add(worksheetRecords);
                });
        LOGGER.info("Completed processing spreadsheet");
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
