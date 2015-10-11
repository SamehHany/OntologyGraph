package eg.edu.alexu.ehr.ontology.graph;

public enum EdgeType {
	SUBCLASS(0), SUPERCLASS(1), EQUIVALENTTO(2),
        DISJOINTWITH(3), INSTANCE(4), INSTANCEOF(5), PROPERTY(6);
	
	private final int value;
    private EdgeType(int value) {
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
