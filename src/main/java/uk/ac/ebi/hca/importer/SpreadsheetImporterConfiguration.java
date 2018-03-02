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

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class SpreadsheetImporterConfiguration {

    private static final String BASE_URL = "https://schema.humancellatlas.org";
    private static final String PATH_PATTERN = "(?<mainType>[\\p{Alpha}_]+[/[\\p{Alpha}_]+]*)/" +
            "(?<version>\\p{Digit}+[.\\p{Digit}+]*)/(?<schemaType>[\\p{Alpha}_]+)";

    private static final Pattern SCHEMA_URL_PATTERN = Pattern.compile(
            String.format("%s/%s", BASE_URL, PATH_PATTERN));

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    private MappingUtil mappingUtil = new MappingUtil();

    enum CoreType {

        BIOMATERIAL("biomaterial", "5.0.0"),
        FILE("file", "5.0.0"),
        PROCESS("process", "5.0.0"),
        PROJECT("project", "5.0.0"),
        PROTOCOL("protocol", "5.0.0");

        private String path;
        private String type;
        private String version;

        CoreType(String type, String version) {
            this.type = type;
            this.version = version;
            this.path = String.format("core/%s/%s/%s_core", type, version, type);
        }

    }

    enum SubmittableType {

        //Use lower case so that enum name() = schema type -> easier to configure
        project("5.0.1", CoreType.PROJECT),
        cell_line("5.0.1", CoreType.BIOMATERIAL),
        cell_suspension("5.0.0", CoreType.BIOMATERIAL),
        donor_organism("5.0.0", CoreType.BIOMATERIAL),
        organoid("5.0.0", CoreType.BIOMATERIAL),
        speciment_from_organism("5.0.0", CoreType.BIOMATERIAL),
        analysis_file("5.0.0", CoreType.FILE),
        sequence_file("5.0.0", CoreType.FILE),
        analysis_process("5.0.0", CoreType.PROCESS, "analysis"),
        collection_process("5.0.0", CoreType.PROCESS, "biomaterial_collection"),
        dissociation_process("5.0.0", CoreType.PROCESS, "biomaterial_collection"),
        enrichment_process("5.0.0", CoreType.PROCESS, "biomaterial_collection"),
        imaging_process("5.0.0", CoreType.PROCESS, "imaging"),
        library_preparation_process("5.0.0", CoreType.PROCESS, "sequencing"),
        sequencing_process("5.0.0", CoreType.PROCESS, "sequencing"),
        protocol("5.0.0", CoreType.PROTOCOL),
        biomaterial_collection_protocol("5.0.0", CoreType.PROTOCOL),
        imaging_protocol("5.0.0", CoreType.PROTOCOL),
        sequencing_protocol("5.0.0", CoreType.PROTOCOL);

        private String[] coreSubTypes;
        private String version;

        private String path;

        private CoreType coreType;

        SubmittableType(String version, CoreType coreType, String... coreSubTypes) {
            this.version = version;
            this.coreType = coreType;
            this.coreSubTypes = coreSubTypes;
            if (coreSubTypes == null || coreSubTypes.length <= 0) {
                this.path = String.format("type/%s/%s/%s", coreType.type, version, name());
            } else {
                String subPath = String.join("/", coreSubTypes);
                this.path = String.format("type/%s/%s/%s/%s", coreType.type, subPath,
                        version, name());
            }
        }

    }

    @Bean
    public WorkbookImporter spreadsheetImporter(@Autowired ObjectMapper objectMapper) {
        return new WorkbookImporter(objectMapper);
    }

    @EventListener
    public void importerRegistrar(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        WorkbookImporter workbookImporter = applicationContext.getBean(WorkbookImporter.class);
        ObjectMapper objectMapper = applicationContext.getBean(ObjectMapper.class);
        Arrays.stream(SubmittableType.values()).forEach(submittableType -> {
            WorksheetImporter importer = createWorksheetImporter(objectMapper,
                    submittableType.path, submittableType.coreType.path);
            workbookImporter.register(submittableType.name(), importer);
        });
    }

    private WorksheetImporter createWorksheetImporter(ObjectMapper objectMapper,
            String schemaPath, String corePath) {
        String schemaUrl = String.format("%s/%s", BASE_URL, schemaPath);
        String coreSchemaUrl = String.format("%s/%s", BASE_URL, corePath);

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

}
