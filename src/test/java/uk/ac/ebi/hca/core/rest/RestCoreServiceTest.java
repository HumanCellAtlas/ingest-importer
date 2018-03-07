package uk.ac.ebi.hca.core.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.hca.core.CoreService;
import uk.ac.ebi.hca.core.SubmissionEnvelope;
import uk.ac.ebi.hca.importer.client.IngestApiClient;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static uk.ac.ebi.hca.importer.client.IngestApiClient.EntityType.PROJECT;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RestCoreServiceTest {

    @Configuration
    static class TestConfiguration {

        @Bean
        RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        CoreService coreService() {
            return new RestCoreService();
        }

    }

    private final String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik1qTkZRa1U0UWtGRlFqUXdRVVEwUlVZNFJqWkZOa1kxUmtVMk9EWXdORE5EUWprd1FrRTFPQSJ9.eyJpc3MiOiJodHRwczovL2RhbmllbHZhdWdoYW4uZXUuYXV0aDAuY29tLyIsInN1YiI6Ilpkc29nNG5EQW5oUTk5eWlLd01RV0FQYzJxVURsUjk5QGNsaWVudHMiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJpYXQiOjE1MTk5ODgyNTMsImV4cCI6MTUyMDA3NDY1MywiYXpwIjoiWmRzb2c0bkRBbmhROTl5aUt3TVFXQVBjMnFVRGxSOTkiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.FJINm7PDG8s3qlzFHmk3Os9vdS1mDdXTsXkDKNZ4z9rRLfd0IICu7b2g81d7YFv6dIrtoCsN3GpE3A4CSysCo3bdtQEZmvyRE1ZQbcqhEGh2sIDqIVd9Q_T_e3eaGBJbs2x0GHnKk-fXtWItjzo5LNddRK1QEGXmggb5eDL60Ms4HCR82YxB84IeshbX5grZvzbLgYbQMjaoPA5aBngcEMrlyLFLccKAGMvu8Gc2GGP9Bpsx5hEZbDGnT4IPag0Sk7Ik8W_jQEcz0AtMfKlFekyYWKwM_22DDsgYm9RQSNVhFY8RKEVFLP-h4wSlncIZ7OWmbkMzSThW9dn172h4Gg";

    private final IngestApiClient ingestApiClient = new IngestApiClient();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CoreService coreService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Ignore
    //TODO define this test
    public void test_create_submission() throws IOException {
        String result = ingestApiClient.createSubmission(token);
        assertTrue(result.startsWith("http://localhost:8080/submissionEnvelopes/"));
    }

    @Ignore
    //TODO define this test
    public void test_create_submission_then_project() {
        String submissionUrl = ingestApiClient.createSubmission(token);
        String response = ingestApiClient.createEntity(token, submissionUrl, PROJECT, "{}");
    }

    @Test
    public void testNothing() {
        //TODO remove this
    }

    @Test
    public void testPrepareSubmission() throws Exception {
        //given:
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        //and: sample Core response with only the relevant details
        String submissionUuid = "126f74fe-9ce0-4ac7-aff8-4359bacb1f33";
        ObjectNode coreResponse = objectMapper.createObjectNode();
        coreResponse.putObject("uuid").put("uuid", submissionUuid);

        //and:
        String responseJson = objectMapper.writeValueAsString(coreResponse);
        server.expect(requestTo("http://foo.bar/submissions"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer cd9bcf"))
                .andRespond(withSuccess(responseJson, APPLICATION_JSON));

        //when:
        SubmissionEnvelope submissionEnvelope = coreService.prepareSubmission("cd9bcf");

        //then:
        server.verify();

        //and:
        assertThat(submissionEnvelope).extracting("uuid").containsExactly(submissionUuid);
    }

}
