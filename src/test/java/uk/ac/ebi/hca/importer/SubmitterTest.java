package uk.ac.ebi.hca.importer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.hca.core.CoreService;

import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SubmitterTest {

    @Configuration
    static class TestConfiguration {

        @Bean
        Submitter submitter() {
            return new Submitter();
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

    }

    @Autowired
    private Submitter submitter;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CoreService coreService;

    @Test
    public void testSubmit() {
        //given:
        ObjectNode json = objectMapper.createObjectNode();
        json.putArray("projects")
                .addPOJO(new Project("Single Cell", "single_cell", "demo project"));

        //when:
        submitter.submit(json);

        //then:
        verify(coreService).prepareSubmission("cd9bcf");
    }

    private static class Project {

        @JsonProperty("project_title")
        private String title;
        @JsonProperty("project_shortname")
        private String shortName;
        @JsonProperty("project_description")
        private String description;

        public Project(String title, String shortName, String description) {
            this.title = title;
            this.shortName = shortName;
            this.description = description;
        }

    }

}
