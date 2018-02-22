package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonassert.JsonAssert;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.runner.RunWith;
import uk.ac.ebi.hca.test.IngestTestRunner;
import uk.ac.ebi.hca.test.IntegrationTest;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static uk.ac.ebi.hca.importer.excel.CellDataType.*;

@RunWith(IngestTestRunner.class)
public class WorksheetImporterTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private WorksheetMapping profileMapping = new WorksheetMapping()
            .map("First Name", "first_name", STRING)
            .map("Last Name", "last_name", STRING)
            .map("Age", "age", NUMERIC)
            .map("Friends", "friends", STRING_ARRAY);

    @IntegrationTest
    public void testImportFrom() throws Exception {
        //given:
        URI spreadsheetUri = ClassLoader.getSystemResource("spreadsheets/generic.xlsx").toURI();
        File spreadsheet = Paths.get(spreadsheetUri).toFile();
        XSSFWorkbook workbook = new XSSFWorkbook(spreadsheet);

        //and:
        XSSFSheet profileWorksheet = workbook.getSheet("Profile");

        //and:
        WorksheetImporter projectImporter = new WorksheetImporter(objectMapper, profileMapping);

        //when:
        JsonNode profile = projectImporter.importFrom(profileWorksheet);

        //then:
        assertThat(profile).isNotNull();
        JsonAssert.with(objectMapper.writeValueAsString(profile))
                .assertEquals("first_name", "Juan")
                .assertEquals("last_name", "dela Cruz")
                .assertEquals("age", 41.0)
                .assertThat("friends", contains("Pedro", "Santiago"))
                .assertEquals("remarks", "This is an extra field")
                .assertEquals("miscellaneous", "looks||like||a||list")
                .assertEquals("extra_number", "123")
                .assertEquals("favorite_languages", "Java||Python")
                .assertEquals("years_of_experience", "20");
    }

    @IntegrationTest
    public void testImportFromModularWorksheet() throws Exception {
        //given:
        URI spreadsheetUri = ClassLoader.getSystemResource("spreadsheets/generic.xlsx").toURI();
        File spreadsheet = Paths.get(spreadsheetUri).toFile();
        XSSFWorkbook workbook = new XSSFWorkbook(spreadsheet);

        //and:
        XSSFSheet profileWorksheet = workbook.getSheet("Profile");

        //and:
        WorksheetMapping modularProfileMapping = profileMapping.copy()
                .map("Developer Grade", "developer.grade", STRING)
                .map("Favorite Languages", "developer.fav_langs", STRING_ARRAY)
                .map("Years of Experience", "developer.years", NUMERIC);

        //and:
        WorksheetImporter worksheetImporter = new WorksheetImporter(objectMapper,
                modularProfileMapping);

        //when:
        JsonNode profileJson = worksheetImporter.importFrom(profileWorksheet);

        //then:
        assertThat(profileJson).isNotNull();
        JsonAssert.with(objectMapper.writeValueAsString(profileJson))
                .assertEquals("$.developer.grade", "Senior")
                .assertThat("$.developer.fav_langs", contains("Java", "Python"))
                .assertEquals("$.developer.years", 20D);
    }

    /*
    TODO add handling for wrongly formatted excel spreadsheet
    For example, when there < 4 rows in the spreadsheet
    */

}
