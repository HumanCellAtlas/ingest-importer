package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING;

public class WorksheetImporterTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testImportFrom() throws Exception {
        //given:
        URI spreadsheetUri = ClassLoader.getSystemResource("spreadsheets/v5.xlsx").toURI();
        File spreadsheet = Paths.get(spreadsheetUri).toFile();
        XSSFWorkbook workbook = new XSSFWorkbook(spreadsheet);

        //and:
        XSSFSheet projectWorksheet = workbook.getSheet("project");

        //and:
        WorksheetMapping projectMapping = new WorksheetMapping()
                .map("Project title", "project_title", STRING)
                .map("Project shortname", "project_shortname", STRING);

        //and:
        WorksheetImporter projectImporter = new WorksheetImporter(objectMapper, projectMapping);

        //when:
        JsonNode projectJson = projectImporter.importFrom(projectWorksheet);

        //then:
        assertThat(projectJson).isNotNull();
    }

}
