package uk.ac.ebi.hca.importer.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONException;
import org.json.JSONObject;
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
            JSONObject rootObject = new JSONObject(text);
            JSONObject propertiesObject = rootObject.getJSONObject("properties");
            for (Object key : iteratorToIterable(propertiesObject.keys())) {
                String keyStr = (String) key;
                try {
                    JSONObject propertyObject = propertiesObject.getJSONObject(keyStr);
                    if (propertyObject.has("user_friendly")) {
                        String id = prefix.isEmpty() ? keyStr : prefix + "." + keyStr;
                        mapUserFriendlyField(worksheetMapping, id, propertyObject);
                    }
                    if (propertyObject.has("$ref")) {
                        String ref_schema_url = propertyObject.getString("$ref");
                        populateMappingsFromSchema(worksheetMapping, ref_schema_url, keyStr);
                    }
                } catch (JSONException jsonException) {
                    LOGGER.info("Error processing attribute : " + keyStr + " in " + schemaUrl + " \n" + jsonException);
                }
            }
        } catch (JSONException jsonException) {
            LOGGER.info("Error processing json: " + jsonException);
        }
    }

    private void mapUserFriendlyField(WorksheetMapping worksheetMapping, String id, JSONObject propertyObject) throws JSONException {
        String header = propertyObject.getString("user_friendly");
        worksheetMapping.map(header, id, determineDataType(propertyObject));
    }

    private CellDataType determineDataType(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("enum")) {
            return CellDataType.ENUM;
        }
        String typeStr = "";
        String arrayTypeStr = "";
        if (jsonObject.has("type")) {
            typeStr = jsonObject.getString("type");

            if (jsonObject.has("items")) {
                JSONObject itemsObject = jsonObject.getJSONObject("items");
                if (itemsObject.has("type")) {
                    arrayTypeStr = itemsObject.getString("type");
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