package uk.ac.ebi.hca.importer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.hca.importer.excel.WorkbookImporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSampleSpreadsheet {

    @Autowired
    private WorkbookImporter workbookImporter;

    @Autowired
    private ObjectMapper objectMapper;

    private static String SPREADSHEET_URL = "https://github.com/HumanCellAtlas/metadata-schema/blob/master/examples/spreadsheets/v5/filled/SmartSeq2/Q4DemoSS2Metadata_v5.xlsx?raw=true";

    @Test
    @Ignore
    public void testSpreadsheet() throws IOException {
        try (InputStream input = new URL(SPREADSHEET_URL).openStream();
             InputStream expectedFile = TestSampleSpreadsheet.class.getResourceAsStream("/expected-example-spreadsheet.json");
        ) {
            Workbook workbook = new XSSFWorkbook(input);
            List<JsonNode> records = workbookImporter.importFrom(workbook);
            String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(records);
            assertEquals(readFromInputStream(expectedFile), jsonString);
        }
        catch (IOException e) {
            throw e;
        }
    }

    private String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString().trim();
    }
}
