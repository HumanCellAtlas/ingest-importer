package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class WorkbookImporterTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testImportFrom() {
        //given:
        Sheet productSheet = mock(Sheet.class);
        doReturn("product").when(productSheet).getSheetName();
        Sheet orderSheet = mock(Sheet.class);
        doReturn("order").when(orderSheet).getSheetName();

        //and:
        Workbook workbook = mock(Workbook.class);
        doReturn(asList(productSheet, orderSheet).iterator()).when(workbook).iterator();

        //and:
        WorkbookImporter workbookImporter = new WorkbookImporter(objectMapper);

        //when:
        JsonNode workbookJson = workbookImporter.importFrom(workbook);

        //then:
        assertThat(workbookJson).isNotNull();
    }

}
