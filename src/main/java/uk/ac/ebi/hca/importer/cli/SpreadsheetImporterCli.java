package uk.ac.ebi.hca.importer.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import uk.ac.ebi.hca.importer.excel.WorkbookImporter;

import java.io.File;
import java.nio.file.Paths;

//@Component
public class SpreadsheetImporterCli implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetImporterCli.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WorkbookImporter workbookImporter;

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 1) {
            String fileName = args[0];
            File spreadsheetFile = Paths.get(fileName).toFile();
            Workbook workbook = new XSSFWorkbook(spreadsheetFile);
            JsonNode json = workbookImporter.importFrom(workbook);
            System.out.println(objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(json));
        } else {
            LOGGER.error("Expected to have exactly one argument.");
        }
    }

}
