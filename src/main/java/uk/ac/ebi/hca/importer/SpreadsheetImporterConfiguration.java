package uk.ac.ebi.hca.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import uk.ac.ebi.hca.importer.excel.WorkbookImporter;
import uk.ac.ebi.hca.importer.excel.WorksheetImporter;
import uk.ac.ebi.hca.importer.excel.WorksheetMapping;
import uk.ac.ebi.hca.importer.util.MappingUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class SpreadsheetImporterConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public MappingUtil mappingUtil() {
        return new MappingUtil();
    }

    @Bean("importer.project")
    public WorksheetImporter projectImporter(@Autowired ObjectMapper objectMapper) {
        String schemaUrl = "https://schema.humancellatlas.org/type/project/5.0.0/project";

        ObjectNode predefinedValues =  objectMapper.createObjectNode();
        mappingUtil().populatePredefinedValuesForSchema(predefinedValues, schemaUrl);

        WorksheetMapping worksheetMapping = new WorksheetMapping();
        mappingUtil().populateMappingsFromSchema(worksheetMapping, schemaUrl, "");

        WorksheetImporter importer = new WorksheetImporter(objectMapper, "projects", worksheetMapping, predefinedValues);
        ObjectNode coreModuleValues = objectMapper
                .createObjectNode()
                .put("describedBy",
                        "https://schema.humancellatlas.org/core/project/5.0.0/project_core")
                .put("schema_version", "5.0.0");
        importer.defineValuesFor("project_core", coreModuleValues);
        return importer;
    }

    @Bean
    public WorkbookImporter spreadsheetImporter(@Autowired ObjectMapper objectMapper) {
        return new WorkbookImporter(objectMapper);
    }

    @EventListener
    public void importerRegistrar(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        WorkbookImporter workbookImporter = applicationContext.getBean(WorkbookImporter.class);
        Pattern namePattern = Pattern.compile("importer\\.([\\p{Alpha}_]+)");
        applicationContext.getBeansOfType(WorksheetImporter.class)
                .forEach((name, importer) -> {
                    Matcher matcher = namePattern.matcher(name);
                    if (matcher.matches()) {
                        String sheetName = matcher.group(1);
                        workbookImporter.register(sheetName, importer);
                    }
                });
    }

}
