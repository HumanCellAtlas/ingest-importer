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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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
            LOGGER.info("Error processing json: " + e);
        }
    }

    private void mapUserFriendlyField(WorksheetMapping worksheetMapping, String id, JsonNode property) {
        String header = property.get("user_friendly").textValue();
        worksheetMapping.map(header, id, determineDataType(property));
    }

    private CellDataType determineDataType(JsonNode property) {
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
            }
        }
        switch (typeStr) {
            case "string":
                return CellDataType.STRING;
            case "integer":
                return CellDataType.NUMERIC;
            case "array":
                switch (arrayTypeStr) {
                    case "string":
                        return CellDataType.STRING_ARRAY;
                    case "integer":
                        return CellDataType.NUMERIC_ARRAY;
                    default:
                        return CellDataType.STRING_ARRAY;
                }
            default:
                return CellDataType.STRING;
        }
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    private <T> Iterable<T> iteratorToIterable(Iterator<T> iterator) {
        return () -> iterator;
    }

    public void populatePredefinedValuesForSchema(ObjectNode objectNode, String schemaUrl) {
        List<String> parts = Arrays.asList(schemaUrl.split("/"));
        if (parts.size() > 2) {
            objectNode
                    .put("describedBy", schemaUrl)
                    .put("schema_version", parts.get(parts.size() - 2))
                    .put("schema_type", parts.get(parts.size() - 1));
        }
    }
}