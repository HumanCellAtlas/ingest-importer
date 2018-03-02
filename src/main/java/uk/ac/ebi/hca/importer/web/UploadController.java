package uk.ac.ebi.hca.importer.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ebi.hca.importer.Dataset;
import uk.ac.ebi.hca.importer.client.IngestApiClient;
import uk.ac.ebi.hca.importer.client.IngestApiClient.EntityType;
import uk.ac.ebi.hca.importer.excel.WorkbookImporter;

import java.io.IOException;
import java.util.List;

@RestController
public class UploadController {

    @Autowired
    private WorkbookImporter workbookImporter;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IngestApiClient apiClient;

    @GetMapping
    public void upload() {}

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    @ResponseBody
    public String testSpreadsheet(@RequestHeader("Authorization") String token, @RequestParam("file") MultipartFile file) {
        String jsonString = "{}";
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            List<JsonNode> records = workbookImporter.importFrom(workbook);
            jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(records);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    @RequestMapping(value = "/api_upload", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    UploadSuccessResponse apiUpload(@RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) throws Exception {
        String submissionUUID = "submissionUUID";
        String displayId = "displayId";
        String submissionId = "submissionId";
        String message = "Your spreadsheet was uploaded and processed successfully";

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        List<JsonNode> records = workbookImporter.importFrom(workbook);
        Dataset dataset = new Dataset();
        records.forEach(dataset::add);

        String submissionUrl = apiClient.createSubmission(token);
        dataset.get(Dataset.Category.BIOMATERIAL).forEach(biomaterial -> {
            apiClient.createEntity(token, submissionUrl, EntityType.BIOMATERIAL,
                    biomaterial.asText());
        });
        dataset.get(Dataset.Category.FILE).forEach(biomaterial -> {
            apiClient.createEntity(token, submissionUrl, EntityType.FILE,
                    biomaterial.asText());
        });
        dataset.get(Dataset.Category.PROCESS).forEach(biomaterial -> {
            apiClient.createEntity(token, submissionUrl, EntityType.PROCESS,
                    biomaterial.asText());
        });
        dataset.get(Dataset.Category.PROJECT).forEach(biomaterial -> {
            apiClient.createEntity(token, submissionUrl, EntityType.PROJECT,
                    biomaterial.asText());
        });
        dataset.get(Dataset.Category.PROTOCOL).forEach(biomaterial -> {
            apiClient.createEntity(token, submissionUrl, EntityType.PROTOCOL,
                    biomaterial.asText());
        });

        return new UploadSuccessResponse(submissionUUID, submissionUrl, displayId, submissionId, message);
    }

    class UploadSuccessResponse {

        private String message;

        public UploadDetails getDetails() {
            return details;
        }

        public void setDetails(UploadDetails details) {
            this.details = details;
        }

        private UploadDetails details;

        public UploadSuccessResponse() {
        }

        public UploadSuccessResponse(String submissionUUID, String submissionUrl, String displayId, String submissionId, String message) {
            this.message = message;
            this.details = new UploadDetails(submissionUUID, submissionUrl, displayId, submissionId);
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    class UploadDetails {

        private String submission_uuid;
        private String submittion_url;
        private String display_id;
        private String submission_id;

        public UploadDetails(String submissionUUID, String submissionUrl, String displayId, String submissionId) {
            this.submission_uuid = submissionUUID;
            this.submittion_url = submissionUrl;
            this.display_id = displayId;
            this.submission_id = submissionId;
        }

        public String getSubmission_uuid() {
            return submission_uuid;
        }

        public void setSubmission_uuid(String submission_uuid) {
            this.submission_uuid = submission_uuid;
        }

        public String getSubmittion_url() {
            return submittion_url;
        }

        public void setSubmittion_url(String submittion_url) {
            this.submittion_url = submittion_url;
        }

        public String getDisplay_id() {
            return display_id;
        }

        public void setDisplay_id(String display_id) {
            this.display_id = display_id;
        }

        public String getSubmission_id() {
            return submission_id;
        }

        public void setSubmission_id(String submission_id) {
            this.submission_id = submission_id;
        }
    }
}
