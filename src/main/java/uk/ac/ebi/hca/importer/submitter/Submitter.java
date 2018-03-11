package uk.ac.ebi.hca.importer.submitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import uk.ac.ebi.hca.importer.client.IngestApiClient;
import uk.ac.ebi.hca.importer.util.EntityType;

import java.util.Iterator;

public class Submitter {

    private IngestApiClient ingestApiClient;

    public Submitter(IngestApiClient ingestApiClient) {
        this.ingestApiClient = ingestApiClient;
    }

    public void submit(String token, JsonNode jsonNode) {
        String submissionUrl = ingestApiClient.createSubmission(token);
        System.out.println("Created submission: " + submissionUrl);
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
                                String entityUrl = ingestApiClient.createEntity(token,submissionUrl, entityType, jsonString);
                                System.out.println("- Created " + entityType.getSchemaType() + " entity: " + entityUrl);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}
