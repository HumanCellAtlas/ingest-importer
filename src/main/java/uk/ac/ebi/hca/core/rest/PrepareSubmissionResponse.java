package uk.ac.ebi.hca.core.rest;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;

public class PrepareSubmissionResponse {

    private String submissionUrl;

    public String getSubmissionUrl() {
        return submissionUrl;
    }

    @JsonSetter("_links")
    private void setSubmissionUrl(JsonNode node) {
        if (node.has("self")) {
            JsonNode self = node.get("self");
            if (self.has("href")) {
                submissionUrl = self.get("href").asText();
            }
        }
    }

}
