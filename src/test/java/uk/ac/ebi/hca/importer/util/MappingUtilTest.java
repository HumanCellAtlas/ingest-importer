package uk.ac.ebi.hca.importer.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.hca.importer.excel.WorksheetMappingSpy;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MappingUtilTest {

    @Autowired MappingUtil mappingUtil;

    @Test
    public void test_with_valid_donor_organism_core_schema() throws Exception {
        WorksheetMappingSpy worksheetMapping = new WorksheetMappingSpy();
        mappingUtil.addMappingsFromSchema(worksheetMapping, "https://schema.humancellatlas.org/type/biomaterial/5.0.0/donor_organism");
        assertEquals(38, worksheetMapping.getNumberOfMappings());
    }

    @Test
    public void test_with_valid_smartseq2_schema() throws Exception {
        WorksheetMappingSpy worksheetMapping = new WorksheetMappingSpy();
        mappingUtil.addMappingsFromSchema(worksheetMapping, "https://schema.humancellatlas.org/module/process/sequencing/5.0.0/smartseq2");
        assertEquals(5, worksheetMapping.getNumberOfMappings());
    }

    @Test
    public void test_with_invalid_schema() throws Exception {
        WorksheetMappingSpy worksheetMapping = new WorksheetMappingSpy();
        mappingUtil.addMappingsFromSchema(worksheetMapping, "https://schema.humancellatlas.org/core/biomaterial/5.0.0/invalid");
        assertEquals(0, worksheetMapping.getNumberOfMappings());
    }

    @Test
    public void test_with_valid_project_schema() throws Exception {
        WorksheetMappingSpy worksheetMapping = new WorksheetMappingSpy();
        mappingUtil.addMappingsFromSchema(worksheetMapping, "https://schema.humancellatlas.org/type/project/5.0.0/project");
        System.out.println(worksheetMapping.toString());
        assertEquals(8, worksheetMapping.getNumberOfMappings());
    }

}
