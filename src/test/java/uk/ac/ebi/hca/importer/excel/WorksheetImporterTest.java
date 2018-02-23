package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonassert.JsonAssert;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.runner.RunWith;
import uk.ac.ebi.hca.test.IngestTestRunner;
import uk.ac.ebi.hca.test.IntegrationTest;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static uk.ac.ebi.hca.importer.excel.CellDataType.*;

@RunWith(IngestTestRunner.class)
public class WorksheetImporterTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    //TODO add container array name in the worksheet mapping (e.g. new WorksheetMapping("profiles")
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
        JsonNode profileJson = projectImporter.importFrom(profileWorksheet);

        //then:
        assertThat(profileJson).isNotNull();
        assertThat(profileJson.has("Profile")).as("Expected field [Profile].").isTrue();
        assertThat(profileJson.get("Profile").isArray()).isTrue();
        JsonAssert.with(objectMapper.writeValueAsString(profileJson))
                .assertThat("Profile", hasSize(3));

        //and:
        ArrayNode profileArray = (ArrayNode) profileJson.get("Profile");
        Function<String, JsonNode> firstNameFinder = firstNameFinder(profileArray);
        assertCorrectJuanProfile(firstNameFinder.apply("Juan"));
        assertCorrectJohnProfile(firstNameFinder.apply("John"));
        assertCorrectMaryProfile(firstNameFinder.apply("Mary"));
    }

    private void assertCorrectJuanProfile(JsonNode juan) throws JsonProcessingException {
        JsonAssert.with(objectMapper.writeValueAsString(juan))
                .assertEquals("first_name", "Juan")
                .assertEquals("last_name", "dela Cruz")
                .assertEquals("age", 41D)
                .assertThat("friends", contains("Pedro", "Santiago"))
                .assertEquals("remarks", "This is an extra field")
                .assertEquals("miscellaneous", "looks||like||a||list")
                .assertEquals("extra_number", "123")
                .assertEquals("developer_grade", "Senior")
                .assertEquals("favorite_languages", "Java||Python")
                .assertEquals("years_of_experience", "20");
    }

    private void assertCorrectJohnProfile(JsonNode john) throws JsonProcessingException {
        JsonAssert.with(objectMapper.writeValueAsString(john))
                .assertEquals("first_name", "John")
                .assertEquals("last_name", "Doe")
                .assertEquals("age", 23D)
                .assertThat("friends", contains("Jessica", "Kaz", "David"))
                .assertNotDefined("remarks")
                .assertNotDefined("miscellaneous")
                .assertNotDefined("extra_number")
                .assertEquals("developer_grade", "Junior")
                .assertEquals("favorite_languages", "Python")
                .assertEquals("years_of_experience", "2");
    }

    private void assertCorrectMaryProfile(JsonNode mary) throws JsonProcessingException {
        JsonAssert.with(objectMapper.writeValueAsString(mary))
                .assertEquals("first_name", "Mary")
                .assertEquals("last_name", "Moon")
                .assertEquals("age", 25D)
                .assertThat("friends", contains("Vegetables"))
                .assertEquals("remarks", "She's a vegetarian")
                .assertNotDefined("miscellaneous")
                .assertNotDefined("extra_number")
                .assertEquals("developer_grade", "Mid")
                .assertEquals("favorite_languages", "Haskell||Perl||Erlang")
                .assertEquals("years_of_experience", "5");
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
        assertThat(profileJson.has("Profile")).as("Expected [Profile] field.");

        //and:
        assertThat(profileJson.get("Profile").isArray())
                .as("[Profile] should be an array.")
                .isTrue();
        ArrayNode profileArray = (ArrayNode) profileJson.get("Profile");

        //and:
        Function<String, JsonNode> firstNameFinder = firstNameFinder(profileArray);
        JsonAssert.with(objectMapper.writeValueAsString(firstNameFinder.apply("Juan")))
                .assertEquals("$.developer.grade", "Senior")
                .assertThat("$.developer.fav_langs", contains("Java", "Python"))
                .assertEquals("$.developer.years", 20D);
    }

    //aren't we overdoing encapsulation here?
    private Function<String, JsonNode> firstNameFinder(ArrayNode profileArray) {
        return new Function<String, JsonNode>() {
            @Override
            public JsonNode apply(String firstName) {
                return StreamSupport
                        .stream(profileArray.spliterator(), false)
                        .filter(node -> {
                            return node.has("first_name") &&
                                    node.get("first_name").asText().equals(firstName);
                        })
                        .findFirst().orElse(null);
            }
        };
    }

    /*
    TODO add handling for wrongly formatted excel spreadsheet
    For example, when there < 4 rows in the spreadsheet
    */

}
