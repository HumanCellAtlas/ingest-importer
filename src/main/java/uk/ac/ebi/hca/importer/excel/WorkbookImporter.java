package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Workbook;

public class WorkbookImporter {

    private final ObjectMapper objectMapper;

    public WorkbookImporter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode importFrom(Workbook workbook) {
        return objectMapper.createObjectNode();
    }

}
