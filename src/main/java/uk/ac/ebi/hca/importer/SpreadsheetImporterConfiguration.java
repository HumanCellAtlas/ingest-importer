package uk.ac.ebi.hca.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import uk.ac.ebi.hca.importer.excel.WorkbookImporter;
import uk.ac.ebi.hca.importer.excel.WorksheetImporter;
import uk.ac.ebi.hca.importer.excel.WorksheetMapping;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.hca.importer.excel.CellDataType.*;

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
        WorksheetImporter importer = new WorksheetImporter(objectMapper, "projects", new
                WorksheetMapping()
                .map("Project shortname", "project_core.project_shortname", STRING)
                .map("Project title", "project_core.project_title", STRING)
                .map("Project description", "project_core.project_description", STRING)
                .map("Supplementary files", "supplementary_files", STRING_ARRAY)
                .map("INSDC project accession", "insdc_project", STRING)
                .map("GEO series accession", "geo_series", STRING)
                .map("ArrayExpress accession", "array_express_investigation", STRING)
                .map("INSDC study accession", "insdc_study", STRING), predefinedValues);
        ObjectNode coreModuleValues = objectMapper
                .createObjectNode()
                .put("describedBy",
                        "https://schema.humancellatlas.org/core/project/5.0.0/project_core")
                .put("schema_version", "5.0.0");
        importer.defineValuesFor("project_core", coreModuleValues);
        return importer;
    }

    @Bean("importer.donor_organism")
    public WorksheetImporter donorOrganismImporter(@Autowired ObjectMapper objectMapper) {
        ObjectNode predefinedValues = objectMapper.createObjectNode()
                .put("describedBy", "https://schema.humancellatlas.org/type/biomaterial/5.0.0/donor_organism")
                .put("schema_version", "5.0.0")
                .put("schema_type", "biomaterial");
        WorksheetImporter importer = new WorksheetImporter(objectMapper, "projects", (new
                WorksheetMapping()), predefinedValues);
        ObjectNode coreModuleValues = objectMapper
                .createObjectNode()
                .put("describedBy",
                        "https://schema.humancellatlas.org/core/biomaterial/5.0.0/biomaterial_core")
                .put("schema_version", "5.0.0");
        importer.defineValuesFor("biomaterial_core", coreModuleValues);
        return importer;
    }

    private WorksheetMapping addMappingFromSchema(WorksheetMapping worksheetMapping) {
        worksheetMapping.map("Biomaterial ID", "biomaterial_core.biomaterial_id", STRING)
                .map("Biomaterial name", "biomaterial_core.biomaterial_name", STRING)
                .map("Biomaterial description", "biomaterial_core.biomaterial_description", STRING)
                .map("NCBI taxon ID", "biomaterial_core.ncbi_taxon_id", NUMERIC_ARRAY)
                .map("Input biomaterial ID", "biomaterial_core.has_input_biomaterial", STRING)
                .map("Genotype", "biomaterial_core.genotype", STRING)
                .map("Karyotype", "biomaterial_core.karyotype", STRING)
                .map("Supplementary files", "biomaterial_core.supplementary_files", STRING_ARRAY)
                .map("BioSample ID", "biomaterial_core.biosd_biomaterial", STRING)
                .map("INSDC ID", "biomaterial_core.insdc_biomaterial", STRING);
        return worksheetMapping;
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
