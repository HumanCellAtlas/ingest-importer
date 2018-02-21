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
        String[] propertyChain = jsonProperty.split(PROPERTY_NESTING_DELIMETER);
        int terminalPropertyIndex = propertyChain.length - 1;

        int index = 0;
        JsonNode navigator = node;
        while (index < terminalPropertyIndex && navigator.has(propertyChain[index])) {
            navigator = navigator.get(propertyChain[index]);
            index++;
        }

        ObjectNode moduleNode = (ObjectNode) navigator;
        if (index < terminalPropertyIndex) {
            String property = propertyChain[index];
            moduleNode = moduleNode.putObject(property);
            for (int propertyIndex = index + 1; propertyIndex < terminalPropertyIndex;
                 propertyIndex++) {
                property = propertyChain[propertyIndex];
                moduleNode = moduleNode.putObject(property);
            }
        }

        String moduleProperty = propertyChain[terminalPropertyIndex];

        if (NUMERIC.equals(dataType)) {
            dataCell.setCellType(CellType.NUMERIC);
            moduleNode.put(moduleProperty, dataCell.getNumericCellValue());
        } else {
            dataCell.setCellType(CellType.STRING);
            String data = dataCell.getStringCellValue();
            if (STRING_ARRAY.equals(dataType)) {
                ArrayNode array = node.putArray(jsonProperty);
                Arrays.stream(data.split(ARRAY_SEPARATOR)).forEach(array::add);
            } else {
                moduleNode.put(moduleProperty, data);
            }
        }
    }

}
