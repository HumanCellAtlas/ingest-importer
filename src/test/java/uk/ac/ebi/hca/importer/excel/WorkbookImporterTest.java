package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonassert.JsonAssert;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import java.util.List;

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
        Sheet productSheet = mock(Sheet.class);
        doReturn("product").when(productSheet).getSheetName();
        Sheet orderSheet = mock(Sheet.class);
        doReturn("order").when(orderSheet).getSheetName();

        //and:
        Workbook workbook = mock(Workbook.class);
        doReturn(asList(productSheet, orderSheet).iterator()).when(workbook).iterator();

        //and:
        ObjectNode productJson = objectMapper.createObjectNode();
        ArrayNode productArray = productJson.putArray("products");
        List<Product> products = asList(new Product("P001", "Milk", 1.09),
                new Product("P002", "Eggs", 1.75), new Product("P003", "Cereal", 2.00));
        products.forEach(productArray::addPOJO);
        WorksheetImporter productImporter = mock(WorksheetImporter.class);
        doReturn(productJson).when(productImporter).importFrom(any(Sheet.class));

        //and:
        ObjectNode orderJson = objectMapper.createObjectNode();
        ArrayNode orderArray = orderJson.putArray("orders");
        List<Order> orders = asList(new Order("O001", "P001", 2), new Order("O002", "P001", 1),
                new Order("O003", "P003", 3));
        orders.forEach(orderArray::addPOJO);
        WorksheetImporter orderImporter = mock(WorksheetImporter.class);
        doReturn(orderJson).when(orderImporter).importFrom(any(Sheet.class));

        //and:
        WorkbookImporter workbookImporter = new WorkbookImporter(objectMapper);
        workbookImporter.register(productSheet.getSheetName(), productImporter)
                .register(orderSheet.getSheetName(), orderImporter);

        //when:
        JsonNode workbookJson = workbookImporter.importFrom(workbook);

        //then:
        assertThat(workbookJson).isNotNull();
        JsonAssert.with(objectMapper.writeValueAsString(workbookJson))
                .assertThat("$.products", hasSize(products.size()))
                .assertEquals("$.products[0].product_id", "P001")
                .assertEquals("$.products[0].name", "Milk")
                .assertEquals("$.products[1].name", "Eggs")
                .assertEquals("$.products[2].price", 2D)
                .assertThat("$.orders", hasSize(orders.size()))
                .assertEquals("$.orders[0].product_id", "P001")
                .assertEquals("$.orders[1].order_number", "O002")
                .assertEquals("$.orders[2].quantity", 3);
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
