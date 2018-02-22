package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class WorksheetImporter {

    private final ObjectMapper objectMapper;

    private final WorksheetMapping worksheetMapping;

    public WorksheetImporter(ObjectMapper objectMapper, WorksheetMapping worksheetMapping) {
        this.objectMapper = objectMapper;
        this.worksheetMapping = worksheetMapping;
    }

    public JsonNode importFrom(Sheet worksheet) {
        Row headerRow = worksheet.getRow(2);
        Row dataRow = worksheet.getRow(3);
        ObjectNode objectNode = objectMapper.createObjectNode();
        dataRow.iterator().forEachRemaining(dataCell -> {
            Cell headerCell = headerRow.getCell(dataCell.getColumnIndex());
            String header = headerCell.getStringCellValue();
            CellMapping cellMapping = worksheetMapping.getMappingFor(header);
            cellMapping.importTo(objectNode, dataCell);
        });
        return objectNode;
    }

}