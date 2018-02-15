package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;

public class ProjectImporter {

    @Autowired
    private ObjectMapper objectMapper;

    public JsonNode importFrom(Workbook workbook) {
        Sheet projectSheet = workbook.getSheet("project");
        projectSheet.getRow(3);
        return objectMapper.createObjectNode().put("project_shortname", "DEMO-ProjectShortname");
    }

}
