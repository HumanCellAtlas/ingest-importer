package uk.ac.ebi.hca.importer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.hca.importer.excel.WorkbookImporter;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleSpreadsheetsImportTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            SampleSpreadsheetsImportTest.class);

    private static final JsonValidator VALIDATOR = JsonSchemaFactory.byDefault().getValidator();

    private static final String Glioblastoma_v5_EXPECTED_JSON_URL = "https://github.com/HumanCellAtlas/metadata-schema/blob/develop/examples/JSON/v5/SmartSeq2/Glioblastoma.json?raw=true";
    private static final String Glioblastoma_v5_single_EXPECTED_JSON_URL = "https://github.com/HumanCellAtlas/metadata-schema/blob/develop/examples/JSON/v5/SmartSeq2/Glioblastoma_single.json?raw=true";
    private static final String Q4DemoSS2Metadata_v5_EXPECTED_JSON_URL = "https://raw.githubusercontent.com/HumanCellAtlas/metadata-schema/develop/examples/JSON/v5/SmartSeq2/Q4DemoSS2Metadata_v5.json";
    private static final String Q4DemoSS2Metadata_v5_SPREADSHEET_URL = "https://github.com/HumanCellAtlas/metadata-schema/blob/master/examples/spreadsheets/v5/filled/SmartSeq2/Q4DemoSS2Metadata_v5.xlsx?raw=true";
    private static final String Glioblastoma_v5_SPREADSHEET_URL = "https://github.com/HumanCellAtlas/metadata-schema/blob/master/examples/spreadsheets/v5/filled/SmartSeq2/Glioblastoma.xlsx?raw=true";
    private static final String Glioblastoma_v5_single_SPREADSHEET_URL = "https://github.com/HumanCellAtlas/metadata-schema/blob/develop/examples/spreadsheets/v5/filled/SmartSeq2/Glioblastoma_single.xlsx?raw=true";
    private static final String pbmc8k_SPREADSHEET_URL = "https://github.com/HumanCellAtlas/metadata-schema/blob/develop/examples/spreadsheets/v5/filled/10x_v2/pbmc8k.xlsx?raw=true";
    private static final String pbmc8k_EXPECTED_JSON_URL = "";

    @Autowired
    private WorkbookImporter workbookImporter;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Ignore
    public void testPbmc8kSample() {
        assertCorrectOutput(pbmc8k_SPREADSHEET_URL, Glioblastoma_v5_EXPECTED_JSON_URL,
                "pbmc8k_v5.json");
    }

    @Test
    @Ignore
    public void testGlioblastomaSample() {
        assertCorrectOutput(Glioblastoma_v5_SPREADSHEET_URL,
                Glioblastoma_v5_EXPECTED_JSON_URL, "Glioblastoma_v5.json");
    }

    @Test
    public void testGlioblastomaSingleSample() {
        assertCorrectOutput(Glioblastoma_v5_single_SPREADSHEET_URL,
                Glioblastoma_v5_single_EXPECTED_JSON_URL, "Glioblastoma_v5_single.json");
    }

    @Test
    public void testQ4DemoSample() {
        assertCorrectOutput(Q4DemoSS2Metadata_v5_SPREADSHEET_URL,
                Q4DemoSS2Metadata_v5_EXPECTED_JSON_URL, "Q4DemoSS2Metadata_v5.json");
    }

    private void assertCorrectOutput(String inputUrl, String expectedFileUrl, String outputFile) {
        try (InputStream input = new URL(inputUrl).openStream()) {
            String jsonString = doImport(input, outputFile);
            final JsonNode jsonNode = new ObjectMapper().readTree(jsonString);
            JsonNode outputSchema = JsonLoader.fromResource("/output-schema.json");
            ProcessingReport processingReport = VALIDATOR.validate(outputSchema, jsonNode);
            processingReport.iterator()
                    .forEachRemaining(message -> LOGGER.info(message.toString()));
            assertThat(processingReport.isSuccess())
                    .as("Resulting JSON should validate against the schema.")
                    .isTrue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String doImport(InputStream input, String fileName) throws Exception {
        Workbook workbook = new XSSFWorkbook(input);
        List<ObjectNode> records = workbookImporter.importFrom(workbook);
        ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
        String jsonString = writer.writeValueAsString(records);
        writeOutputToFile(fileName, records, writer);
        return jsonString;
    }

    private void writeOutputToFile(String fileName, List<ObjectNode> records, ObjectWriter writer)
            throws Exception {
        Path outputFilePath = Paths.get("tmp/integration-test/" + fileName);
        File outputFile = outputFilePath.toFile();
        if (!outputFilePath.toFile().exists()) {
            Path outputDirectoryPath = outputFilePath.getParent();
            if (!outputDirectoryPath.toFile().exists()) {
                Files.createDirectories(outputDirectoryPath);
            }
            outputFile = Files.createFile(outputFilePath).toFile();
        }
        writer.writeValue(outputFile, records);
    }

}
