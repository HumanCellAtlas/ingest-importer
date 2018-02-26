package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.stream.IntStream;

public class WorksheetImporter {

    private final ObjectMapper objectMapper;

    private final WorksheetMapping worksheetMapping;

    public WorksheetImporter(ObjectMapper objectMapper, WorksheetMapping worksheetMapping) {
        this.objectMapper = objectMapper;
        this.worksheetMapping = worksheetMapping;
    }

    public JsonNode importFrom(Sheet worksheet) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        String arrayName = worksheetMapping.hasName() ? worksheetMapping.getName() :
                worksheet.getSheetName();
        ArrayNode arrayNode = objectNode.putArray(arrayName);

        Row headerRow = worksheet.getRow(2);
        for (int row = 3; row <= worksheet.getLastRowNum(); row++) {
            ObjectNode rowJson = objectMapper.createObjectNode();
            worksheet.getRow(row).iterator().forEachRemaining(dataCell -> {
                Cell headerCell = headerRow.getCell(dataCell.getColumnIndex());
                String header = headerCell.getStringCellValue();
                CellMapping cellMapping = worksheetMapping.getMappingFor(header);
                cellMapping.importTo(rowJson, dataCell);
            });
            arrayNode.add(rowJson);
        }

        return objectNode;
    }

}
