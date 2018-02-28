package uk.ac.ebi.hca.importer.util;

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
import java.util.Iterator;

public class MappingUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MappingUtil.class);

    public void addMappingsFromSchema(WorksheetMapping worksheetMapping, String schemaUrl) {
        try {
            String text = getText(schemaUrl).trim();
            JSONObject rootObject = new JSONObject(text);
            JSONObject propertiesObject = rootObject.getJSONObject("properties");
            for (Object key : iteratorToIterable(propertiesObject.keys())) {
                String keyStr = (String) key;
                try {
                    JSONObject propertyObject = propertiesObject.getJSONObject(keyStr);
                    if (propertyObject.has("user_friendly")) {
                        mapUserFriendlyField(worksheetMapping, keyStr, propertyObject);
                    }
                    if (propertyObject.has("$ref")) {
                        mapFieldsInRefSchema(worksheetMapping, propertyObject);
                    }
                } catch (JSONException jsonException) {
                    LOGGER.info("Error processing attribute : " + keyStr + " in " + schemaUrl + " \n" + jsonException);
                }
            }
        } catch (JSONException jsonException) {
            LOGGER.info("Error processing json: " + jsonException);
        }
    }

    private void mapFieldsInRefSchema(WorksheetMapping worksheetMapping, JSONObject propertyObject) throws JSONException {
        String ref_schema_url = propertyObject.getString("$ref");
        addMappingsFromSchema(worksheetMapping, ref_schema_url);
    }

    private void mapUserFriendlyField(WorksheetMapping worksheetMapping, String keyStr, JSONObject propertyObject) throws JSONException {
        CellDataType type = CellDataType.STRING;
        //TODO: Check for enum
        if (propertyObject.has("type")) {
            String typeStr = propertyObject.getString("type");
            String arrayTypeStr = "";
            if (propertyObject.has("items")) {
                JSONObject itemsObject = propertyObject.getJSONObject("items");
                if (itemsObject.has("type")) {
                    arrayTypeStr = itemsObject.getString("type");
                }
            }
            type = determineDataType(typeStr, arrayTypeStr);
        }
        String header = propertyObject.getString("user_friendly");
        String id = keyStr;
        worksheetMapping.map(header, id, type);
    }

    private CellDataType determineDataType(String type, String arrayType) {
        switch (type) {
            case "string":
                return CellDataType.STRING;
            case "integer":
                return CellDataType.NUMERIC;
            case "array":
                switch (arrayType) {
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
}
