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
        LOGGER.info("\tImporting worksheet " + worksheet.getSheetName());
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
                        LOGGER.warn("\t* Header cell is null in worksheet: " + worksheet.getSheetName() + " col: " + dataCell.getColumnIndex());
                    } else {
                        String header = headerCell.getStringCellValue();
                        CellMapping cellMapping = worksheetMapping.getMappingFor(header);
                        if (cellMapping.isLink) {
                            links.add(createLink(worksheet, id, dataCell, cellMapping, schemaType));

                        } else {
                            if (dataCell.getColumnIndex() == 0) {
                                if (cellMapping.schemaDataType == SchemaDataType.STRING) {
                                    id = dataCell.getStringCellValue();
                                    LOGGER.info("\t- Importing row: " + id);
                                } else {
                                    LOGGER.warn("\t* Don't know how to deal with non string id: " + cellMapping.schemaDataType);
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

    private JsonNode createLink(Sheet worksheet, String id, Cell spreadsheetDataCell, CellMapping cellMapping, String schemaType) {
        if (cellMapping.schemaDataType == SchemaDataType.STRING_ARRAY) {
            spreadsheetDataCell.setCellType(CellType.STRING);
            String[] values = spreadsheetDataCell.getStringCellValue().split("\\|\\|");
            String worksheetName = worksheet.getSheetName();
            ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
            objectNode.put("source_type", schemaType);
            objectNode.put("source_id", id);
            String linkName = cellMapping.jsonProperty;
            String[] parts = linkName.split("_");
            objectNode.put("destination_type", parts[0]);
            ArrayNode destinationNode = JsonNodeFactory.instance.arrayNode();
            for (String value: values)
            {
                destinationNode.add(value);
            }
            objectNode.put("destination_ids", destinationNode);
            return objectNode;
        } else {
            throw new WorksheetImporterException("Don't know how to deal with non string array link: " + cellMapping.schemaDataType);
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

    public class WorksheetImporterException extends RuntimeException {
        public WorksheetImporterException(String message) {
            super(message);
        }
    }

}
