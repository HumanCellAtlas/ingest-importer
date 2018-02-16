package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Map;

import static uk.ac.ebi.hca.importer.excel.CellMapping.map;

public class ProjectImporter {

    private static final String ARRAY_SEPARATOR = "\\|\\|";

    private static final Map<String, CellMapping> HEADER_MAP;

    static {
        HEADER_MAP = new ImmutableMap.Builder<String, CellMapping>()
                .put("Project shortname", map("project_shortname", CellDataType.STRING))
                .put("Project title", map("project_title", CellDataType.STRING))
                .put("Project description", map("project_description", CellDataType.STRING))
                .put("Supplementary files", map("supplementary_files", CellDataType.STRING_ARRAY))
                .put("INSDC project accession", map("insdc_project", CellDataType.STRING))
                .put("GEO series accession", map("geo_series", CellDataType.STRING))
                .put("ArrayExpress accession", map("array_express_investigation",
                        CellDataType.STRING))
                .put("INSDC study accession", map("insdc_study", CellDataType.STRING))
                .put("Related projects", map("related_projects", CellDataType.STRING_ARRAY))
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
            CellMapping cellMapping = HEADER_MAP.get(headerCell.getStringCellValue());
            if (cellMapping != null) {
                String data = dataCell.getStringCellValue();
                if (CellDataType.STRING.equals(cellMapping.dataType)) {
                    objectNode.put(cellMapping.jsonProperty, data);
                } else if (CellDataType.STRING_ARRAY.equals(cellMapping.dataType)) {
                    ArrayNode array = objectNode.putArray(cellMapping.jsonProperty);
                    Arrays.stream(data.split(ARRAY_SEPARATOR)).forEach(array::add);
                }
            }
        });
        return objectNode;
    }

}
