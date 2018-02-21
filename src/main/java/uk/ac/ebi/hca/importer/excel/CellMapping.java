package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Arrays;

import static uk.ac.ebi.hca.importer.excel.CellDataType.NUMERIC;
import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING_ARRAY;

class CellMapping {

    static final String PROPERTY_NESTING_DELIMETER = "\\.";
    static final String ARRAY_SEPARATOR = "\\|\\|";

    final String jsonProperty;
    final CellDataType dataType;

    CellMapping(String jsonProperty, CellDataType dataType) {
        this.jsonProperty = jsonProperty;
        this.dataType = dataType;
    }

    static CellMapping map(String jsonProperty, CellDataType dataType) {
        return new CellMapping(jsonProperty, dataType);
    }

    void importTo(final ObjectNode node, final Cell dataCell) {
        NodeNavigator nodeNavigator = new NodeNavigator(node).navigateTo(jsonProperty);
        if (NUMERIC.equals(dataType)) {
            dataCell.setCellType(CellType.NUMERIC);
            nodeNavigator.put(dataCell.getNumericCellValue());
        } else {
            dataCell.setCellType(CellType.STRING);
            String data = dataCell.getStringCellValue();
            if (STRING_ARRAY.equals(dataType)) {
                nodeNavigator.put(data.split(ARRAY_SEPARATOR));
            } else {
                nodeNavigator.put(data);
            }
        }
    }

    private static class NodeNavigator {

        ObjectNode currentNode;

        String currentProperty;

        NodeNavigator(ObjectNode currentNode) {
            this.currentNode = currentNode;
            this.currentProperty = "";
        }

        NodeNavigator navigateTo(String jsonProperty) {
            String[] propertyChain = jsonProperty.split(PROPERTY_NESTING_DELIMETER);

            int indexOfLastKnownNode = moveToLastExistentNode(propertyChain);
            int terminalPropertyIndex = propertyChain.length - 1;
            if (indexOfLastKnownNode < terminalPropertyIndex) {
                int propertyIndex = indexOfLastKnownNode;
                do {
                    String property = propertyChain[indexOfLastKnownNode];
                    currentNode = currentNode.putObject(property);
                    propertyIndex++;
                } while (propertyIndex < terminalPropertyIndex);
            }

            currentProperty = propertyChain[terminalPropertyIndex];
            return this;
        }

        private int moveToLastExistentNode(String[] propertyChain) {
            int terminalPropertyIndex = propertyChain.length - 1;
            int index = 0;
            JsonNode navigator = currentNode;
            while (index < terminalPropertyIndex && navigator.has(propertyChain[index])) {
                navigator = navigator.get(propertyChain[index]);
                index++;
            }
            currentNode = (ObjectNode) navigator;
            return index;
        }

        void put(double value) {
            currentNode.put(currentProperty, value);
        }

        void put(String value) {
            currentNode.put(currentProperty, value);
        }

        void put(String[] value) {
            ArrayNode array = currentNode.putArray(currentProperty);
            Arrays.stream(value).forEach(array::add);
        }

    }

}
