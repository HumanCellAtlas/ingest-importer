package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonassert.JsonAssert;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.util.Arrays;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class WorkbookImporterTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testImportFrom() throws Exception {
        //given:
        Workbook workbook = new XSSFWorkbook();
        Sheet productWorksheet = workbook.createSheet("product");
        Sheet orderWorksheet = workbook.createSheet("order");

        //and:
        WorksheetImporter productImporter = createProductImporter(new Product("P001", "Milk", 1.09),
                new Product("P002", "Eggs", 1.75), new Product("P003", "Cereal", 2.00));
        WorksheetImporter orderImporter = createOrderImporter(new Order("O001", "P001", 2),
                new Order("O002", "P001", 1), new Order("O003", "P003", 3));

        //and:
        WorkbookImporter workbookImporter = new WorkbookImporter(objectMapper)
                .register(productWorksheet.getSheetName(), productImporter)
                .register(orderWorksheet.getSheetName(), orderImporter);

        //when:
        JsonNode workbookJson = workbookImporter.importFrom(workbook);

        //then:
        assertThat(workbookJson).isNotNull();
        JsonAssert.with(objectMapper.writeValueAsString(workbookJson))
                .assertThat("$.products", hasSize(3))
                .assertEquals("$.products[0].product_id", "P001")
                .assertEquals("$.products[0].name", "Milk")
                .assertEquals("$.products[1].name", "Eggs")
                .assertEquals("$.products[2].price", 2D)
                .assertThat("$.orders", hasSize(3))
                .assertEquals("$.orders[0].product_id", "P001")
                .assertEquals("$.orders[1].order_number", "O002")
                .assertEquals("$.orders[2].quantity", 3);
    }

    @Test
    public void testImportFromWorkbookWithNonRegisteredWorksheet() throws Exception {
        //given:
        Workbook workbook = new XSSFWorkbook();
        Sheet productWorksheet = workbook.createSheet("product");
        Sheet orderWorksheet = workbook.createSheet("order");

        //and:
        WorksheetImporter productImporter = createProductImporter(
                new Product("P211", "Cereal", 2.25));

        //and:
        WorkbookImporter workbookImporter = new WorkbookImporter(objectMapper).register
                (productWorksheet.getSheetName(), productImporter);

        //when:
        JsonNode workbookJson = workbookImporter.importFrom(workbook);

        //then:
        assertThat(workbookJson).isNotNull();
        JsonAssert.with(objectMapper.writeValueAsString(workbookJson))
                .assertThat("$.products", hasSize(1))
                .assertEquals("$.products[0].product_id", "P211")
                .assertEquals("$.products[0].name", "Cereal")
                .assertEquals("$.products[0].price", 2.25)
                .assertNotDefined("$.orders");
    }

    private WorksheetImporter createProductImporter(Product... products) {
        ObjectNode productJson = objectMapper.createObjectNode();
        ArrayNode productArray = productJson.putArray("products");
        Arrays.stream(products).forEach(productArray::addPOJO);
        WorksheetImporter productImporter = mock(WorksheetImporter.class);
        doReturn(productJson).when(productImporter).importFrom(any(Sheet.class));
        return productImporter;
    }

    private WorksheetImporter createOrderImporter(Order... orders) {
        ObjectNode orderJson = objectMapper.createObjectNode();
        ArrayNode orderArray = orderJson.putArray("orders");
        Arrays.stream(orders).forEach(orderArray::addPOJO);
        WorksheetImporter orderImporter = mock(WorksheetImporter.class);
        doReturn(orderJson).when(orderImporter).importFrom(any(Sheet.class));
        return orderImporter;
    }

    private static class Product {

        @JsonProperty("product_id")
        String productId;
        @JsonProperty
        String name;
        @JsonProperty
        Double price; //ideally BigDecimal, but simplified for testing

        Product(String productId, String name, Double price) {
            this.productId = productId;
            this.name = name;
            this.price = price;
        }

    }

    private static class Order {

        @JsonProperty("order_number")
        String orderNumber;
        @JsonProperty("product_id")
        String productId;
        @JsonProperty
        Integer quantity;

        public Order(String orderNumber, String productId, Integer quantity) {
            this.orderNumber = orderNumber;
            this.productId = productId;
            this.quantity = quantity;
        }

    }

}
