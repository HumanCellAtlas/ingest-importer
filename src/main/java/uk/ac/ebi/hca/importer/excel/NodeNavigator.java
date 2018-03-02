package uk.ac.ebi.hca.importer.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.ac.ebi.hca.importer.excel.exception.NotAnObjectNode;

import java.util.Arrays;
import java.util.stream.StreamSupport;

class NodeNavigator {

    static final String PROPERTY_NESTING_DELIMETER = "\\.";

    JsonNode currentNode;

    String nextProperty;

    NodeNavigator(JsonNode currentNode) {
        this.currentNode = currentNode;
        this.nextProperty = "";
    }

    static NodeNavigator navigate(JsonNode currentNode) {
        return new NodeNavigator(currentNode);
    }

    //TODO add unit tests?
    NodeNavigator prepareObjectNode(String path) {
        String[] propertyChain = path.split(PROPERTY_NESTING_DELIMETER);

        int lastKnownNodeIndex = moveToLastExistentObjectNode(propertyChain);

        ObjectNode pointerNode = null;
        if (currentNode.isObject()) {
            pointerNode = (ObjectNode) currentNode;
        } else {
            throw new NotAnObjectNode(Arrays.copyOfRange(propertyChain, 0,
                    lastKnownNodeIndex));
        }

        int terminalPropertyIndex = propertyChain.length - 1;
        if (lastKnownNodeIndex < terminalPropertyIndex) {
            do {
                String property = propertyChain[lastKnownNodeIndex];
                pointerNode = pointerNode.putObject(property);
                lastKnownNodeIndex++;
            } while (lastKnownNodeIndex < terminalPropertyIndex);
        }

        currentNode = pointerNode;
        nextProperty = propertyChain[terminalPropertyIndex];
        return this;
    }

    private int moveToLastExistentObjectNode(String[] propertyChain) {
        int terminalPropertyIndex = propertyChain.length - 1;
        int index = 0;
        JsonNode navigator = currentNode;
        while (index < terminalPropertyIndex && navigator.has(propertyChain[index])) {
            navigator = navigator.get(propertyChain[index]);
            index++;
        }
        currentNode = navigator;
        return index;
    }

    //TODO add unit tests?
    NodeNavigator moveTo(String path) {
        String[] propertyChain = path.split(PROPERTY_NESTING_DELIMETER);
        for(int index = 0; index < propertyChain.length; index++) {
            currentNode = currentNode.get(propertyChain[index]);
        }
        nextProperty = null;
        return this;
    }

    void putNext(double value) {
        ((ObjectNode) currentNode).put(nextProperty, value);
    }

    void putNext(String value) {
        ((ObjectNode) currentNode).put(nextProperty, value);
    }

    public void putNext(int[] value) {
        ArrayNode array = ((ObjectNode) currentNode).putArray(nextProperty);
        Arrays.stream(value).forEach(array::add);
    }

    void putNext(String[] value) {
        ArrayNode array = ((ObjectNode) currentNode).putArray(nextProperty);
        Arrays.stream(value).forEach(array::add);
    }

    //TODO add unit tests?
    void addValuesFrom(JsonNode json) {
        json.fieldNames().forEachRemaining(fieldName -> {
            ((ObjectNode) currentNode).set(fieldName, json.get(fieldName));
        });
    }

}
