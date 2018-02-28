package uk.ac.ebi.hca.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import uk.ac.ebi.hca.importer.excel.WorkbookImporter;
import uk.ac.ebi.hca.importer.excel.WorksheetImporter;
import uk.ac.ebi.hca.importer.excel.WorksheetMapping;

import java.util.regex.Pattern;

import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING;
import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING_ARRAY;

@Configuration
public class SpreadsheetImporterConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean("importer.project")
    public WorksheetImporter projectImporter(@Autowired ObjectMapper objectMapper) {
        ObjectNode predefinedValues = objectMapper.createObjectNode()
                .put("describedBy", "https://schema.humancellatlas.org/type/project/5.0.0/project")
                .put("schema_version", "5.0.0")
                .put("schema_type", "project");
        return new WorksheetImporter(objectMapper, "projects",
                new WorksheetMapping()
                        .map("Project shortname", "project_core.project_shortname", STRING)
                        .map("Project title", "project_core.project_title", STRING)
                        .map("Project description", "project_core.project_description", STRING)
                        .map("Supplementary files", "supplementary_files", STRING_ARRAY)
                        .map("INSDC project accession", "insdc_project", STRING)
                        .map("GEO series accession", "geo_series", STRING)
                        .map("ArrayExpress accession", "array_express_investigation", STRING)
                        .map("INSDC study accession", "insdc_study", STRING),
                predefinedValues);
    }

    @Bean
    public WorkbookImporter spreadsheetImporter(@Autowired ObjectMapper objectMapper) {
        return new WorkbookImporter(objectMapper);
    }

    @EventListener
    public void importerRegistrar(ContextStartedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        WorkbookImporter workbookImporter = applicationContext.getBean(WorkbookImporter.class);
        Pattern namePattern = Pattern.compile("importer\\.([\\p{Alpha}_]+)");
        applicationContext.getBeansOfType(WorksheetImporter.class)
                .forEach((name, importer) -> {
                    String sheetName = namePattern.matcher(name).group(1);
                    workbookImporter.register(sheetName, importer);
                });
    }

}
