package uk.ac.ebi.hca.importer;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.hca.core.CoreService;

public class Submitter {

    @Autowired
    private CoreService coreService;

    public void submit(JsonNode dataset) {
        coreService.prepareSubmission();
    }

}
