package uk.ac.ebi.hca.importer.excel.exception;

public class NotAnObjectNode extends RuntimeException {

    public NotAnObjectNode(String[] propertyChain) {
        super(String.format("[%s] field is not an object.", String.join(".", propertyChain)));
    }

}
