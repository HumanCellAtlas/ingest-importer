package uk.ac.ebi.hca.importer.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class FileUtil {

    private FileUtil() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    private static String getText(String url) throws IOException {
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
            LOGGER.info("Error processing json at " + url + ": " + e);
            throw e;
        }
        return response.toString();
    }

    public static JsonNode buildJsonNodeFromUrl(String url) {
        try {
            String text = getText(url).trim();
            JsonNode root = new ObjectMapper().readTree(text);
            return root;
        } catch (IOException e) {
            LOGGER.error("Error processing: " + url, e);
            throw new FileUtilException("Error building JSON for " + url, e);
        }
    }


    public static class FileUtilException extends RuntimeException {
        public FileUtilException(String message) {
            super(message);
        }

        public FileUtilException(String message, Exception e) {
            super(message, e);
        }
    }
}
