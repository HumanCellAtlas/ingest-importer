package uk.ac.ebi.hca.importer.web;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UploadController {

    @RequestMapping("/api_upload")
    @ResponseBody
    UploadSuccessResponse apiUpload(@RequestHeader("Authorization") String token) {
        System.out.println("token: " + token);
        String submissionUUID = "submissionUUID";
        String submissionUrl = "submissionUrl";
        String displayId = "displayId";
        String submissionId = "submissionId";
        String message = "Your spreadsheet was uploaded and processed successfully";
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

        public UploadSuccessResponse() {}

        public UploadSuccessResponse(String submissionUUID, String submissionUrl, String displayId, String submissionId, String message) {
            this.message = message;
            this.details = new UploadDetails( submissionUUID,  submissionUrl,  displayId,  submissionId);
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
