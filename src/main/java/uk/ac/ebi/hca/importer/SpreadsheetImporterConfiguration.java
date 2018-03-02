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
            "https://schema.humancellatlas.org/(?<mainType>[\\p{Alpha}_]+[/[\\p{Alpha}_]+]*)/" +
                    "(?<version>\\p{Digit}+[.\\p{Digit}+]*)/(?<schemaType>[\\p{Alpha}_]+)");

    public static final String CORE_BIOMATERIAL_PATH = "core/biomaterial/5.0.0/biomaterial_core";
    public static final String CORE_FILE_PATH = "core/file/5.0.0/file_core";
    public static final String CORE_PROCESS_PATH = "core/process/5.0.0/process_core";

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

    @Bean(name="importer.cell_line")
    public WorksheetImporter cellLine(@Autowired ObjectMapper objectMapper) {
        String schemaPath = "type/biomaterial/5.0.1/cell_line";
        return createWorksheetImporter(objectMapper, schemaPath, CORE_BIOMATERIAL_PATH);
    }

    @Bean(name="importer.cell_suspension")
    public WorksheetImporter cellSuspension(@Autowired ObjectMapper objectMapper) {
        String schemaPath = "type/biomaterial/5.0.0/cell_suspension";
        return createWorksheetImporter(objectMapper, schemaPath, CORE_BIOMATERIAL_PATH);
    }

    @Bean(name="importer.donor_organism")
    public WorksheetImporter donorOrganism(@Autowired ObjectMapper objectMapper) {
        String schemaPath = "type/biomaterial/5.0.0/donor_organism";
        return createWorksheetImporter(objectMapper, schemaPath, CORE_BIOMATERIAL_PATH);
    }

    @Bean(name="importer.organoid")
    public WorksheetImporter organoid(@Autowired ObjectMapper objectMapper) {
        String schemaPath = "type/biomaterial/5.0.0/organoid";
        return createWorksheetImporter(objectMapper, schemaPath, CORE_BIOMATERIAL_PATH);
    }

    @Bean(name="importer.specimen_from_organism")
    public WorksheetImporter specimenFromOrganismImporter(@Autowired ObjectMapper objectMapper) {
        String schemaPath = "type/biomaterial/5.0.0/specimen_from_organism";
        return createWorksheetImporter(objectMapper, schemaPath, CORE_BIOMATERIAL_PATH);
    }

    @Bean(name="importer.analysis_file")
    public WorksheetImporter analysisFile(@Autowired ObjectMapper objectMapper) {
        String schemaPath = "type/file/5.0.0/analysis_file";
        return createWorksheetImporter(objectMapper, schemaPath, CORE_FILE_PATH);
    }

    @Bean(name="importer.sequence_file")
    public WorksheetImporter sequenceFile(@Autowired ObjectMapper objectMapper) {
        String schemaPath = "type/file/5.0.0/sequence_file";
        return createWorksheetImporter(objectMapper, schemaPath, CORE_FILE_PATH);
    }

    @Bean(name="importer.analysis_process")
    public WorksheetImporter analysisProcess(@Autowired ObjectMapper objectMapper) {
        String schemaPath = "type/process/analysis/5.0.0/analysis_process";
        return createWorksheetImporter(objectMapper, schemaPath, CORE_PROCESS_PATH);
    }

    @Bean("importer.collection_process")
    public WorksheetImporter collectionProcess(@Autowired ObjectMapper objectMapper) {
        String schemaPath = "type/process/biomaterial_collection/5.0.0/collection_process";
        return createWorksheetImporter(objectMapper, schemaPath, CORE_PROCESS_PATH);
    }

    @Bean("importer.dissociation_process")
    public WorksheetImporter dissociationProcess(@Autowired ObjectMapper objectMapper) {
        String schemaPath = "type/process/biomaterial_collection/5.0.0/dissociation_process";
        return createWorksheetImporter(objectMapper, schemaPath, CORE_PROCESS_PATH);
    }

    @Bean("importer.enrichment_process")
    public WorksheetImporter enrichmentProcess(@Autowired ObjectMapper objectMapper) {
        String schemaPath = "type/process/biomaterial_collection/5.0.0/enrichment_process";
        return createWorksheetImporter(objectMapper, schemaPath, CORE_PROCESS_PATH);
    }

    @Bean("importer.imaging_process")
    public WorksheetImporter imagingProcess(@Autowired ObjectMapper objectMapper) {
        String schemaPath = "type/process/imaging/5.0.0/imaging_process";
        return createWorksheetImporter(objectMapper, schemaPath, CORE_PROCESS_PATH);
    }

    @Bean("importer.library_preparation_process")
    public WorksheetImporter libraryPreparationProcess(@Autowired ObjectMapper objectMapper) {
        String schemaPath = "type/process/sequencing/5.0.0/library_preparation_process";
        return createWorksheetImporter(objectMapper, schemaPath, CORE_PROCESS_PATH);
    }

    @Bean("importer.sequencing_process")
    public WorksheetImporter sequencingProcess(@Autowired ObjectMapper objectMapper) {
        String schemaPath = "type/process/sequencing/5.0.0/sequencing_process";
        return createWorksheetImporter(objectMapper, schemaPath, CORE_PROCESS_PATH);
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

        WorksheetImporter importer = new WorksheetImporter(objectMapper, worksheetMapping,
                predefinedSchemaValues);

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
