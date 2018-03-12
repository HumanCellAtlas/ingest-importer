package uk.ac.ebi.hca.importer;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.hca.importer.excel.WorkbookImporter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestProcessingOfSampleSpreadsheets {

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
    public void test_pbmc8k_v5_spreadsheet_matched_expected_output_and_is_valid_against_schema() throws IOException, ProcessingException {
        checkSpreadsheetValidates(pbmc8k_SPREADSHEET_URL, Glioblastoma_v5_EXPECTED_JSON_URL, "src/test/resources/output/pbmc8k_v5.json");
    }

    @Test
    @Ignore
    public void test_Glioblastoma_v5_spreadsheet_matched_expected_output_and_is_valid_against_schema() throws IOException, ProcessingException {
        checkSpreadsheetValidates(Glioblastoma_v5_SPREADSHEET_URL, Glioblastoma_v5_EXPECTED_JSON_URL, "src/test/resources/output/Glioblastoma_v5.json");
    }

    @Test
    public void test_Glioblastoma_v5_single_spreadsheet_matched_expected_output_and_is_valid_against_schema() throws IOException, ProcessingException {
        checkSpreadsheetValidates(Glioblastoma_v5_single_SPREADSHEET_URL, Glioblastoma_v5_single_EXPECTED_JSON_URL, "src/test/resources/output/Glioblastoma_v5_single.json");
    }

    @Test
    public void test_Q4DemoSS2Metadata_v5_spreadsheet_matched_expected_output_and_is_valid_against_schema() throws IOException, ProcessingException {
        checkSpreadsheetValidates(Q4DemoSS2Metadata_v5_SPREADSHEET_URL, Q4DemoSS2Metadata_v5_EXPECTED_JSON_URL, "src/test/resources/output/Q4DemoSS2Metadata_v5.json");
    }

    private void checkSpreadsheetValidates(String inputUrl, String expectedFileUrl, String outputFile) throws ProcessingException, IOException {
        try (InputStream input = new URL(inputUrl).openStream();
             //InputStream expectedFile = new URL(expectedFileUrl).openStream()
        ) {
            Workbook workbook = new XSSFWorkbook(input);
            List<ObjectNode> records = workbookImporter.importFrom(workbook, inputUrl);
            ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
            String jsonString = writer.writeValueAsString(records);
            writer.writeValue(new File(outputFile), records);
            final JsonNode jsonNode = new ObjectMapper().readTree(jsonString);
            JsonNode outputSchema = JsonLoader.fromResource("/output-schema.json");
            ProcessingReport processingReport = VALIDATOR.validate(outputSchema, jsonNode);
            for (ProcessingMessage processingMessage : processingReport) {
                System.out.println(processingMessage.toString());
            }
            assertTrue(processingReport.isSuccess());
        } catch (IOException e) {
            throw e;
        }
    }

}
