package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonassert.JsonAssert;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
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
        WorksheetImporter productImporter = mockImporter(new Product("P001", "Milk", 1.09),
                new Product("P002", "Eggs", 1.75), new Product("P003", "Cereal", 2.00));
        WorksheetImporter orderImporter = mockImporter(new Order("O001", "P001", 2),
                new Order("O002", "P001", 1), new Order("O003", "P003", 3));

        //and:
        WorkbookImporter workbookImporter = new WorkbookImporter(objectMapper)
                .register(productWorksheet.getSheetName(), productImporter)
                .register(orderWorksheet.getSheetName(), orderImporter);

        //when:
        List<JsonNode> records = workbookImporter.importFrom(workbook);

        //then:
        assertThat(records).hasSize(6);

        //and:
        List<JsonNode> productRecords = records.stream()
                .filter(json -> json.has("product_id") && json.has("name"))
                .collect(toList());
        JsonAssert.with(objectMapper.writeValueAsString(productRecords))
                .assertThat("$", hasSize(3))
                .assertEquals("$[0].product_id", "P001")
                .assertEquals("$[0].name", "Milk")
                .assertEquals("$[1].name", "Eggs")
                .assertEquals("$[2].price", 2D);

        //and:
        List<JsonNode> orderRecords = records
                .stream()
                .filter(json -> json.has("order_number"))
                .collect(toList());
        JsonAssert.with(objectMapper.writeValueAsString(orderRecords))
                .assertThat("$", hasSize(3))
                .assertEquals("$[0].product_id", "P001")
                .assertEquals("$[1].order_number", "O002")
                .assertEquals("$[2].quantity", 3);
    }

    @Test
    public void testImportFromWorkbookWithNonRegisteredWorksheet() throws Exception {
        //given:
        Workbook workbook = new XSSFWorkbook();
        Sheet productWorksheet = workbook.createSheet("product");

        //and:
        WorksheetImporter productImporter = mockImporter(new Product("P211", "Cereal", 2.25));

        //and:
        WorkbookImporter workbookImporter = new WorkbookImporter(objectMapper)
                .register(productWorksheet.getSheetName(), productImporter);

        //when:
        List<JsonNode> records = workbookImporter.importFrom(workbook);

        //then:
        assertThat(records).hasSize(1);

        //and:
        List<JsonNode> productRecords = records.stream()
                .filter(json -> json.has("product_id") && json.has("name"))
                .collect(toList());
        JsonAssert.with(objectMapper.writeValueAsString(productRecords))
                .assertThat("$", hasSize(1))
                .assertEquals("[0].product_id", "P211")
                .assertEquals("$[0].name", "Cereal")
                .assertEquals("$[0].price", 2.25);
    }

    private WorksheetImporter mockImporter(Object... objects) {
        List<JsonNode> list = new ArrayList<>();
        Arrays.stream(objects)
                .map(objectMapper::<JsonNode>valueToTree)
                .forEach(list::add);
        WorksheetImporter worksheetImporter = mock(WorksheetImporter.class);
        doReturn(list).when(worksheetImporter).importFrom(any(Sheet.class));
        return worksheetImporter;
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
