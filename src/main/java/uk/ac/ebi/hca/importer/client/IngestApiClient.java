package uk.ac.ebi.hca.importer.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class IngestApiClient {

    public enum EntityType {

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
        mapper.registerModule(new Jackson2HalModule());
        template = new RestTemplate();
    }

    public String createSubmission(String token) {
        HttpEntity<?> httpEntity = new HttpEntity<Object>("{}", getRequestHeaders(token));
        String response = template.postForObject(ingestApiUrl + "/submissionEnvelopes", httpEntity, String.class);
        try {
            JsonNode root = new ObjectMapper().readTree(response);
            return getObjectId(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
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

    public String linkEntity(JsonNode fromEntity, JsonNode toEntity, String relationship) {
        String fromUri = getRelationshipId(fromEntity);
        String toUri = getObjectId(toEntity);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-type", "text/uri-list");
        HttpEntity<?> httpEntity = new HttpEntity<Object>(toUri, requestHeaders);
        String response = template.postForObject(fromUri, httpEntity, String.class);
        try {
            JsonNode root = new ObjectMapper().readTree(response);
            return root.asText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    protected String getRelationshipId(JsonNode jsonNode)
    {
        if (jsonNode.has("_links"))
        {
            return jsonNode.get("_links").get("relationship").get("href").asText();
        }
        throw new IllegalArgumentException("Can't get relationship id for ' + " + jsonNode.toString() + " is it a HCA entity?");
    }

    protected String getObjectId(JsonNode jsonNode)
    {
        if (jsonNode.has("_links"))
        {
            return jsonNode.get("_links").get("self").get("href").asText();
        }
        throw new IllegalArgumentException("Can't get id for ' + " + jsonNode.toString() + " is it a HCA entity?");
    }

    private HttpHeaders getRequestHeaders(String token) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", "Bearer " + token);
        requestHeaders.add("Content-type", "application/json");
        return requestHeaders;
    }
}
