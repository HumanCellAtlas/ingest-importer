package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class ProjectImporter {

    private static final Map<String, String> HEADER_MAP;

    static {
        HEADER_MAP = new ImmutableMap.Builder<String, String>()
                .put("Project shortname", "project_shortname")
                .put("Project title", "project_title")
                .build();
    }

    @Autowired
    private ObjectMapper objectMapper;

    public JsonNode importFrom(Workbook workbook) {
        Sheet projectSheet = workbook.getSheet("project");
        Row headerRow = projectSheet.getRow(2);
        Row dataRow = projectSheet.getRow(3);
        ObjectNode objectNode = objectMapper.createObjectNode();
        dataRow.iterator().forEachRemaining(dataCell -> {
            Cell headerCell = headerRow.getCell(dataCell.getColumnIndex());
            String key = HEADER_MAP.get(headerCell.getStringCellValue());
            if (key != null) {
                String value = dataCell.getStringCellValue();
                objectNode.put(key, value);
            }
        });
        return objectNode;
    }

}
