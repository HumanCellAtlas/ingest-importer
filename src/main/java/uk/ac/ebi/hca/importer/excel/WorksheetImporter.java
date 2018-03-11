package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static uk.ac.ebi.hca.importer.excel.NodeNavigator.navigate;

public class WorksheetImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorksheetImporter.class);

    private final WorksheetMapping worksheetMapping;

    private final JsonNode predefinedValues;

    private final Map<String, JsonNode> modulePredefinedValues = new HashMap<>();

    public WorksheetImporter(ObjectMapper objectMapper, WorksheetMapping worksheetMapping) {
        this(worksheetMapping, objectMapper.createObjectNode());
    }

    public WorksheetImporter(WorksheetMapping worksheetMapping, ObjectNode predefinedValues) {
        this.worksheetMapping = worksheetMapping;
        this.predefinedValues = predefinedValues;
    }

    public ObjectNode importFrom(Sheet worksheet) {
        String schemaType = predefinedValues.get("schema_type").textValue();
        ArrayNode nodes = JsonNodeFactory.instance.arrayNode();
        ArrayNode links = JsonNodeFactory.instance.arrayNode();
        Row headerRow = worksheet.getRow(2);
        for (int rowIndex = 3; rowIndex <= worksheet.getLastRowNum(); rowIndex++) {
            ObjectNode rowJson = predefinedValues.deepCopy();
            Row row = worksheet.getRow(rowIndex);
            if (row != null && !isRowEmpty(row)) {
                String id = "";
                for (Cell dataCell : row) {
                    Cell headerCell = headerRow.getCell(dataCell.getColumnIndex());
                    if (headerCell == null) {
                        LOGGER.warn("Header cell is null in worksheet: " + worksheet.getSheetName() + " col: " + dataCell.getColumnIndex());
                    } else {
                        String header = headerCell.getStringCellValue();
                        CellMapping cellMapping = worksheetMapping.getMappingFor(header);
                        if (cellMapping.isLink) {
                            links.add(createLink(worksheet, id, dataCell, cellMapping));

                        } else {
                            if (dataCell.getColumnIndex() == 0) {
                                if (cellMapping.dataType == SchemaDataType.STRING) {
                                    id = dataCell.getStringCellValue();
                                } else {
                                    LOGGER.warn("Don't know how to deal with non string id: " + cellMapping.dataType);
                                }
                            }
                            cellMapping.importTo(rowJson, dataCell);
                        }
                    }
                }
                ;
                modulePredefinedValues.forEach((module, json) -> {
                    navigate(rowJson).moveTo(module).addValuesFrom(json);
                });
                nodes.add(rowJson);
            }
        }
        ObjectNode resultNode = JsonNodeFactory.instance.objectNode();
        resultNode.put("schema_type", schemaType);
        resultNode.set("content", nodes);
        if (links.size()>0) {
            resultNode.set("links", links);
        }
        return resultNode;
    }

    private JsonNode createLink(Sheet worksheet, String id, Cell dataCell, CellMapping cellMapping) {
        if (cellMapping.dataType == SchemaDataType.STRING) {
            dataCell.setCellType(CellType.STRING);
            String value = dataCell.getStringCellValue();
            String worksheetName = worksheet.getSheetName();
            ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
            objectNode.put("source_type", worksheetName);
            objectNode.put("source_id", id);
            objectNode.put("destination_type", cellMapping.jsonProperty);
            objectNode.put("destination_id", value);
            return objectNode;
        } else {
            throw new RuntimeException("Don't know how to deal with non string link: " + cellMapping.dataType);
        }
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
