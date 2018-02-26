package uk.ac.ebi.hca.importer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uk.ac.ebi.hca.importer.excel.WorksheetImporter;
import uk.ac.ebi.hca.importer.excel.WorksheetMapping;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.IntStream;

import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING;
import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING_ARRAY;

public class SpreadsheetImporter {

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, WorksheetImporter> registry;

    public SpreadsheetImporter() {
        //Cell to JSON field mapping
        WorksheetImporter projectImporter = new WorksheetImporter(objectMapper, "projects",
                new WorksheetMapping()
                        .map("Project shortname", "project_shortname", STRING)
                        .map("Related projects", "related_projects", STRING_ARRAY)
                        .map("INSDC project accession", "extra.indsc", STRING)
                        .map("GEO series accession", "extra.geo_series", STRING));

        //Worksheet to importer mapping
        registry = new ImmutableMap.Builder<String, WorksheetImporter>()
                .put("project", projectImporter)
                .build();
    }

    public JsonNode importSpreadsheet(File spreadsheetFile) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(spreadsheetFile);
            return doImportSpreadsheet(workbook);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode importSpreadsheet(InputStream inputStream) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            return doImportSpreadsheet(workbook);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode doImportSpreadsheet(XSSFWorkbook workbook) {
        ObjectNode json = objectMapper.createObjectNode();
        IntStream
                .range(0, workbook.getNumberOfSheets())
                .filter(index -> {
                    return registry.containsKey(workbook.getSheetAt(index)
                            .getSheetName());
                })
                .forEach(index -> {
                    XSSFSheet worksheet = workbook.getSheetAt(index);
                    WorksheetImporter importer = registry.get(worksheet
                            .getSheetName());
                    String fieldName = importer.getFieldName();
                    JsonNode importedNode = importer.importFrom(worksheet);
                    json.set(fieldName, importedNode.get(fieldName));
                });
        return json;
    }

    public String importSpreadsheetInPrettyPrint(InputStream inputStream) {
        try {
            JsonNode json = importSpreadsheet(inputStream);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
