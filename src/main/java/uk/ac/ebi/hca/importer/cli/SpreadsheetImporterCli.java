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
import uk.ac.ebi.hca.importer.SpreadsheetImporter;
import uk.ac.ebi.hca.importer.excel.WorksheetImporter;
import uk.ac.ebi.hca.importer.excel.WorksheetMapping;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.IntStream;

import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING;
import static uk.ac.ebi.hca.importer.excel.CellDataType.STRING_ARRAY;

//@Component
public class SpreadsheetImporterCli implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetImporterCli.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 1) {
            String fileName = args[0];
            File spreadsheetFile = Paths.get(fileName).toFile();
            JsonNode json = new SpreadsheetImporter().importSpreadsheet(spreadsheetFile);
            System.out.println(objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(json));
        } else {
            LOGGER.error("Expected to have exactly one argument.");
        }
    }

}
