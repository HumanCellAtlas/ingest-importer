package uk.ac.ebi.hca.importer.util;

import org.json.JSONObject;
import uk.ac.ebi.hca.importer.excel.CellDataType;
import uk.ac.ebi.hca.importer.excel.WorksheetMapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

public class MappingUtil {

    public static void addMappingsFromSchema(WorksheetMapping worksheetMapping, String schemaUrl) throws Exception {
        JSONObject rootObject = new JSONObject(getText(schemaUrl).trim());
        JSONObject propertiesObject = rootObject.getJSONObject("properties");
        for (Object key : iteratorToIterable(propertiesObject.keys())) {
            String keyStr = (String)key;
            JSONObject propertyObject = propertiesObject.getJSONObject(keyStr);
            if (propertyObject.has("user_friendly")) {
                worksheetMapping.map(propertyObject.getString("user_friendly"), keyStr, CellDataType.STRING);
                System.out.println("header: " + propertyObject.getString("user_friendly") + " id: " + keyStr);
            }
        }
    }

    public static String getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        return response.toString();
    }

    private static <T> Iterable<T> iteratorToIterable(Iterator<T> iterator) {
        return () -> iterator;
    }
}
