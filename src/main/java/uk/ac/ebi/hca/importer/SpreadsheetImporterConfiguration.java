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

    private static final Pattern SCHEMA_URL_PATTERN = Pattern.compile(
            "https://schema.humancellatlas.org/(?<mainType>[\\p{Alpha}_]+/[\\p{Alpha}_]+)/" +
                    "(?<version>\\p{Digit}+[.\\p{Digit}+]*)/(?<schemaType>[\\p{Alpha}_]+)");

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    private MappingUtil mappingUtil = new MappingUtil();

    @Bean("importer.project")
    public WorksheetImporter projectImporter(@Autowired ObjectMapper objectMapper) {
        String schemaPath = "type/project/5.0.0/project";
        String corePath = "core/project/5.0.0/project_core";
        return createWorksheetImporter(objectMapper, schemaPath, corePath);
    }

    @Bean(name="importer.specimen_from_organism")
    public WorksheetImporter specimenFromOrganismImporter(ObjectMapper objectMapper) {
        String schemaPath = "type/biomaterial/5.0.0/specimen_from_organism";
        String corePath = "core/biomaterial/5.0.0/biomaterial_core";
        return createWorksheetImporter(objectMapper, schemaPath, corePath);
    }

    private WorksheetImporter createWorksheetImporter(ObjectMapper objectMapper,
            String schemaPath, String corePath) {
        String baseUrl = "https://schema.humancellatlas.org";
        String schemaUrl = String.format("%s/%s", baseUrl, schemaPath);
        String coreSchemaUrl = String.format("%s/%s", baseUrl, corePath);

        WorksheetMapping worksheetMapping = new WorksheetMapping();
        mappingUtil.populateMappingsFromSchema(worksheetMapping, schemaUrl, "");

        ObjectNode predefinedSchemaValues = objectMapper.createObjectNode();
        mappingUtil.populatePredefinedValuesForSchema(predefinedSchemaValues, schemaUrl);

        WorksheetImporter importer = new WorksheetImporter(objectMapper, worksheetMapping);

        Matcher matcher = SCHEMA_URL_PATTERN.matcher(coreSchemaUrl);
        if (matcher.matches()) {
            ObjectNode predefinedCoreSchemaValues = objectMapper.createObjectNode()
                    .put("describedBy", coreSchemaUrl)
                    .put("schema_version", matcher.group("version"));
            String coreType = matcher.group("schemaType");
            importer.defineValuesFor(coreType, predefinedCoreSchemaValues);
        }

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
