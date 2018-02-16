package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CellMappingTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testStringTypeImportTo() {
        //given:
        ObjectNode node = objectMapper.createObjectNode();

        //and:
        String firstName = "first_name";
        CellMapping firstNameMapping = new CellMapping(firstName, CellDataType.STRING);
        String lastName = "last_name";
        CellMapping lastNameMapping = new CellMapping(lastName, CellDataType.STRING);

        //when:
        String juan = "Juan";
        firstNameMapping.importTo(node, juan);

        //and:
        String delaCruz = "dela Cruz";
        lastNameMapping.importTo(node, delaCruz);

        //then:
        assertThat(node.has(firstName)).as("first_name field expected").isTrue();
        assertThat(node.get(firstName).asText()).isEqualTo(juan);

        //and:
        assertThat(node.has(lastName)).as("last_name field expected").isTrue();
        assertThat(node.get(lastName).asText()).isEqualTo(delaCruz);
    }

}
