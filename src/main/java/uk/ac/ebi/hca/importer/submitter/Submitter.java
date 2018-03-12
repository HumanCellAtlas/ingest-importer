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
import uk.ac.ebi.hca.importer.util.MappingUtil;

import java.util.Iterator;

public class Submitter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Submitter.class);

    private IngestApiClient ingestApiClient;

    public Submitter(IngestApiClient ingestApiClient) {
        this.ingestApiClient = ingestApiClient;
    }

    public String submit(String token, JsonNode jsonNode) {
        String submissionUrl = ingestApiClient.createSubmission(token);
        LOGGER.info("Created submission: " + submissionUrl);
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
                                LOGGER.info("- Created " + entityType.getSchemaType() + " entity: " + entityUrl);
                            } catch (JsonProcessingException e) {
                                LOGGER.error("Error processing JSON", e);
                            }
                        }
                    }
                }
            }
        }
        return submissionUrl;
    }
}
