package uk.ac.ebi.hca.importer.submitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.hca.importer.client.IngestApiClient;
import uk.ac.ebi.hca.importer.util.EntityType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Submitter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Submitter.class);

    private IngestApiClient ingestApiClient;

    public Submitter(IngestApiClient ingestApiClient) {
        this.ingestApiClient = ingestApiClient;
    }

    private Map<EntityType, Map> entityTypeMap = new HashMap<>();

    public String submit(String token, JsonNode jsonNode) {
        String submissionUrl = ingestApiClient.createSubmission(token);
        LOGGER.info("Created submission: " + submissionUrl);
        createEntities(token, jsonNode, submissionUrl);
        linkEntities(jsonNode);
        return submissionUrl;
    }

    private void linkEntities(JsonNode jsonNode) {
        if (jsonNode.getNodeType() == JsonNodeType.ARRAY) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (Iterator<JsonNode> collectionIterator = arrayNode.iterator(); collectionIterator.hasNext(); ) {
                JsonNode collectionNode = collectionIterator.next();
                if (collectionNode.has("links")) {
                    ArrayNode linksNode = (ArrayNode) collectionNode.get("links");
                    EntityType entitytype = EntityType.get(linksNode.get("source_type").textValue());
                    switch (entitytype) {
                        case FILE:

                    }
                }
            }
        }
    }

    private void createEntities(String token, JsonNode jsonNode, String submissionUrl) {
        if (jsonNode.getNodeType() == JsonNodeType.ARRAY) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (Iterator<JsonNode> collectionIterator = arrayNode.iterator(); collectionIterator.hasNext(); ) {
                JsonNode collectionNode = collectionIterator.next();
                if (collectionNode.has("schema_type")) {
                    EntityType entityType = EntityType.get(collectionNode.get("schema_type").textValue());
                    if (collectionNode.has("content")) {
                        ArrayNode contentNode = (ArrayNode) collectionNode.get("content");
                        for (Iterator<JsonNode> contentIterator = contentNode.iterator(); contentIterator.hasNext(); ) {
                            JsonNode itemNode = contentIterator.next();
                            ObjectMapper objectMapper = new ObjectMapper();
                            ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
                            try {
                                String jsonString = writer.writeValueAsString(itemNode);
                                String entityUrl = ingestApiClient.createEntity(token, submissionUrl, entityType, jsonString);
                                storeMapping(entityType, jsonString, entityUrl);
                                LOGGER.info("- Created " + entityType.getSchemaType() + " entity: " + entityUrl);
                            } catch (JsonProcessingException e) {
                                LOGGER.error("Error processing JSON", e);
                            }
                        }
                    }
                }
            }
            LOGGER.info(entityTypeMap.toString());
        }
    }

    private void storeMapping(EntityType entityType, String jsonString, String entityUrl) {
        Map<String, String> entityMap = entityTypeMap.get(entityType);
        if (entityMap == null) {
            entityMap = new HashMap<>();
        }
        String id = getId(entityType, jsonString);
        entityMap.put(id, entityUrl);
        entityTypeMap.put(entityType, entityMap);
    }

    private String getId(EntityType entityType, String jsonString) {
        try {
            JsonNode root = new ObjectMapper().readTree(jsonString);
            String type = entityType.getSchemaType();
            switch (entityType) {
                case PROJECT:
                    return root.get(type + "_core").get(type + "_shortname").textValue();
                case BIOMATERIAL:
                case PROCESS:
                case PROTOCOL:
                    return root.get(type + "_core").get(type + "_id").textValue();
                case FILE:
                    return root.get(type + "_core").get(type + "_name").textValue();
                default:
                    throw new RuntimeException("Unknown Entity Type: " + entityType);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}

/*
self.ingest_api.linkEntity(processIngest, projectIngest, "projects") # correct
self.ingest_api.linkEntity(processIngest, biomaterialMap[biomaterial], "inputBiomaterials")
self.ingest_api.linkEntity(processIngest, biomaterialMap[biomaterial], "derivedBiomaterials")
self.ingest_api.linkEntity(processIngest, biomaterialMap[biomaterial], "inputBiomaterials") # correct
self.ingest_api.linkEntity(processIngest, filesMap[file], "derivedFiles") # correct
 */
