package eg.edu.alexu.ehr.ontology.graph;

public enum NodeType {

    SUBCLASS(0), SUPERCLASS(1), EQUIVALENTTO(2),
    DISJOINTWITH(3), INSTANCE(4), INSTANCEOF(5), PROPERTY(6),
    CLASS(7), DATATYPE(8), INDIVIDUAL(9), LITERAL(10);

    private final int value;

    private NodeType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public int noOfValues() {
        return noOfValues;
    }

    private static final int noOfValues = EdgeType.values().length;

}
