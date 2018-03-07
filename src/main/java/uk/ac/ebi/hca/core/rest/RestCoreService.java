package uk.ac.ebi.hca.core.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.hca.core.CoreService;
import uk.ac.ebi.hca.core.SubmissionEnvelope;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class RestCoreService implements CoreService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.core.submissions.url}")
    private String submissionsUrl;

    @Override
    public SubmissionEnvelope prepareSubmission(String authenticationToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", asList(format("Bearer %s", authenticationToken)));
        HttpEntity<String> request = new HttpEntity<>(headers);
        PrepareSubmissionResponse response = restTemplate.postForObject(submissionsUrl, request,
                PrepareSubmissionResponse.class);
        return new SubmissionEnvelope(response.getSubmissionUrl());
    }

}
