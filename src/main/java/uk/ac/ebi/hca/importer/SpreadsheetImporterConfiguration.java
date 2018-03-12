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
import java.util.stream.StreamSupport;

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

        //use lower case so that enum name() = schema type -> easier to configure
        biomaterial("5.0.0"),
        file("5.0.0"),
        process("5.0.0"),
        project("5.0.0"),
        protocol("5.0.0");

        private String path;
        private String version;

        CoreType(String version) {
            this.version = version;
            this.path = String.format("core/%s/%s/%s_core", name(), version, name());
        }

    }

    enum SubmittableType {

        //use lower case so that enum name() = schema type -> easier to configure
        project("5.0.1", CoreType.project),
        cell_line("5.0.1", CoreType.biomaterial),
        cell_suspension("5.0.0", CoreType.biomaterial),
        donor_organism("5.0.0", CoreType.biomaterial),
        organoid("5.0.0", CoreType.biomaterial),
        specimen_from_organism("5.0.0", CoreType.biomaterial),
        analysis_file("5.0.0", CoreType.file),
        sequence_file("5.0.0", CoreType.file),
        analysis_process("5.0.0", CoreType.process, "analysis"),
        collection_process("5.0.0", CoreType.process, "biomaterial_collection"),
        dissociation_process("5.0.0", CoreType.process, "biomaterial_collection"),
        enrichment_process("5.0.0", CoreType.process, "biomaterial_collection"),
        imaging_process("5.0.0", CoreType.process, "imaging"),
        library_preparation_process("5.0.0", CoreType.process, "sequencing"),
        sequencing_process("5.0.0", CoreType.process, "sequencing"),
        protocol("5.0.0", CoreType.protocol),
        biomaterial_collection_protocol("5.0.0", CoreType.protocol, "biomaterial"),
        imaging_protocol("5.0.0", CoreType.protocol, "imaging"),
        sequencing_protocol("5.0.0", CoreType.protocol, "sequencing");


        private String version;
        private String path;
        private CoreType coreType;
        private String[] coreSubTypes;

        SubmittableType(String version, CoreType coreType, String... coreSubTypes) {
            this.version = version;
            this.coreType = coreType;
            this.coreSubTypes = coreSubTypes;
            if (coreSubTypes == null || coreSubTypes.length <= 0) {
                this.path = String.format("type/%s/%s/%s", coreType.name(), version, name());
            } else {
                String subPath = String.join("/", coreSubTypes);
                this.path = String.format("type/%s/%s/%s/%s", coreType.name(), subPath,
                        version, name());
            }
        }

    }

    enum ModuleType {

        smartseq2("5.0.0", CoreType.process, "sequencing");

        private final String version;
        private final CoreType coreType;
        private final String[] coreSubTypes;
        private final String path;

        ModuleType(String version, CoreType coreType, String... coreSubTypes) {
            this.version = version;
            this.coreType = coreType;
            this.coreSubTypes = coreSubTypes;
            if (coreSubTypes == null || coreSubTypes.length <= 0) {
                this.path = String.format("type/%s/%s/%s", coreType.name(), version, name());
            } else {
                String subPath = String.join("/", coreSubTypes);
                this.path = String.format("type/%s/%s/%s/%s", coreType.name(), subPath,
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
            String corePath = submittableType.coreType.path;
            WorksheetImporter importer = createWorksheetImporter(objectMapper,
                    submittableType.path, corePath);
            addPredefinedCoreValues(objectMapper, corePath, importer);
            Arrays.stream(ModuleType.values())
                    .filter(moduleType -> submittableType.coreType.equals(moduleType.coreType))
                    .forEach(moduleType -> {
                        ObjectNode predefinedModuleValues = objectMapper.createObjectNode()
                                .put("describedBy", moduleType.path);
                        importer.defineValuesFor(moduleType.name(), predefinedModuleValues);
                    });
            workbookImporter.register(submittableType.name(), importer);
        });
    }

    private WorksheetImporter createWorksheetImporter(ObjectMapper objectMapper,
            String schemaPath, String corePath) {
        String schemaUrl = String.format("%s/%s", BASE_URL, schemaPath);

        ObjectNode predefinedSchemaValues = objectMapper.createObjectNode();
        mappingUtil.populatePredefinedValuesForSchema(predefinedSchemaValues, schemaUrl);

        WorksheetMapping worksheetMapping = new WorksheetMapping();
        mappingUtil.populateMappingsFromSchema(worksheetMapping, schemaUrl, "");

        return new WorksheetImporter(worksheetMapping, predefinedSchemaValues);
    }

    private void addPredefinedCoreValues(ObjectMapper objectMapper, String corePath,
            WorksheetImporter importer) {
        String coreSchemaUrl = String.format("%s/%s", BASE_URL, corePath);
        Matcher matcher = SCHEMA_URL_PATTERN.matcher(coreSchemaUrl);
        if (matcher.matches()) {
            ObjectNode predefinedCoreSchemaValues = objectMapper.createObjectNode()
                    .put("describedBy", coreSchemaUrl)
                    .put("schema_version", matcher.group("version"));
            String coreType = matcher.group("schemaType");
            importer.defineValuesFor(coreType, predefinedCoreSchemaValues);
        }
    }

}
