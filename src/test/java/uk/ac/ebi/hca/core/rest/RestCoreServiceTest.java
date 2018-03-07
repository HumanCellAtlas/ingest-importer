package uk.ac.ebi.hca.core.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest(properties={
        "service.core.submissions.url=http://core.sample.com/submissions"
})
public class RestCoreServiceTest {

    private static final String URL_SUBMISSIONS = "http://core.sample.com/submissions";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CoreService coreService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testPrepareSubmission() throws Exception {
        //given:
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        //and: sample Core response with only the relevant details
        String submissionUrl = "http://core.sample.com/submissions/5a9ff7296895ad0006b79dac";
        ObjectNode coreResponse = objectMapper.createObjectNode()
                .put("submissionDate", "2018-03-06T16:14:18.112Z");
        coreResponse.putObject("_links")
                .putObject("self")
                .put("href", submissionUrl);

        //and:
        String responseJson = objectMapper.writeValueAsString(coreResponse);
        server.expect(requestTo(URL_SUBMISSIONS))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer cd9bcf"))
                .andRespond(withSuccess(responseJson, APPLICATION_JSON));

        //when:
        SubmissionEnvelope submissionEnvelope = coreService.prepareSubmission("cd9bcf");

        //then:
        server.verify();

        //and:
        assertThat(submissionEnvelope).extracting("submissionUrl").containsExactly(submissionUrl);
    }

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

}
