package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class WorksheetImporter {

    private final ObjectMapper objectMapper;

    private final WorksheetMapping worksheetMapping;

    public WorksheetImporter(ObjectMapper objectMapper, WorksheetMapping worksheetMapping) {
        this.objectMapper = objectMapper;
        this.worksheetMapping = worksheetMapping;
    }

    public JsonNode importFrom(XSSFSheet worksheet) {
        return objectMapper.createObjectNode();
    }

}
