package uk.ac.ebi.hca.core.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.hca.core.CoreService;
import uk.ac.ebi.hca.core.SubmissionEnvelope;

public class RestCoreService implements CoreService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public SubmissionEnvelope prepareSubmission() {
        PrepareSubmissionResponse response = restTemplate
                .postForObject("http://foo.bar/submissions", null, PrepareSubmissionResponse.class);
        return new SubmissionEnvelope(response.getUuid().getUuid());
    }

}
