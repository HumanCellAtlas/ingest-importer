package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonassert.JsonAssert;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static uk.ac.ebi.hca.importer.excel.CellDataType.NUMERIC;
import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING;
import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING_ARRAY;

public class WorksheetImporterTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testImportFrom() throws Exception {
        //given:
        URI spreadsheetUri = ClassLoader.getSystemResource("spreadsheets/generic.xlsx").toURI();
        File spreadsheet = Paths.get(spreadsheetUri).toFile();
        XSSFWorkbook workbook = new XSSFWorkbook(spreadsheet);

        //and:
        XSSFSheet profileWorksheet = workbook.getSheet("Profile");

        //and:
        WorksheetMapping projectMapping = new WorksheetMapping()
                .map("First Name", "first_name", STRING)
                .map("Last Name", "last_name", STRING)
                .map("Age", "age", NUMERIC)
                .map("Friends", "friends", STRING_ARRAY);

        //and:
        WorksheetImporter projectImporter = new WorksheetImporter(objectMapper, projectMapping);

        //when:
        JsonNode projectJson = projectImporter.importFrom(profileWorksheet);

        //then:
        assertThat(projectJson).isNotNull();
        JsonAssert.with(objectMapper.writeValueAsString(projectJson))
                .assertEquals("first_name", "Juan")
                .assertEquals("last_name", "dela Cruz")
                .assertEquals("age", 41.0)
                .assertThat("friends", contains("Pedro", "Santiago"))
                .assertEquals("remarks", "This is an extra field")
                .assertEquals("miscellaneous", "looks||like||a||list")
                .assertEquals("extra_number", "123");
    }

}
