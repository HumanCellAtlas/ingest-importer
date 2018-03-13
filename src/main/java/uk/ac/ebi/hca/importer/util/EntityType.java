package uk.ac.ebi.hca.importer.util;

import java.util.HashMap;
import java.util.Map;

public enum EntityType {

    PROJECT("project", "projects"),
    BIOMATERIAL("biomaterial", "biomaterials"),
    PROCESS("process", "processes"),
    PROTOCOL("protocol", "protocols"),
    FILE("file", "files");

    private static final Map<String, EntityType> lookup = new HashMap<>();

    static {
        for (EntityType e : EntityType.values()) {
            lookup.put(e.getSchemaType(), e);
        }
    }

    private final String submissionPath;

    private final String schemaType;

    public String getSubmissionPath() {
        return submissionPath;
    }

    public String getSchemaType() {
        return schemaType;
    }

    EntityType(String schemaType, String submissionPath) {
        this.submissionPath = submissionPath;
        this.schemaType = schemaType;
    }

    public static EntityType get(String schemaType) {
        return lookup.get(schemaType);
    }
}
