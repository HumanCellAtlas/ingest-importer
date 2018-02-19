package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonassert.JsonAssert;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static uk.ac.ebi.hca.importer.excel.CellDataType.NUMERIC;
import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING;
import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING_ARRAY;

public class CellMappingTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testStringTypeImportTo() {
        //given:
        ObjectNode node = objectMapper.createObjectNode();

        //and:
        String firstName = "first_name";
        CellMapping firstNameMapping = new CellMapping(firstName, STRING);
        String lastName = "last_name";
        CellMapping lastNameMapping = new CellMapping(lastName, STRING);

        //when:
        String juan = "Juan";
        firstNameMapping.importTo(node, juan);

        //and:
        String delaCruz = "dela Cruz";
        lastNameMapping.importTo(node, delaCruz);

        //then:
        assertThat(node.has(firstName)).as("first_name field expected").isTrue();
        assertThat(node.get(firstName).asText()).isEqualTo(juan);

        //and:
        assertThat(node.has(lastName)).as("last_name field expected").isTrue();
        assertThat(node.get(lastName).asText()).isEqualTo(delaCruz);
    }

    @Test
    public void testStringArrayTypeImportTo() throws Exception {
        //given:
        ObjectNode node = objectMapper.createObjectNode();

        //and:
        String shoppingList = "shopping_list";
        CellMapping shoppingListMapping = new CellMapping(shoppingList, STRING_ARRAY);

        //when:
        shoppingListMapping.importTo(node, "milk||egg||cereals");

        //then:
        assertThat(node.has(shoppingList)).as("shopping_list field expected").isTrue();
        JsonAssert.with(objectMapper.writeValueAsString(node))
                .assertThat(shoppingList, contains("milk", "egg", "cereals"));
    }

    @Test
    public void testNumericTypeImportTo() {
        //given:
        ObjectNode node = objectMapper.createObjectNode();

        //and:
        String quantity = "quantity";
        CellMapping quantityMapping = new CellMapping(quantity, NUMERIC);

        //and:
        Cell cell = mock(Cell.class);
        doReturn(CellType.NUMERIC).when(cell).getCellTypeEnum();
        double cellValue = 12;
        doReturn(cellValue).when(cell).getNumericCellValue();

        //when:
        quantityMapping.importTo(node, cell);

        //then:
        assertThat(node.has(quantity)).as("quantity field expected").isTrue();
        assertThat(node.get(quantity).asDouble()).isEqualTo(cellValue);
    }

}
