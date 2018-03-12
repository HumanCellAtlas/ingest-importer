package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonassert.JsonAssert;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import uk.ac.ebi.hca.importer.excel.exception.NotAnObjectNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.Matchers.contains;
import static uk.ac.ebi.hca.importer.excel.SchemaDataType.*;

public class CellMappingTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testImportStringType() {
        //given:
        ObjectNode node = objectMapper.createObjectNode();
        Row row = createSampleRow();

        //and:
        String firstName = "first_name";
        CellMapping firstNameMapping = new CellMapping(firstName, STRING);
        String lastName = "last_name";
        CellMapping lastNameMapping = new CellMapping(lastName, STRING);

        //when:
        Cell juanCell = row.createCell(0);
        String juan = "Juan";
        juanCell.setCellValue(juan);
        firstNameMapping.importTo(node, juanCell);

        //and:
        Cell delaCruzCell = row.createCell(1);
        String delaCruz = "dela Cruz";
        delaCruzCell.setCellValue(delaCruz);
        lastNameMapping.importTo(node, delaCruzCell);

        //then:
        assertThat(node.has(firstName)).as("first_name field expected").isTrue();
        assertThat(node.get(firstName).asText()).isEqualTo(juan);

        //and:
        assertThat(node.has(lastName)).as("last_name field expected").isTrue();
        assertThat(node.get(lastName).asText()).isEqualTo(delaCruz);
    }

    @Test
    public void testImportStringArrayType() throws Exception {
        //given:
        ObjectNode node = objectMapper.createObjectNode();
        Row row = createSampleRow();

        //and:
        Cell shoppingListCell = row.createCell(0);
        String items = "milk||egg||cereals";
        shoppingListCell.setCellValue(items);
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
    public void testImportIntegerType() {
        //given:
        ObjectNode node = objectMapper.createObjectNode();
        Row row = createSampleRow();

        //and:
        String quantity = "quantity";
        CellMapping quantityMapping = new CellMapping(quantity, INTEGER);

        //and:
        Cell cell = row.createCell(0);
        double cellValue = 12D;
        cell.setCellValue(cellValue);

        //when:
        quantityMapping.importTo(node, cell);

        //then:
        assertThat(node.has(quantity)).as("quantity field expected").isTrue();
        assertThat(node.get(quantity).asDouble()).isEqualTo(cellValue);
    }

    @Test
    public void testImportIntegerArrayType() throws Exception {
        //given:
        String fibonacci = "fibonacci";
        CellMapping fibonacciMapping = new CellMapping(fibonacci, INTEGER_ARRAY);
        Row row = createSampleRow();

        //and:
        Cell cell = row.createCell(0);
        cell.setCellValue("1||1||2||3||5||8");

        //and:
        ObjectNode node = objectMapper.createObjectNode();

        //when:
        fibonacciMapping.importTo(node, cell);

        //then:
        JsonAssert.with(objectMapper.writeValueAsString(node))
                .assertThat("$.fibonacci", contains(1, 1, 2, 3, 5, 8));
    }

    @Test
    public void testImportIntegerArrayTypeFromNumericCell() throws Exception {
        //given:
        ObjectNode node = objectMapper.createObjectNode();

        //and:
        Cell cell = createSampleRow().createCell(0);
        cell.setCellValue(23.0D);

        //and:
        CellMapping agesMapping = new CellMapping("allowed_ages", INTEGER_ARRAY);

        //when:
        agesMapping.importTo(node, cell);

        //then:
        JsonAssert.with(objectMapper.writeValueAsString(node))
                .assertThat("$.allowed_ages", contains(23));
    }

    @Test
    public void testImportEmptyIntegerArrayType() throws Exception {
        //given:
        String intList = "int_list";
        CellMapping integerListMapping = new CellMapping(intList, INTEGER_ARRAY);

        //and:
        Cell cell = createSampleRow().createCell(0);
        cell.setCellValue("");

        //and:
        ObjectNode node = objectMapper.createObjectNode();

        //when:
        integerListMapping.importTo(node, cell);

        //then:
        JsonAssert.with(objectMapper.writeValueAsString(node))
                .assertNotDefined(intList);
    }

    @Test
    public void testImportModularField() throws Exception {
        //given:
        ObjectNode node = objectMapper.createObjectNode();
        Row row = createSampleRow();

        //and:
        String warrantyLength = "warranty.warranty_length";
        CellMapping integerCellMapping = new CellMapping(warrantyLength, INTEGER);

        //and:
        Cell integerCell = row.createCell(0);
        int lengthValue = 1;
        integerCell.setCellValue(lengthValue);

        //and:
        String warrantyLengthUnit = "warranty.warranty_length_unit";
        CellMapping stringCellMapping = new CellMapping(warrantyLengthUnit, STRING);

        //and:
        Cell stringCell = row.createCell(1);
        String unitValue = "year";
        stringCell.setCellValue(unitValue);

        //and:
        String warrantyExclusions = "warranty.exclusions";
        CellMapping arrayCellMapping = new CellMapping(warrantyExclusions, STRING_ARRAY);

        //and:
        Cell arrayCell = row.createCell(2);
        arrayCell.setCellValue("cosmetic damages||dead pixels");

        //when:
        integerCellMapping.importTo(node, integerCell);
        stringCellMapping.importTo(node, stringCell);
        arrayCellMapping.importTo(node, arrayCell);

        //then:
        JsonAssert.with(objectMapper.writeValueAsString(node))
                .assertEquals("$.warranty.warranty_length", lengthValue)
                .assertEquals("$.warranty.warranty_length_unit", unitValue)
                .assertThat("$.warranty.exclusions", contains("cosmetic damages", "dead pixels"));
    }

    @Test
    public void testImportDeeplyNestedField() throws Exception {
        //given:
        ObjectNode node = objectMapper.createObjectNode();
        Row row = createSampleRow();

        //and:
        Cell ageCell = row.createCell(0);
        int ageValue = 39;
        ageCell.setCellValue(ageValue);


        //and:
        Cell sexCell = row.createCell(1);
        String male = "male";
        sexCell.setCellValue(male);

        //and:
        CellMapping ageMapping = new CellMapping("personal.info.age", INTEGER);
        CellMapping sexMapping = new CellMapping("personal.info.sex", STRING);

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
        CellMapping cellMapping = new CellMapping("friends.count", INTEGER);

        //and:
        Cell cell = createSampleRow().createCell(0);
        cell.setCellValue(14D);

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

    @Test
    public void testImportNumberType() throws Exception {
        //given:
        ObjectNode node = objectMapper.createObjectNode();

        //and:
        Cell cell = createSampleRow().createCell(0);
        double cellValue = 385.402D;
        cell.setCellValue(cellValue);

        //and:
        CellMapping numberCellMapping = new CellMapping("amount", NUMBER);

        //when:
        numberCellMapping.importTo(node, cell);

        //then:
        JsonAssert.with(objectMapper.writeValueAsString(node))
                .assertEquals("$.amount", cellValue);
    }

    private Row createSampleRow() {
        Workbook workbook = new XSSFWorkbook();
        Sheet worksheet = workbook.createSheet("sample");
        return worksheet.createRow(0);
    }

}
