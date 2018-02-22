package uk.ac.ebi.hca.importer.excel;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ac.ebi.hca.importer.excel.CellDataType.*;

public class WorksheetMappingTest {

    @Test
    public void testGetMappingFor() {
        //given:
        WorksheetMapping worksheetMapping = new WorksheetMapping()
                .map("First Name", "first_name", STRING)
                .map("Friends", "friends", STRING_ARRAY)
                .map("Age", "age", NUMERIC);

        //when:
        CellMapping firstName = worksheetMapping.getMappingFor("First Name");
        CellMapping friends = worksheetMapping.getMappingFor("Friends");
        CellMapping age = worksheetMapping.getMappingFor("Age");

        //and:
        CellMapping extraField = worksheetMapping.getMappingFor("Extra Field");

        //then:
        String[] cellMappingFields = {"jsonProperty", "dataType"};
        assertThat(firstName).extracting(cellMappingFields)
                .containsExactly("first_name", STRING);
        assertThat(friends).extracting(cellMappingFields)
                .containsExactly("friends", STRING_ARRAY);
        assertThat(age).extracting(cellMappingFields)
                .containsExactly("age", NUMERIC);

        //and:
        assertThat(extraField).extracting(cellMappingFields)
                .containsExactly("extra_field", STRING);
    }

}