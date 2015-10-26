package eg.edu.alexu.ehr.ontology.graph;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import eg.edu.alexu.ehr.ontology.api.wrapper.Ontology;
import eg.edu.alexu.ehr.ontology.api.wrapper.thing.OntologyProperty;
import eg.edu.alexu.ehr.ontology.api.wrapper.thing.object.entities.OntologyClass;
import eg.edu.alexu.ehr.ontology.api.wrapper.thing.object.entities.OntologyObjectType;

public class OntologyGraphEdge {
	private EdgeType edgeType;
	private OntologyProperty property;
	private OntologyGraphNode prevNode;
	private OntologyGraphNode nextNode;
	private String label;
	private float weight;
	private Integer hash;
        private boolean isInverse;

        private OntologyGraphEdge inverseEdge;
        
        private static final float initWeight = 1.0f;
	
	public OntologyGraphEdge(EdgeType edgeType, OntologyGraphNode prevNode, OntologyGraphNode nextNode) {
            initialize(null, edgeType, prevNode, nextNode, "", false);
	}
	
	public OntologyGraphEdge(OntologyProperty property, OntologyGraphNode prevNode, OntologyGraphNode nextNode) {
            initialize(property, EdgeType.PROPERTY, prevNode, nextNode, "", false);
	}
	
	public OntologyGraphEdge(OntologyProperty property, OntologyGraphNode prevNode, OntologyGraphNode nextNode, String label) {
            initialize(property, EdgeType.PROPERTY, prevNode, nextNode, label, false);
	}
        
        public OntologyGraphEdge(EdgeType edgeType, OntologyGraphNode prevNode, OntologyGraphNode nextNode, boolean isInverse) {
            initialize(null, edgeType, prevNode, nextNode, "", isInverse);
	}
	
	public OntologyGraphEdge(OntologyProperty property, OntologyGraphNode prevNode, OntologyGraphNode nextNode, boolean isInverse) {
            initialize(property, EdgeType.PROPERTY, prevNode, nextNode, "", isInverse);
	}
	
	public OntologyGraphEdge(OntologyProperty property, OntologyGraphNode prevNode, OntologyGraphNode nextNode, String label, boolean isInverse) {
            initialize(property, EdgeType.PROPERTY, prevNode, nextNode, label, isInverse);
	}
        
        private void initialize(OntologyProperty property, EdgeType edgeType, OntologyGraphNode prevNode, OntologyGraphNode nextNode, String label, boolean isInverse) {
            this.property = property;
            this.edgeType = edgeType;
            this.prevNode = prevNode;
            this.nextNode = nextNode;
            if (label.equals("") || label == null)
                this.label = getLabelOfObject();
            else
                this.label = label;
            inverseEdge = null;
            weight = initWeight;
            this.isInverse = isInverse;
            hashCode();
        }
        
        public void setIsInverse() {
            isInverse = true;
        }
        
        public void resetIsInverse() {
            isInverse = false;
        }
        
        public boolean isInverse() {
            return isInverse;
        }

        public void inverseOf(OntologyGraphEdge inverseEdge) {
            this.inverseEdge = inverseEdge;
        }

        public boolean isInverse(OntologyGraphEdge edge) {
            if (inverseEdge == null)
                return false;
            return inverseEdge.equals(edge);
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
	
	public Set<OntologyObjectType> getRanges(Ontology ontology) {
		return property.getRanges(ontology);
	}

        public OntologyGraphNode getPreviousNode() {
            return prevNode;
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

        private String getLabelOfObject() {
            if (edgeType == EdgeType.PROPERTY) {
                return getLabelFromUri(property.getURIAsStr());
            } else {
                return edgeTypeAsString();
            }
        }

        private String getLabelFromUri(String uri) {
            int len = uri.length();
            int end = uri.charAt(len-1) == '/' ? len-1 : len;
            for (int i = len-1; i >= 0; i--) {
                char ch = uri.charAt(i);
                if (ch == '#' || ch == '/')
                    return uri.substring(i+1, end);
            }

            return "";
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
