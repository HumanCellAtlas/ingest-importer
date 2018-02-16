package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING;
import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING_ARRAY;
import static uk.ac.ebi.hca.importer.excel.CellMapping.map;

public class ProjectImporter {

    private static final Map<String, CellMapping> HEADER_MAP;

    static {
        HEADER_MAP = new ImmutableMap.Builder<String, CellMapping>()
                .put("Project shortname", map("project_shortname", STRING))
                .put("Project title", map("project_title", STRING))
                .put("Project description", map("project_description", STRING))
                .put("Supplementary files", map("supplementary_files", STRING_ARRAY))
                .put("INSDC project accession", map("insdc_project", STRING))
                .put("GEO series accession", map("geo_series", STRING))
                .put("ArrayExpress accession", map("array_express_investigation",
                        STRING))
                .put("INSDC study accession", map("insdc_study", STRING))
                .put("Related projects", map("related_projects", STRING_ARRAY))
                .build();
    }

    @Autowired
    private ObjectMapper objectMapper;

    public JsonNode importFrom(Workbook workbook) {
        Sheet projectSheet = workbook.getSheet("project");
        Row headerRow = projectSheet.getRow(2);
        Row dataRow = projectSheet.getRow(3);
        ObjectNode objectNode = objectMapper.createObjectNode();
        dataRow.iterator().forEachRemaining(dataCell -> {
            Cell headerCell = headerRow.getCell(dataCell.getColumnIndex());
            String header = headerCell.getStringCellValue();
            CellMapping cellMapping = HEADER_MAP.get(header);
            if (cellMapping == null) {
                String customField = header.toLowerCase().replaceAll(" ", "_");
                cellMapping = new CellMapping(customField, STRING);
            }
            String data = dataCell.getStringCellValue();
            cellMapping.importTo(objectNode, data);
        });
        return objectNode;
    }

}
