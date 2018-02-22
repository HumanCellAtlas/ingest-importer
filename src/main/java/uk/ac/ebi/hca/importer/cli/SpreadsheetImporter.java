package uk.ac.ebi.hca.importer.cli;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.hca.importer.excel.WorksheetImporter;
import uk.ac.ebi.hca.importer.excel.WorksheetMapping;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.IntStream;

import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING;
import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING_ARRAY;

@Component
public class SpreadsheetImporter implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetImporter.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, WorksheetImporter> registry;

    public SpreadsheetImporter() {
        //Cell to JSON field mapping
        WorksheetImporter projectImporter = new WorksheetImporter(objectMapper,
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

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 1) {
            String fileName = args[0];
            File spreadsheetFile = Paths.get(fileName).toFile();
            XSSFWorkbook workbook = new XSSFWorkbook(spreadsheetFile);

            IntStream.range(0, workbook.getNumberOfSheets())
                    .filter(index -> {
                        return registry.containsKey(workbook.getSheetAt(index)
                                .getSheetName());
                    })
                    .forEach(index -> {
                        XSSFSheet worksheet = workbook.getSheetAt(index);
                        WorksheetImporter importer = registry.get(worksheet
                                .getSheetName());
                        JsonNode json = importer.importFrom(worksheet);
                        try {
                            System.out.println(objectMapper
                                    .writerWithDefaultPrettyPrinter()
                                    .writeValueAsString(json));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    });
        } else {
            LOGGER.error("Expected to have exactly one argument.");
        }
    }

}
