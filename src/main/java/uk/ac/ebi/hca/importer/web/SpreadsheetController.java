package uk.ac.ebi.hca.importer.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ebi.hca.importer.SpreadsheetImporter;

import java.io.IOException;

@Controller
@RequestMapping("/upload")
public class SpreadsheetController {

    private SpreadsheetImporter spreadsheetImporter = new SpreadsheetImporter();

    @GetMapping
    public void upload() {}

    @PostMapping(produces="application/json")
    @ResponseBody
    public String importSpreadsheet(@RequestParam("spreadsheet") MultipartFile spreadsheet) {
        String json = "{}";
        try {
            json = spreadsheetImporter.importSpreadsheetInPrettyPrint(spreadsheet.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

}

