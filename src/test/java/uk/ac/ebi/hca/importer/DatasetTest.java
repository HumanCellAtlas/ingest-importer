package uk.ac.ebi.hca.importer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.ac.ebi.hca.importer.Dataset.Category.BIOMATERIAL;
import static uk.ac.ebi.hca.importer.Dataset.Category.PROCESS;

public class DatasetTest {

    private static final String SCHEMA_REFERENCE = "describedBy";

    private ObjectMapper objectMapper = new ObjectMapper();

    private String schemaUrlPrefix =  "https://schema.humancellatlas.org";

    @Test
    public void testAdd() {
        //given:
        JsonNode cellLineJson = createJson("type/biomaterial/5.0.0/cell_line");
        JsonNode cellSuspension = createJson("type/biomaterial/5.0.0/cell_suspension");
        JsonNode analysisProcess = createJson("type/process/analysis/5.0.0/analysis_process");

        //and:
        Dataset dataset = new Dataset(schemaUrlPrefix);

        //when:
        asList(cellLineJson, cellSuspension, analysisProcess).forEach(dataset::add);

        //then:
        List<JsonNode> biomaterials = dataset.get(BIOMATERIAL);
        assertThat(biomaterials).hasSize(2);
        assertThat(biomaterials.stream().allMatch(json -> {
            return json.get(SCHEMA_REFERENCE).asText().contains("type/biomaterial");
        }));

        //and:
        List<JsonNode> processes = dataset.get(PROCESS);
        assertThat(processes).hasSize(1);
        assertThat(processes.stream().allMatch(json -> {
            return json.get(SCHEMA_REFERENCE).asText().contains("type/process");
        }));
    }

    private JsonNode createJson(String schemaPath) {
        return objectMapper.createObjectNode()
                .put("describedBy", String.format("%s/%s", schemaUrlPrefix, schemaPath));
    }

}
