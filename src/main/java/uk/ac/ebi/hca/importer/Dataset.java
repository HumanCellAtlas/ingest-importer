package uk.ac.ebi.hca.importer;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static uk.ac.ebi.hca.importer.Dataset.Category.BIOMATERIAL;

public class Dataset {

    private static final String DEFAULT_SCHEMA_URL_PREFIX = "https://schema.humancellatlas.org";

    private final String schemaUrlPrefix;

    private final Pattern typePattern;

    private final Map<Category, List<JsonNode>> dataset = new HashMap<>();

    public Dataset() {
        this(DEFAULT_SCHEMA_URL_PREFIX);
    }

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
            /*
            We can iterate through the categories every time since there aren't that many anyway;
            there's a predefined number of Categories. It's O(n) but we can guarantee that n is
            small, to the point that it's rather similar to if we had a long list of if-else.

            Thoughts?
            */
            Arrays.stream(Category.values())
                    .filter(category -> schemaPath.startsWith(category.pathPrefix))
                    .findAny()
                    .ifPresent(category -> {
                        dataset.get(category).add(data);
                    });
        }
    }

    public List<JsonNode> get(Category category) {
        return Collections.unmodifiableList(dataset.get(category));
    }

    public enum Category {

        BIOMATERIAL("type/biomaterial"),
        PROCESS("type/process");

        private String pathPrefix;

        private Category(String pathPrefix) {
            this.pathPrefix = pathPrefix;
        }

    }

}
