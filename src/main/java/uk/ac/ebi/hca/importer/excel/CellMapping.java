package uk.ac.ebi.hca.importer.excel;

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

        ObjectNode moduleNode = node.putObject(propertyChain[0]);
        for (int propertyIndex = 1; propertyIndex < propertyChain.length - 1; propertyIndex++) {
            moduleNode = moduleNode.putObject(propertyChain[propertyIndex]);
        }

        String moduleProperty = propertyChain[propertyChain.length - 1];

        if (NUMERIC.equals(dataType)) {
            dataCell.setCellType(CellType.NUMERIC);
            moduleNode.put(moduleProperty, dataCell.getNumericCellValue());
        } else {
            dataCell.setCellType(CellType.STRING);
            importTo(node, dataCell.getStringCellValue());
        }
    }

    void importTo(final ObjectNode node, final String data) {
        if (STRING_ARRAY.equals(dataType)) {
            ArrayNode array = node.putArray(jsonProperty);
            Arrays.stream(data.split(ARRAY_SEPARATOR)).forEach(array::add);
        } else {
            node.put(jsonProperty, data);
        }
    }

}
