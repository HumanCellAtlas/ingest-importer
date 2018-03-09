package uk.ac.ebi.hca.importer.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.hca.importer.excel.CellDataType;
import uk.ac.ebi.hca.importer.excel.WorksheetMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MappingUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MappingUtil.class);

    public void populateMappingsFromSchema(WorksheetMapping worksheetMapping, String schemaUrl, String prefix) {
        try {
            String text = getText(schemaUrl).trim();
            JsonNode root = new ObjectMapper().readTree(text);
            JsonNode properties = root.get("properties");
            for (String field : iteratorToIterable(properties.fieldNames())) {
                JsonNode property = properties.get(field);
                if (property.has("user_friendly")) {
                    String id = prefix.isEmpty() ? field : prefix + "." + field;
                    mapUserFriendlyField(worksheetMapping, id, property);
                }
                if (property.has("$ref")) {
                    String ref_schema_url = property.get("$ref").textValue();
                    populateMappingsFromSchema(worksheetMapping, ref_schema_url, field);
                }
            }
        } catch (IOException e) {
            LOGGER.info("Error processing json at " + schemaUrl + ": " + e);
            throw new RuntimeException("Invalid schema: " + prefix + ": " + schemaUrl);
        }
    }

    private void mapUserFriendlyField(WorksheetMapping worksheetMapping, String id, JsonNode property) {
        String header = property.get("user_friendly").textValue();
        CellDataType cellDataType = determineDataType(id, property);
        String ref;
        ref = determineRef(id, property, cellDataType);
        worksheetMapping.map(header, id, cellDataType, ref);
    }

    private String determineRef(String id, JsonNode property, CellDataType cellDataType) {
        String ref = "";
        if (cellDataType == CellDataType.OBJECT)
        {
            if (property.has("$ref"))
            {
                ref = property.get("$ref").textValue();
            }
            else {
                throw new RuntimeException("$ref not found for " + id);
            }
        }
        if (cellDataType == CellDataType.OBJECT_ARRAY)
        {
            if (property.has("items"))
            {
                JsonNode items = property.get("items");
                if (items.has("$ref"))
                {
                    ref = items.get("$ref").textValue();
                }
                else {
                    throw new RuntimeException("$ref not found for " + id);
                }
            }
            else {
                throw new RuntimeException("$ref not found for " + id);
            }
        }
        return ref;
    }

    private CellDataType determineDataType(String id, JsonNode property) {
        if (property.has("enum")) {
            return CellDataType.ENUM;
        }
        String typeStr = "";
        String arrayTypeStr = "";
        if (property.has("type")) {
            typeStr = property.get("type").textValue();
            if (property.has("items")) {
                JsonNode items = property.get("items");
                if (items.has("type")) {
                    arrayTypeStr = items.get("type").textValue();
                }
                if (items.has("$ref")) {
                    arrayTypeStr = items.get("$ref").textValue();
                }
            }
            switch (typeStr) {
                case "string":
                    return CellDataType.STRING;
                case "integer":
                    return CellDataType.INTEGER;
                case "number":
                    return CellDataType.NUMBER;
                case "boolean":
                    return CellDataType.BOOLEAN;
                case "object":
                    return CellDataType.OBJECT;
                case "array":
                    switch (arrayTypeStr) {
                        case "string":
                            return CellDataType.STRING_ARRAY;
                        case "integer":
                            return CellDataType.INTEGER_ARRAY;
                        default:
                            if (arrayTypeStr.contains("http"))
                            {
                                return CellDataType.OBJECT_ARRAY;
                            }
                            throw new RuntimeException("Unknown array type in " + id + ": " + arrayTypeStr + " type: " + typeStr);
                    }
                default:
                    throw new RuntimeException("Unknown type in " + id + ": " + typeStr);
            }
        }
        throw new RuntimeException("Cannot determine type of " + id);
    }

    public String getText(String url) {
        StringBuilder response = new StringBuilder();
        try {
            URL website = new URL(url);
            URLConnection connection = website.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
        } catch (IOException e) {
            LOGGER.error("Error processing: " + url, e);
        }
        return response.toString();
    }

    private <T> Iterable<T> iteratorToIterable(Iterator<T> iterator) {
        return () -> iterator;
    }

    public void populatePredefinedValuesForSchema(ObjectNode objectNode, String schemaUrl) {
        List<String> parts = Arrays.asList(schemaUrl.split("/"));
        if (parts.size() > 3) {
            objectNode
                    .put("describedBy", schemaUrl)
                    .put("schema_version", parts.get(parts.size() - 2))
                    .put("schema_type", parts.get(parts.size() - 3));
        }
    }
}