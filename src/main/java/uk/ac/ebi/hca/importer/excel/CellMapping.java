package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.primitives.Ints;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.hca.importer.util.MappingUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static uk.ac.ebi.hca.importer.excel.NodeNavigator.navigate;

class CellMapping {

    private static final Logger LOGGER = LoggerFactory.getLogger(CellMapping.class);

    static final String ARRAY_SEPARATOR = "\\|\\|";

    final String jsonProperty;
    final SchemaDataType schemaDataType;
    final String ref;
    final boolean isLink;

    CellMapping(String jsonProperty, SchemaDataType schemaDataType, String ref, boolean isLink) {
        this.jsonProperty = jsonProperty;
        this.schemaDataType = schemaDataType;
        this.ref = ref;
        this.isLink = isLink;
    }

    CellMapping(String jsonProperty, SchemaDataType schemaDataType) {
        this.jsonProperty = jsonProperty;
        this.schemaDataType = schemaDataType;
        this.ref = "";
        this.isLink = false;
    }

    void importTo(final ObjectNode node, final Cell spreadsheetDataCell) {
        NodeNavigator nodeNavigator = navigate(node).prepareObjectNode(jsonProperty);
        switch (schemaDataType) {
            case INTEGER:
                spreadsheetDataCell.setCellType(CellType.NUMERIC);
                Double numericCellValue = spreadsheetDataCell.getNumericCellValue();
                int intValue = numericCellValue.intValue();
                nodeNavigator.putNext(intValue);
                break;
            case NUMBER:
                spreadsheetDataCell.setCellType(CellType.NUMERIC);
                nodeNavigator.putNext(spreadsheetDataCell.getNumericCellValue());
                break;
            case STRING:
                spreadsheetDataCell.setCellType(CellType.STRING);
                nodeNavigator.putNext(spreadsheetDataCell.getStringCellValue());
                break;
            case STRING_ARRAY:
                spreadsheetDataCell.setCellType(CellType.STRING);
                nodeNavigator.putNext(spreadsheetDataCell.getStringCellValue().split(ARRAY_SEPARATOR));
                break;
            case INTEGER_ARRAY:
                switch (spreadsheetDataCell.getCellTypeEnum()) {
                    case NUMERIC:
                        Double doubleValue = spreadsheetDataCell.getNumericCellValue();
                        int[] intArray = new int[]{doubleValue.intValue()};
                        nodeNavigator.putNext(intArray);
                        break;
                    case STRING:
                        String data = spreadsheetDataCell.getStringCellValue();
                        if (!data.isEmpty()) {
                            List<Integer> numericValues = Arrays
                                    .stream(data.split(ARRAY_SEPARATOR))
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList());
                            nodeNavigator.putNext(Ints.toArray(numericValues));
                        }
                        break;
                    case BLANK:
                        break;
                    default:
                        reportFailure(schemaDataType, spreadsheetDataCell.getCellTypeEnum());
                }
                break;
            case BOOLEAN:
                switch (spreadsheetDataCell.getCellTypeEnum()) {
                    case STRING:
                        String stringValue = spreadsheetDataCell.getStringCellValue();
                        switch (stringValue.toLowerCase()) {
                            case "yes":
                                nodeNavigator.putNext(true);
                                break;
                            case "no":
                                nodeNavigator.putNext(false);
                                break;
                            default:
                                reportFailure(schemaDataType, stringValue);
                        }
                        break;
                    default:
                        reportFailure(schemaDataType, spreadsheetDataCell.getCellTypeEnum());
                }
                break;
            case ENUM:
                switch (spreadsheetDataCell.getCellTypeEnum()) {
                    case STRING:
                        String stringValue = spreadsheetDataCell.getStringCellValue();
                        nodeNavigator.putNext(stringValue);
                        break;
                    default:
                        reportFailure(schemaDataType, spreadsheetDataCell.getCellTypeEnum());
                }
                break;
            case OBJECT:
                switch (spreadsheetDataCell.getCellTypeEnum()) {
                    case STRING:
                        String stringValue = spreadsheetDataCell.getStringCellValue();
                        nodeNavigator.putNext(stringValue, ref);
                        break;
                    case BLANK:
                        break;
                    default:
                        reportFailure(schemaDataType, spreadsheetDataCell.getCellTypeEnum());
                }
                break;
            case OBJECT_ARRAY:
                switch (spreadsheetDataCell.getCellTypeEnum()) {
                    case STRING:
                        nodeNavigator.putNext(spreadsheetDataCell.getStringCellValue().split(ARRAY_SEPARATOR), ref);
                        break;
                    default:
                        reportFailure(schemaDataType, spreadsheetDataCell.getCellTypeEnum());
                }
                break;
            default:
                reportFailure(schemaDataType, spreadsheetDataCell.getCellTypeEnum());
        }
    }

    private void reportFailure(SchemaDataType schemaDataType, CellType cellTypeEnum) {
        String message = "Unable to process " + cellTypeEnum + " spreadsheet field as schema data type " + schemaDataType;
        LOGGER.warn(message);
        throw new CellMappingException(message);
    }

    private void reportFailure(SchemaDataType schemaDataType, String value) {
        String message ="Unable to determine schema data type " + schemaDataType + " from " + value;
        LOGGER.warn(message);
        throw new CellMappingException(message);
    }

    @Override
    public String toString() {
        return "CellMapping{" +
                "jsonProperty='" + jsonProperty + '\'' +
                ", schemaDataType=" + schemaDataType +
                ", ref='" + ref + '\'' +
                ", isLink=" + isLink +
                '}';
    }


    public class CellMappingException extends RuntimeException {
        public CellMappingException(String message) {
            super(message);
        }
    }
}
