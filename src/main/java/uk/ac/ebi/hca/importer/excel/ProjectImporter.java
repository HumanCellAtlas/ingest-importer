package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING;
import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING_ARRAY;

public class ProjectImporter {

    private static final WorksheetMapping WORKSHEET_MAPPING = new WorksheetMapping()
            .map("Project shortname", "project_shortname", STRING)
            .map("Project title", "project_title", STRING)
            .map("Project description", "project_description", STRING)
            .map("Supplementary files", "supplementary_files", STRING_ARRAY)
            .map("INSDC project accession", "insdc_project", STRING)
            .map("GEO series accession", "geo_series", STRING)
            .map("ArrayExpress accession", "array_express_investigation", STRING)
            .map("INSDC study accession", "insdc_study", STRING)
            .map("Related projects", "related_projects", STRING_ARRAY);

    @Autowired
    private ObjectMapper objectMapper;

    private WorksheetImporter worksheetImporter;

    @PostConstruct
    public void setUp() {
        worksheetImporter = new WorksheetImporter(objectMapper, WORKSHEET_MAPPING);
    }

    public JsonNode importFrom(Workbook workbook) {
        Sheet projectSheet = workbook.getSheet("project");
        return worksheetImporter.importFrom(projectSheet);
    }

}
