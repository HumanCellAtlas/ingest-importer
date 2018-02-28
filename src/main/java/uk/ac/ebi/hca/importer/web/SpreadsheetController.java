package uk.ac.ebi.hca.importer.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ebi.hca.importer.excel.WorkbookImporter;

import java.io.IOException;

@Controller
@RequestMapping("/upload")
public class SpreadsheetController {

    @Autowired
    private WorkbookImporter workbookImporter;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public void upload() {}

    @PostMapping(produces="application/json")
    @ResponseBody
    public String importSpreadsheet(@RequestParam("spreadsheet") MultipartFile spreadsheet) {
        String jsonString = "{}";
        try {
            Workbook workbook = new XSSFWorkbook(spreadsheet.getInputStream());
            JsonNode json = workbookImporter.importFrom(workbook);
            jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

}

