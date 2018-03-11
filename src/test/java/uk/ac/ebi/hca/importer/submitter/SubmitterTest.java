package uk.ac.ebi.hca.importer.submitter;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.hca.importer.client.IngestApiClient;
import uk.ac.ebi.hca.importer.util.FileUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SubmitterTest {

    private static final String Q4DemoSS2Metadata_v5_EXPECTED_JSON_URL = "https://raw.githubusercontent.com/HumanCellAtlas/metadata-schema/develop/examples/JSON/v5/SmartSeq2/with_links/Q4DemoSS2Metadata_v5.json";

    private IngestApiClient ingestApiClient = new IngestApiClient();

    private Submitter submitter = new Submitter(ingestApiClient);

    @Test
    public void given_Q4DemoSS2Metadata_v5_json_ingest_successfully() {
        JsonNode jsonNode = FileUtil.buildJsonNodeFromUrl(Q4DemoSS2Metadata_v5_EXPECTED_JSON_URL);
        submitter.submit(ingestApiClient.requestAuthenticationToken(), jsonNode);
    }


}
