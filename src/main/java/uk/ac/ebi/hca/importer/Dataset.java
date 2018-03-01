package uk.ac.ebi.hca.importer;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static uk.ac.ebi.hca.importer.Dataset.Category.BIOMATERIAL;

public class Dataset {

    private final String schemaUrlPrefix;

    private final Pattern typePattern;

    private Map<Category, List<JsonNode>> dataset = new HashMap<>();

    public Dataset(String schemaUrlPrefix) {
        this.schemaUrlPrefix = schemaUrlPrefix;
        String urlRegex = format("%s/([\\p{ASCII}*/]*)", schemaUrlPrefix);
        typePattern = Pattern.compile(urlRegex);
        Arrays.stream(Category.values()).forEach(category -> {
            dataset.put(category, new ArrayList<>());
        });
    }

    public void add(JsonNode data) {
        String schema = data.get("describedBy").asText();
        Matcher matcher = typePattern.matcher(schema);
        if (matcher.matches()) {
            String schemaPath = matcher.group(1);
            if (schemaPath.startsWith(BIOMATERIAL.pathPrefix)) {
                dataset.get(BIOMATERIAL).add(data);
            }
        }
    }

    public List<JsonNode> get(Category category) {
        return Collections.unmodifiableList(dataset.get(category));
    }

    public enum Category {

        BIOMATERIAL("type/biomaterial");

        private String pathPrefix;

        private Category(String pathPrefix) {
            this.pathPrefix = pathPrefix;
        }

    }

}
