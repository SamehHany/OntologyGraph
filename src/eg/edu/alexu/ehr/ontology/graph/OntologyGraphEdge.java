package eg.edu.alexu.ehr.ontology.graph;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import eg.edu.alexu.ehr.ontology.api.wrapper.Ontology;
import eg.edu.alexu.ehr.ontology.api.wrapper.OntologyProperty;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyClass;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyEntity;

public class OntologyGraphEdge {
	private EdgeType edgeType;
	private OntologyProperty property;
	private OntologyGraphNode prevNode;
	private OntologyGraphNode nextNode;
	private String label;
	private float weight;
	private Integer hash;
	
	public OntologyGraphEdge(EdgeType edgeType, OntologyGraphNode prevNode, OntologyGraphNode nextNode) {
		this.edgeType = edgeType;
		property = null;
		this.prevNode = prevNode;
		this.nextNode = nextNode;
		label = "";
		weight = 0.0f;
		hashCode();
	}
	
	public OntologyGraphEdge(OntologyProperty property, OntologyGraphNode prevNode, OntologyGraphNode nextNode) {
		this.property = property;
		edgeType = EdgeType.PROPERTY;
		this.prevNode = prevNode;
		this.nextNode = nextNode;
		label = "";
		weight = 0.0f;
		hashCode();
	}
	
	public OntologyGraphEdge(OntologyProperty property, OntologyGraphNode prevNode, OntologyGraphNode nextNode, String label) {
		this.property = property;
		edgeType = EdgeType.PROPERTY;
		this.prevNode = prevNode;
		this.nextNode = nextNode;
		this.label = label;
		weight = 0.0f;
		hashCode();
	}
	
	public float getWeight() {
		return weight;
	}
	
	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	public void incrementWeight() {
		weight++;
	}
	
	public void incrementWeight(float x) {
		weight+=x;
	}
	
	public void decrementWeight() {
		weight--;
	}
	
	public void decrementWeight(float x) {
		weight-=x;
	}
	
	public void multiplyWeightBy(float p) {
		weight*=p;
	}
	
	public void divideWeightBy(float d) {
		weight/=d;
	}
	
	public OntologyProperty getProperty(){
		return property;
	}
	
	public Set<OntologyClass> getDomains(Ontology ontology) {
		return property.getDomains(ontology);
	}
	
	public Set<OntologyEntity> getRanges(Ontology ontology) {
		return property.getRanges(ontology);
	}

        public OntologyGraphNode getNextNode() {
            return nextNode;
        }
	
	public EdgeType getEdgeType() {
		return edgeType;
	}
	
	public boolean isSubclass() {
		return edgeType == EdgeType.SUBCLASS;
	}
	
	public boolean isProperty() {
		return edgeType == EdgeType.PROPERTY;
	}
	
	public boolean isEquivalent() {
		return edgeType == EdgeType.EQUIVALENTTO;
	}
	
	public boolean isDisjoint() {
		return edgeType == EdgeType.DISJOINTWITH;
	}
	
	public boolean isInstance() {
		return edgeType == EdgeType.INSTANCE;
	}
	
	public String getLabel() {
		return label;
	}
	
	public URI getURI() {
		if (property == null) {
			try {
				return new URI(edgeTypeAsString());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return property.getURI();
	}
	
	public String getURIAsStr() {
		if (property == null)
			return edgeTypeAsString();
		return property.getURIAsStr();
	}
	
	private String edgeTypeAsString() {
		switch (edgeType) {
		case SUBCLASS:
			return "Subclass";
		case SUPERCLASS:
			return "Superclass";
		case EQUIVALENTTO:
			return "Equivalent";
		case DISJOINTWITH:
			return "Disjoint";
		case INSTANCE:
			return "Instance";
		case PROPERTY:
			return "Property";
		default:
			return "";
		}
	}
	
	@Override
	public int hashCode() {
		if (hash != null) {
			return hash;
		}
		if (edgeType == EdgeType.PROPERTY) {
			hash = prevNode.hashCode() ^ property.hashCode() ^ nextNode.hashCode();
			return hash;
		}
		
		hash = prevNode.hashCode() ^ edgeType.value() ^ nextNode.hashCode();
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		OntologyGraphEdge edge = (OntologyGraphEdge)obj;
		if (hash != (edge.hash))
			return false;
		if (edgeType == edge.edgeType && property.equals(edge.property) && prevNode.equals(edge.prevNode) && nextNode.equals(edge.nextNode))
			return true;
		return false;
	}

        @Override
        public String toString() {
            if (edgeType == EdgeType.PROPERTY)
                return property.toString();
            return edgeTypeAsString();
        }
}
