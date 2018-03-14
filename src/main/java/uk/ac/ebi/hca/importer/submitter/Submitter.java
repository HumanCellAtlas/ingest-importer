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
        return submissionUrl;
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
        if (entityMap==null) {
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
                    return root.get(type + "_core").get(type +  "_shortname").textValue();
                case BIOMATERIAL:
                case PROCESS:
                case PROTOCOL:
                    return root.get(type + "_core").get(type +  "_id").textValue();
                case FILE:
                    return root.get(type + "_core").get(type +  "_name").textValue();
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
               processIngest = self.ingest_api.createProcess(submissionUrl, json.dumps(process))
                self.ingest_api.linkEntity(processIngest, projectIngest, "projects") # correct

                if process["process_core"]["process_id"] in proc_input_biomaterials:
                    for biomaterial in proc_input_biomaterials[process["process_core"]["process_id"]]:
                        self.ingest_api.linkEntity(processIngest, biomaterialMap[biomaterial], "inputBiomaterials")

                if process["process_core"]["process_id"] in proc_output_biomaterials:
                    for biomaterial in proc_output_biomaterials[process["process_core"]["process_id"]]:
                        self.ingest_api.linkEntity(processIngest, biomaterialMap[biomaterial], "derivedBiomaterials")

                # seems to not be used for biomaterials
                for biomaterial in output_biomaterials:
                    self.ingest_api.linkEntity(processIngest, biomaterialMap[biomaterial], "inputBiomaterials") # correct

                for file in output_files:
                    self.ingest_api.linkEntity(processIngest, filesMap[file], "derivedFiles") # correct
 */
