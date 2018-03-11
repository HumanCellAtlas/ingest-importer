package uk.ac.ebi.hca.importer.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.hca.importer.util.EntityType;

import java.io.IOException;

@Component
public class IngestApiClient {

    @Value("${ingest.api.url}")
    private String ingestApiUrl = "http://localhost:8080";

    private RestTemplate template;

    public IngestApiClient() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        template = new RestTemplate();
    }

    public String requestAuthenticationToken() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-type", "application/json");
        HttpEntity<?> httpEntity = new HttpEntity<Object>("{\"client_id\":\"Zdsog4nDAnhQ99yiKwMQWAPc2qUDlR99\",\"client_secret\":\"t-OAE-GQk_nZZtWn-QQezJxDsLXmU7VSzlAh9cKW5vb87i90qlXGTvVNAjfT9weF\",\"audience\":\"http://localhost:8080\",\"grant_type\":\"client_credentials\"}", requestHeaders);
        String response = template.postForObject("https://danielvaughan.eu.auth0.com/oauth/token", httpEntity, String.class);
        try {
            JsonNode root = new ObjectMapper().readTree(response);
            if (root.has("access_token"))
            {
                return root.get("access_token").textValue();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public String createSubmission(String token) {
        HttpEntity<?> httpEntity = new HttpEntity<Object>("{}", getRequestHeaders(token));
        String response = template.postForObject(ingestApiUrl + "/submissionEnvelopes", httpEntity, String.class);
        try {
            return getSelfLink(new ObjectMapper().readTree(response));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String createEntity(String token, String submissionUrl, EntityType entityType, String json) {
        String postUrl = submissionUrl + "/" + entityType.getSubmissionPath();
        HttpEntity<?> httpEntity = new HttpEntity<Object>(json, getRequestHeaders(token));
        String response = template.postForObject(postUrl, httpEntity, String.class);
        try {
            return getSelfLink(new ObjectMapper().readTree(response));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getSelfLink(JsonNode rootNode)
    {
        if (rootNode.has("_links"))
        {
            JsonNode linksNode = rootNode.get("_links");
            if (linksNode.has("self"))
            {
                JsonNode selfNode = linksNode.get("self");
                if (selfNode.has("href")) {
                    return selfNode.get("href").textValue();
                }
            }
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
