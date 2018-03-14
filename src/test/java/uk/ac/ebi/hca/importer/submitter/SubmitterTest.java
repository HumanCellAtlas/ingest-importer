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

    private static final String Spleen_v5_EXPECTED_JSON_URL = "https://raw.githubusercontent.com/HumanCellAtlas/metadata-schema/develop/examples/JSON/v5/10x_v2/metadata_spleen_v5_20180313_userFriendlyHeaders.json";

    private IngestApiClient ingestApiClient = new IngestApiClient();

    private Submitter submitter = new Submitter(ingestApiClient);

    @Test
    public void given_Spleen_v5_json_ingest_successfully() {
        JsonNode jsonNode = FileUtil.buildJsonNodeFromUrl(Spleen_v5_EXPECTED_JSON_URL);
        submitter.submit(ingestApiClient.requestAuthenticationToken(), jsonNode);
    }


}
