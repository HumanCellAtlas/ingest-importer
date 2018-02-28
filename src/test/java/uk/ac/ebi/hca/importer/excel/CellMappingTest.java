package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonassert.JsonAssert;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.junit.Test;
import uk.ac.ebi.hca.importer.excel.exception.NotAnObjectNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static uk.ac.ebi.hca.importer.excel.CellDataType.*;

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
        Cell juanCell = mock(Cell.class);
        doReturn(CellType.STRING).when(juanCell).getCellTypeEnum();
        String juan = "Juan";
        doReturn(juan).when(juanCell).getStringCellValue();
        firstNameMapping.importTo(node, juanCell);

        //and:
        Cell delaCruzCell = mock(Cell.class);
        doReturn(CellType.STRING).when(delaCruzCell).getCellTypeEnum();
        String delaCruz = "dela Cruz";
        doReturn(delaCruz).when(delaCruzCell).getStringCellValue();
        lastNameMapping.importTo(node, delaCruzCell);

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
        Cell shoppingListCell = mock(Cell.class);
        doReturn(CellType.STRING).when(shoppingListCell).getCellTypeEnum();
        String items = "milk||egg||cereals";
        doReturn(items).when(shoppingListCell).getStringCellValue();
        String shoppingList = "shopping_list";
        CellMapping shoppingListMapping = new CellMapping(shoppingList, STRING_ARRAY);

        //when:
        shoppingListMapping.importTo(node, shoppingListCell);

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
        double cellValue = 12D;
        doReturn(cellValue).when(cell).getNumericCellValue();

        //when:
        quantityMapping.importTo(node, cell);

        //then:
        assertThat(node.has(quantity)).as("quantity field expected").isTrue();
        assertThat(node.get(quantity).asDouble()).isEqualTo(cellValue);
    }

    @Test
    public void testModularFieldImportTo() throws Exception {
        //given:
        ObjectNode node = objectMapper.createObjectNode();

        //and:
        String warrantyLength = "warranty.warranty_length";
        CellMapping numericCellMapping = new CellMapping(warrantyLength, NUMERIC);

        //and:
        Cell numericCell = mock(Cell.class);
        doReturn(CellType.NUMERIC).when(numericCell).getCellTypeEnum();
        double lengthValue = 1D;
        doReturn(lengthValue).when(numericCell).getNumericCellValue();

        //and:
        String warrantyLengthUnit = "warranty.warranty_length_unit";
        CellMapping stringCellMapping = new CellMapping(warrantyLengthUnit, STRING);

        //and:
        Cell stringCell = mock(Cell.class);
        doReturn(CellType.STRING).when(stringCell).getCellTypeEnum();
        String unitValue = "year";
        doReturn(unitValue).when(stringCell).getStringCellValue();

        //and:
        String warrantyExclusions = "warranty.exclusions";
        CellMapping arrayCellMapping = new CellMapping(warrantyExclusions, STRING_ARRAY);

        //and:
        Cell arrayCell = mock(Cell.class);
        doReturn(CellType.STRING).when(arrayCell).getCellTypeEnum();
        doReturn("cosmetic damages||dead pixels").when(arrayCell).getStringCellValue();

        //when:
        numericCellMapping.importTo(node, numericCell);
        stringCellMapping.importTo(node, stringCell);
        arrayCellMapping.importTo(node, arrayCell);

        //then:
        JsonAssert.with(objectMapper.writeValueAsString(node))
                .assertEquals("$.warranty.warranty_length", lengthValue)
                .assertEquals("$.warranty.warranty_length_unit", unitValue)
                .assertThat("$.warranty.exclusions", contains("cosmetic damages", "dead pixels"));
    }

    @Test
    public void testImportToDeeplyNestedField() throws Exception {
        //given:
        ObjectNode node = objectMapper.createObjectNode();

        //and:
        CellMapping ageMapping = new CellMapping("personal.info.age", NUMERIC);
        CellMapping sexMapping = new CellMapping("personal.info.sex", STRING);

        //and:
        Cell ageCell = mock(Cell.class);
        doReturn(CellType.NUMERIC).when(ageCell).getCellTypeEnum();
        double ageValue = 39D;
        doReturn(ageValue).when(ageCell).getNumericCellValue();

        //and:
        Cell sexCell = mock(Cell.class);
        doReturn(CellType.STRING).when(sexCell).getCellTypeEnum();
        String male = "male";
        doReturn(male).when(sexCell).getStringCellValue();

        //when:
        ageMapping.importTo(node, ageCell);
        sexMapping.importTo(node, sexCell);

        //then:
        JsonAssert.with(objectMapper.writeValueAsString(node))
                .assertEquals("$.personal.info.age", ageValue)
                .assertEquals("$.personal.info.sex", male);
    }

    @Test
    public void testAttemptImportToModularFieldOfArrayType() {
        //given: a node with friends array
        ObjectNode node = objectMapper.createObjectNode();
        node.putArray("friends")
                .add("Juan")
                .add("Pedro");

        //and: a cell mapping that attempts to putNext a field under friends property
        CellMapping cellMapping = new CellMapping("friends.count", NUMERIC);

        //and:
        Cell cell = mock(Cell.class);
        doReturn(CellType.NUMERIC).when(cell).getCellTypeEnum();
        doReturn(14D).when(cell).getNumericCellValue();

        //when:
        NotAnObjectNode notAnObjectNode = null;
        try {
            cellMapping.importTo(node, cell);
        } catch (NotAnObjectNode exception) {
            notAnObjectNode = exception;
        } catch (Exception e) {
            fail("Expected to throw NotAnObjectNode exception");
        }

        //then:
        assertThat(notAnObjectNode.getMessage()).contains("[friends] field");
    }

}
