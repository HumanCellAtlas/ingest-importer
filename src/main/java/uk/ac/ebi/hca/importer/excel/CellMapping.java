package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import uk.ac.ebi.hca.importer.excel.exception.NotAnObjectNode;

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
        NodeNavigator nodeNavigator = new NodeNavigator(node).prepareObjectNode(jsonProperty);
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

        JsonNode currentNode;

        String currentProperty;

        NodeNavigator(ObjectNode currentNode) {
            this.currentNode = currentNode;
            this.currentProperty = "";
        }

        NodeNavigator prepareObjectNode(String jsonProperty) {
            String[] propertyChain = jsonProperty.split(PROPERTY_NESTING_DELIMETER);

            int indexOfLastKnownNode = moveToLastExistentObjectNode(propertyChain);

            ObjectNode pointerNode = null;
            if (currentNode.isObject()) {
                pointerNode = (ObjectNode) currentNode;
            } else {
                throw new NotAnObjectNode(Arrays.copyOfRange(propertyChain, 0,
                        indexOfLastKnownNode));
            }

            int terminalPropertyIndex = propertyChain.length - 1;
            if (indexOfLastKnownNode < terminalPropertyIndex) {
                int propertyIndex = indexOfLastKnownNode;
                do {
                    String property = propertyChain[indexOfLastKnownNode];
                    pointerNode = pointerNode.putObject(property);
                    propertyIndex++;
                } while (propertyIndex < terminalPropertyIndex);
            }
            currentNode = pointerNode;
            currentProperty = propertyChain[terminalPropertyIndex];
            return this;
        }

        private int moveToLastExistentObjectNode(String[] propertyChain) {
            int terminalPropertyIndex = propertyChain.length - 1;
            int index = 0;
            JsonNode navigator = currentNode;
            while (index < terminalPropertyIndex && navigator.has(propertyChain[index])) {
                navigator = navigator.get(propertyChain[index]);
                index++;
            }

            currentNode = navigator;
            return index;
        }

        void put(double value) {
            ((ObjectNode) currentNode).put(currentProperty, value);
        }

        void put(String value) {
            ((ObjectNode) currentNode).put(currentProperty, value);
        }

        void put(String[] value) {
            ArrayNode array = ((ObjectNode) currentNode).putArray(currentProperty);
            Arrays.stream(value).forEach(array::add);
        }

    }

}
