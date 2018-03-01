package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static uk.ac.ebi.hca.importer.excel.CellDataType.NUMERIC;
import static uk.ac.ebi.hca.importer.excel.CellDataType.NUMERIC_ARRAY;
import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING_ARRAY;
import static uk.ac.ebi.hca.importer.excel.NodeNavigator.navigate;

class CellMapping {

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
        NodeNavigator nodeNavigator = navigate(node).prepareObjectNode(jsonProperty);
        if (NUMERIC.equals(dataType)) {
            dataCell.setCellType(CellType.NUMERIC);
            nodeNavigator.putNext(dataCell.getNumericCellValue());
        } else {
            dataCell.setCellType(CellType.STRING);
            String data = dataCell.getStringCellValue();
            if (STRING_ARRAY.equals(dataType)) {
                nodeNavigator.putNext(data.split(ARRAY_SEPARATOR));
            } else if (NUMERIC_ARRAY.equals(dataType)) {
                List<Integer> numericValues = Arrays
                        .stream(data.split(ARRAY_SEPARATOR))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                nodeNavigator.putNext(Ints.toArray(numericValues));
            } else {
                nodeNavigator.putNext(data);
            }
        }
    }

    @Override
    public String toString() {
        return "CellMapping{" +
                "jsonProperty='" + jsonProperty + '\'' +
                ", dataType=" + dataType +
                '}';
    }
}
