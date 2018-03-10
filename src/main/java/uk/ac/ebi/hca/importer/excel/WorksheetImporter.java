package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.hca.importer.excel.NodeNavigator.navigate;

public class WorksheetImporter {

    private final ObjectMapper objectMapper;

    private final WorksheetMapping worksheetMapping;

    private final JsonNode predefinedValues;

    private final Map<String, JsonNode> modulePredefinedValues = new HashMap<>();

    public WorksheetImporter(ObjectMapper objectMapper, WorksheetMapping worksheetMapping) {
        this(objectMapper, worksheetMapping, objectMapper.createObjectNode());
    }

    public WorksheetImporter(ObjectMapper objectMapper, WorksheetMapping worksheetMapping,
            ObjectNode predefinedValues) {
        this.objectMapper = objectMapper;
        this.worksheetMapping = worksheetMapping;
        this.predefinedValues = predefinedValues;
    }

    public List<JsonNode> importFrom(Sheet worksheet) {
        List<JsonNode> nodes = new ArrayList<>();

        Row headerRow = worksheet.getRow(2);
        for (int rowIndex = 3; rowIndex <= worksheet.getLastRowNum(); rowIndex++) {
            ObjectNode rowJson = predefinedValues.deepCopy();
            Row row = worksheet.getRow(rowIndex);
            if (row != null && !isRowEmpty(row)) {
                row.iterator().forEachRemaining(dataCell -> {
                    Cell headerCell = headerRow.getCell(dataCell.getColumnIndex());
                    String header = headerCell.getStringCellValue();
                    CellMapping cellMapping = worksheetMapping.getMappingFor(header);
                    cellMapping.importTo(rowJson, dataCell);
                });
                modulePredefinedValues.forEach((module, json) -> {
                    navigate(rowJson).moveTo(module).addValuesFrom(json);
                });
                nodes.add(rowJson);
            }
        }

        return nodes;
    }

    public static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellTypeEnum() != CellType.BLANK)
                return false;
        }
        return true;
    }

    public void defineValuesFor(String module, JsonNode predefinedValues) {
        modulePredefinedValues.put(module, predefinedValues);
    }

}
