package uk.ac.ebi.hca.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class RestCoreService implements CoreService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void prepareSubmission() {
        restTemplate.postForEntity("http://foo.bar/submissions", null, String.class);
    }

}
