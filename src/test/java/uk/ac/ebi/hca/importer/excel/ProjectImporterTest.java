package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonassert.JsonAssert;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import uk.ac.ebi.hca.test.IngestTestRunner;
import uk.ac.ebi.hca.test.IntegrationTest;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;

@RunWith(IngestTestRunner.class)
@SpringBootTest
public class ProjectImporterTest {

    @ClassRule
    public static final SpringClassRule CLASS_RULE = new SpringClassRule();

    @Rule
    public SpringMethodRule springMethodRule = new SpringMethodRule();

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

    @IntegrationTest
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
                        "glioblastomas")
                .assertThat("project_description", Matchers.containsString("Single cell cDNA " +
                        "libraries for MGH30 were resequenced using 100 bp paired end reads to " +
                        "allow for isoform and splice junction reconstruction (96 samples, " +
                        "annotated MGH30L)."))
                .assertThat("supplementary_files", contains("supplementary.jpg"))
                .assertEquals("insdc_project", "ACS123")
                .assertEquals("geo_series", "GEO456")
                .assertEquals("array_express_investigation", "AR789")
                .assertEquals("insdc_study", "INSDC001")
                .assertThat("related_projects", contains("UUID321", "UUID654"))
                .assertEquals("extra_field", "this is a custom field");
    }

}
