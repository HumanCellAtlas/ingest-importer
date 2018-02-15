package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonassert.JsonAssert;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectImporterTest {

    @Configuration
    static class TestConfiguration {

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        ProjectImporter projectImporter() {
            return new ProjectImporter();
        }

    }

    @Autowired
    private ProjectImporter projectImporter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testImportFrom() throws Exception {
        //given:
        URI spreadsheetUri = ClassLoader.getSystemResource("spreadsheets/v5.xlsx").toURI();
        File spreadsheet = Paths.get(spreadsheetUri).toFile();
        XSSFWorkbook workbook = new XSSFWorkbook(spreadsheet);

        //when:
        JsonNode projectJson = projectImporter.importFrom(workbook);

        //then:
        assertThat(projectJson).isNotNull();

        //and:
        String json = objectMapper.writeValueAsString(projectJson);
        JsonAssert.with(json)
                .assertEquals("project_shortname", "DEMO-ProjectShortname")
                .assertEquals("project_title", "DEMO-Single cell RNA-seq of primary human " +
                        "glioblastomas");
    }

}
