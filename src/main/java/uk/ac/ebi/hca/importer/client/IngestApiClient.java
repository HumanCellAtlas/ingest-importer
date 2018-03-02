package uk.ac.ebi.hca.importer.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class IngestApiClient {

    public enum EntityType {

        BIOMATERIAL("biomaterials"),
        PROJECT("projects"),
        PROCESS("processes"),
        PROTOCOL("protocols"),
        FILE("files");

        private final String path;

        public String getPath() {
            return path;
        }

        EntityType(String path) {
            this.path = path;
        }
    }

    @Value("${ingest.api.url}")
    private String ingestApiUrl = "http://localhost:8080";

    private RestTemplate template;

    public IngestApiClient() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        template = new RestTemplate();
    }

    public String createSubmission(String token) {
        HttpEntity<?> httpEntity = new HttpEntity<Object>("{}", getRequestHeaders(token));
        String response = template.postForObject(ingestApiUrl + "/submissionEnvelopes", httpEntity, String.class);
        try {
            JsonNode root = new ObjectMapper().readTree(response);
            return root.get("_links").get("self").get("href").asText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String createEntity(String token, String submissionUrl, EntityType entityType, String json) {
        String postUrl = submissionUrl + "/" + entityType.getPath();
        HttpEntity<?> httpEntity = new HttpEntity<Object>(json, getRequestHeaders(token));
        String response = template.postForObject(postUrl, httpEntity, String.class);
        try {
            JsonNode root = new ObjectMapper().readTree(response);
            return root.asText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private HttpHeaders getRequestHeaders(String token) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", "Bearer " + token);
        requestHeaders.add("Content-type", "application/json");
        return requestHeaders;
    }
}
