package uk.ac.ebi.hca.core.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrepareSubmissionResponse {

    @JsonProperty
    private Uuid uuid;

    public Uuid getUuid() {
        return uuid;
    }

    static class Uuid {

        @JsonProperty
        private String uuid;

        public String getUuid() {
            return uuid;
        }

    }

}
