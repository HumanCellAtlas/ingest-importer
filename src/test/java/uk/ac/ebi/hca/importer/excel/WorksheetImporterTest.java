package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonassert.JsonAssert;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.runner.RunWith;
import uk.ac.ebi.hca.test.IngestTestRunner;
import uk.ac.ebi.hca.test.IntegrationTest;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
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
        XSSFSheet profileWorksheet = loadGenericWorkbook().getSheet("Profile");

        //and:
        WorksheetImporter projectImporter = new WorksheetImporter(objectMapper, profileMapping);

        //when:
        List<JsonNode> profiles = projectImporter.importFrom(profileWorksheet);

        //then:
        assertThat(profiles).hasSize(3);

        //and:
        Function<String, JsonNode> firstNameFinder = firstNameFinder(profiles);
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
        XSSFSheet profileWorksheet = loadGenericWorkbook().getSheet("Profile");

        //and:
        WorksheetMapping modularProfileMapping = profileMapping.copy()
                .map("Developer Grade", "developer.grade", STRING)
                .map("Favorite Languages", "developer.fav_langs", STRING_ARRAY)
                .map("Years of Experience", "developer.years", NUMERIC);

        //and:
        WorksheetImporter worksheetImporter = new WorksheetImporter(objectMapper,
                modularProfileMapping);

        //when:
        JsonNode profileJson = objectMapper.createObjectNode();

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

    @Deprecated
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

    private Function<String, JsonNode> firstNameFinder(List<JsonNode> profiles) {
        return new Function<String, JsonNode>() {
            @Override
            public JsonNode apply(String firstName) {
                return profiles.stream()
                        .filter(json -> {
                            return json.has("first_name") &&
                                    json.get("first_name").asText().equals(firstName);
                        })
                        .findFirst().orElse(null);
            }
        };
    }

    @IntegrationTest
    public void testImportFromWorksheetUseSetFieldName() throws Exception {
        //given:
        XSSFSheet productWorksheet = loadGenericWorkbook().getSheet("Product");

        //and:
        String products = "products";
        WorksheetImporter worksheetImporter = new WorksheetImporter(objectMapper, products,
                new WorksheetMapping()
                        .map("ID", "id", STRING)
                        .map("NAME", "name", STRING)
                        .map("QUANTITY", "quantity", NUMERIC));

        //when:
        JsonNode productJson = objectMapper.createObjectNode();

        //then:
        JsonAssert.with(objectMapper.writeValueAsString(productJson))
                .assertThat("$.products", hasSize(3))
                .assertEquals("$.products[0].id", "A001")
                .assertEquals("$.products[1].name", "Butter")
                .assertEquals("$.products[2].quantity", 37D);
    }

    @IntegrationTest
    public void testImportWithDeepNesting() throws Exception {
        //given:
        XSSFSheet profileWorksheet = loadGenericWorkbook().getSheet("Profile");

        //and:
        WorksheetMapping deepMapping = profileMapping.copy()
                .map("Developer Grade", "developer.info.grade", STRING)
                .map("Favorite Languages", "developer.preferences.languages", STRING_ARRAY)
                .map("Years of Experience", "developer.info.years", NUMERIC);

        //and:
        WorksheetImporter worksheetImporter = new WorksheetImporter(objectMapper, "profiles",
                deepMapping);

        //when:
        JsonNode profileJson = objectMapper.createObjectNode();

        //then:
        JsonAssert.with(objectMapper.writeValueAsString(profileJson))
                .assertEquals("$.profiles[0].developer.info.grade", "Senior")
                .assertEquals("$.profiles[1].developer.info.years", 2D)
                .assertThat("$.profiles[2].developer.preferences.languages",
                        contains("Haskell", "Perl", "Erlang"));
    }

    @IntegrationTest
    public void testImportWithPredefinedValues() throws Exception {
        //given:
        XSSFSheet profileWorksheet = loadGenericWorkbook().getSheet("Profile");

        //and:
        String schemaUrl = "https://schema.sample.com/profile";
        String schemaVersion = "1.2.7";
        ObjectNode predefinedValues = objectMapper.createObjectNode()
                .put("describedBy", schemaUrl)
                .put("schema_version", schemaVersion);

        //and:
        WorksheetImporter worksheetImporter = new WorksheetImporter(objectMapper, "profiles",
                profileMapping.copy(), predefinedValues);

        //when:
        JsonNode profileJson = objectMapper.createObjectNode();

        //then:
        JsonAssert.with(objectMapper.writeValueAsString(profileJson))
                .assertThat("$.profiles", hasSize(3))
                .assertEquals("$.profiles[0].describedBy", schemaUrl)
                .assertEquals("$.profiles[0].schema_version", schemaVersion)
                .assertEquals("$.profiles[1].describedBy", schemaUrl)
                .assertEquals("$.profiles[1].schema_version", schemaVersion)
                .assertEquals("$.profiles[2].describedBy", schemaUrl)
                .assertEquals("$.profiles[2].schema_version", schemaVersion);
    }

    @IntegrationTest
    public void testImportWithPredefinedValuesInModules() throws Exception {
        //given:
        XSSFSheet profileWorksheet = loadGenericWorkbook().getSheet("Profile");

        //and:
        String description = "developer module";
        String version = "2.2.8";
        ObjectNode modulePredefinedValues = objectMapper.createObjectNode()
                .put("description", description)
                .put("version", version);

        //and:
        WorksheetMapping modularMapping = profileMapping.copy()
                .map("Developer Grade", "developer.grade", STRING)
                .map("Favorite Languages", "developer.languages", STRING_ARRAY)
                .map("Years of Experience", "developer.years", NUMERIC);

        //and:
        WorksheetImporter worksheetImporter = new WorksheetImporter(objectMapper, "profiles",
                modularMapping);
        worksheetImporter.defineValuesFor("developer", modulePredefinedValues);

        //when:
        JsonNode profileJson = objectMapper.createObjectNode();

        //then:
        JsonAssert.with(objectMapper.writeValueAsString(profileJson))
                .assertThat("$.profiles", hasSize(3))
                .assertEquals("$.profiles[0].developer.description", description)
                .assertEquals("$.profiles[0].developer.version", version)
                .assertEquals("$.profiles[1].developer.description", description)
                .assertEquals("$.profiles[1].developer.version", version)
                .assertEquals("$.profiles[2].developer.description", description)
                .assertEquals("$.profiles[2].developer.version", version);
    }

    private XSSFWorkbook loadGenericWorkbook() {
        try {
            URI spreadsheetUri = ClassLoader.getSystemResource("spreadsheets/generic.xlsx").toURI();
            File spreadsheet = Paths
                    .get(spreadsheetUri).toFile();
            return new XSSFWorkbook(spreadsheet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
    TODO add handling for wrongly formatted excel spreadsheet
    For example, when there < 4 rows in the spreadsheet
    */

}
