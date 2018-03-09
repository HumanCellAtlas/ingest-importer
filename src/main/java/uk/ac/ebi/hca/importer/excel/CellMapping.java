package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.primitives.Ints;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static uk.ac.ebi.hca.importer.excel.NodeNavigator.navigate;

class CellMapping {

    static final String ARRAY_SEPARATOR = "\\|\\|";

    final String jsonProperty;
    final CellDataType dataType;
    final String ref;

    CellMapping(String jsonProperty, CellDataType dataType, String ref) {
        this.jsonProperty = jsonProperty;
        this.dataType = dataType;
        this.ref = ref;
    }

    CellMapping(String jsonProperty, CellDataType dataType) {
        this.jsonProperty = jsonProperty;
        this.dataType = dataType;
        this.ref = "";
    }

    void importTo(final ObjectNode node, final Cell dataCell) {
        NodeNavigator nodeNavigator = navigate(node).prepareObjectNode(jsonProperty);
        switch (dataType) {
            case INTEGER:
                dataCell.setCellType(CellType.NUMERIC);
                Double numericCellValue = dataCell.getNumericCellValue();
                int intValue = numericCellValue.intValue();
                nodeNavigator.putNext(intValue);
                break;
            case NUMBER:
                dataCell.setCellType(CellType.NUMERIC);
                nodeNavigator.putNext(dataCell.getNumericCellValue());
                break;
            case STRING:
                dataCell.setCellType(CellType.STRING);
                nodeNavigator.putNext(dataCell.getStringCellValue());
                break;
            case STRING_ARRAY:
                dataCell.setCellType(CellType.STRING);
                nodeNavigator.putNext(dataCell.getStringCellValue().split(ARRAY_SEPARATOR));
                break;
            case INTEGER_ARRAY:
                switch (dataCell.getCellTypeEnum()) {
                    case NUMERIC:
                        Double doubleValue = dataCell.getNumericCellValue();
                        int[] intArray = new int[]{doubleValue.intValue()};
                        nodeNavigator.putNext(intArray);
                        break;
                    case STRING:
                        String data = dataCell.getStringCellValue();
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
                        throw new RuntimeException("Unable to process:" + dataCell.getCellTypeEnum());
                }
                break;
            case BOOLEAN:
                switch (dataCell.getCellTypeEnum()) {
                    case STRING:
                        String stringValue = dataCell.getStringCellValue();
                        switch(stringValue) {
                            case "yes":
                                nodeNavigator.putNext(true);
                                break;
                            case "no":
                                nodeNavigator.putNext(false);
                                break;
                            default:
                                throw new RuntimeException("Unable to process: " + dataType + " value: " + stringValue);
                        }
                        break;
                    default:
                        throw new RuntimeException("Unable to process: " + dataType + " " + dataCell.getCellTypeEnum());
                }
                break;
            case ENUM:
                switch (dataCell.getCellTypeEnum()) {
                    case STRING:
                        String stringValue = dataCell.getStringCellValue();
                        nodeNavigator.putNext(stringValue);
                        break;
                    default:
                        throw new RuntimeException("Unable to process: " + dataType + " " + dataCell.getCellTypeEnum());
                }
                break;
            case OBJECT:
                switch (dataCell.getCellTypeEnum()) {
                    case STRING:
                        String stringValue = dataCell.getStringCellValue();
                        nodeNavigator.putNext(stringValue, ref);
                        break;
                    default:
                        throw new RuntimeException("Unable to process: " + dataType + " " + dataCell.getCellTypeEnum());
                }
                break;
            case OBJECT_ARRAY:
                switch (dataCell.getCellTypeEnum()) {
                    case STRING:
                        nodeNavigator.putNext(dataCell.getStringCellValue().split(ARRAY_SEPARATOR), ref);
                        break;
                    default:
                        throw new RuntimeException("Unable to process: " + dataType + " " + dataCell.getCellTypeEnum());
                }
                break;
            default:
                throw new RuntimeException("Unable to process: " + dataType);
        }
    }

    @Override
    public String toString() {
        return "CellMapping{" +
                "jsonProperty='" + jsonProperty + '\'' +
                ", dataType=" + dataType +
                ", ref='" + ref + '\'' +
                '}';
    }
}
