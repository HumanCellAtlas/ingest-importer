package uk.ac.ebi.hca.importer.util;

import org.junit.Test;
import uk.ac.ebi.hca.importer.excel.WorksheetMapping;

public class MappingUtilTest {

    @Test
    public void test() throws Exception {
        WorksheetMapping worksheetMapping = new WorksheetMapping();
        MappingUtil.addMappingsFromSchema(worksheetMapping, "https://schema.humancellatlas.org/core/biomaterial/5.0.0/biomaterial_core");
    }
}
