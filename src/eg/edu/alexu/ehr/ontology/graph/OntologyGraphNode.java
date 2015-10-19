package eg.edu.alexu.ehr.ontology.graph;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import eg.edu.alexu.ehr.ontology.api.wrapper.OntologyProperty;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.OntologyObject;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyClass;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyDatatype;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyEntity;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.values.OntologyValue;

public class OntologyGraphNode {
	private OntologyObject object;
	NodeType nodeType;
	//private Map<EdgeType, List<OntologyGraphEdge>> edges;
	private List<OntologyGraphEdge> []classRelations;
	private Map<String, List<OntologyGraphEdge>> properties;
        private List<OntologyGraphEdge> allEdges;
	private String label;
	
	public OntologyGraphNode(OntologyObject object) {
		this.object = object;
		setNodeType(object);
		label = "";
		EdgeType edgeType = EdgeType.PROPERTY;
		classRelations = (List<OntologyGraphEdge>[])(new List[edgeType.noOfValues()]);
		for (int i = 0; i < edgeType.noOfValues(); i++)
			classRelations[i] = new LinkedList<OntologyGraphEdge>();
		properties = new HashMap<String, List<OntologyGraphEdge>>();
                allEdges = new ArrayList<OntologyGraphEdge>();
	}
	
	/*public OntologyGraphNode(String uri, NodeType nodeType) {
		this.nodeType = nodeType;
		switch (nodeType) {
		case CLASS:
			object = new OntologyClass(uri);
			break;
		case DATATYPE:
			object = new OntologyDatatype(uri);
			break;
		case INDIVIDUAL:
			object = new OntologyIndividual(uri);
			break;
		case LITERAL:
			object = new OntologyLiteral(uri);
			break;
		}
		if (isClass)
			object = new OntologyClass(uri);
		else
			object = new OntologyDatatype(uri);
		setNodeType(object);
		label = "";
		EdgeType edgeType = EdgeType.PROPERTY;
		classRelations = (List<OntologyGraphEdge>[])(new List[edgeType.noOfValues()]);
		properties = new HashMap<String, List<OntologyGraphEdge>>();
	}*/
	
	public OntologyGraphNode(OntologyObject object, String label) {
		this.object = object;
		setNodeType(object);
		this.label = label;
		EdgeType edgeType = EdgeType.PROPERTY;
		classRelations = (List<OntologyGraphEdge>[])(new List[edgeType.noOfValues()]);
		for (int i = 0; i < edgeType.noOfValues(); i++)
			classRelations[i] = new LinkedList<OntologyGraphEdge>();
		properties = new HashMap<String, List<OntologyGraphEdge>>();
                allEdges = new ArrayList<OntologyGraphEdge>();
	}
	
	public OntologyGraphNode(String uri, String label, boolean isClass) {
		if (isClass)
			object = new OntologyClass(uri);
		else
			object = new OntologyDatatype(uri);
		setNodeType(object);
		this.label = label;
		EdgeType edgeType = EdgeType.PROPERTY;
		classRelations = (List<OntologyGraphEdge>[])(new List[edgeType.noOfValues()]);
		properties = new HashMap<String, List<OntologyGraphEdge>>();
                allEdges = new ArrayList<OntologyGraphEdge>();
	}

        public boolean isValue() {
            if (nodeType == NodeType.INDIVIDUAL || nodeType == NodeType.LITERAL)
                return true;
            return false;
        }

        public OntologyGraphEdge lastEdgeAdded() {
            return allEdges.get(allEdges.size()-1);
        }

        public Set<OntologyGraphEdge> getEdges() {
            Set<OntologyGraphEdge> set = new HashSet<OntologyGraphEdge>(allEdges.size());
            for (OntologyGraphEdge edge : allEdges) {
                set.add(edge);
            }

            return set;
        }

        public Set<OntologyGraphEdge> getEdges(EdgeType connection) {
            int index = connection.value();
            List<OntologyGraphEdge> list = classRelations[index];
            Set<OntologyGraphEdge> set = new HashSet<OntologyGraphEdge>(list.size());
            for (OntologyGraphEdge edge : list)
                set.add(edge);
            return set;
        }

        public Set<OntologyGraphEdge> getEdges(String propertyURI) {
            String uri = propertyURI.toString();
            List<OntologyGraphEdge> list = properties.get(uri);
            Set<OntologyGraphEdge> set = new HashSet<OntologyGraphEdge>(list.size());
            for (OntologyGraphEdge edge : list)
                set.add(edge);
            return set;
        }

        public Set<OntologyGraphEdge> getEdges(OntologyProperty property) {
            String uri = property.getURIAsStr();
            return getEdges(uri);
        }

        public Set<OntologyGraphEdge> getEdges(URI propertyURI) {
            String uri = propertyURI.toString();
            return getEdges(uri);
        }

        public Set<OntologyGraphNode> getNextNodes(EdgeType connection) {
            int index = connection.value();
            List<OntologyGraphEdge> list = classRelations[index];
            Set<OntologyGraphNode> set = new HashSet<OntologyGraphNode>(list.size());
            for (OntologyGraphEdge edge : list) {
                set.add(edge.getNextNode());
            }

            return set;
        }
        
        public Set<OntologyGraphNode> getNextNodes(OntologyProperty property) {
            String uri = property.getURIAsStr();
            return getNextNodes(uri);
        }

        public Set<OntologyGraphNode> getNextNodes(URI propertyURI) {
            String uri = propertyURI.toString();
            return getNextNodes(uri);
        }
        
        public Set<OntologyGraphNode> getNextNodes(String propertyURI) {
            List<OntologyGraphEdge> list = properties.get(propertyURI);
            Set<OntologyGraphNode> set = new HashSet<OntologyGraphNode>(list.size());
            for (OntologyGraphEdge edge : list) {
                set.add(edge.getNextNode());
            }

            return set;
        }
	
	public NodeType getNodeType() {
		return nodeType;
	}
	
	private NodeType setNodeType(OntologyObject object) {
		if (object.isEntity()) {
			if (((OntologyEntity)object).isClass())
				nodeType = NodeType.CLASS;
			else
				nodeType = NodeType.DATATYPE;
		}
		else {
			if (((OntologyValue)object).isIndividual())
				nodeType = NodeType.INDIVIDUAL;
			else
				nodeType = NodeType.LITERAL;
		}
		
		return nodeType;
	}
	
	public void addConnection(OntologyProperty property, OntologyGraphNode node) {
		OntologyGraphEdge edge = new OntologyGraphEdge(property, this, node);
		String uri = property.getURIAsStr();
		if (properties.containsKey(uri)) {
			properties.get(uri).add(edge);
		}
		else {
			List<OntologyGraphEdge> list = new LinkedList<OntologyGraphEdge>();
			list.add(edge);
			properties.put(uri, list);
		}
                allEdges.add(edge);
	}
	
	public void addConnection(OntologyProperty property, OntologyEntity entity) {
		OntologyGraphNode node = new OntologyGraphNode(entity);
		OntologyGraphEdge edge = new OntologyGraphEdge(property, this, node);
		String uri = property.getURIAsStr();
		if (properties.containsKey(uri)) {
			properties.get(uri).add(edge);
		}
		else {
			List<OntologyGraphEdge> list = new LinkedList<OntologyGraphEdge>();
			list.add(edge);
			properties.put(uri, list);
		}
                allEdges.add(edge);
	}
	
	public void addConnection(EdgeType edgeType, OntologyEntity entity) {
		if (edgeType == EdgeType.PROPERTY)
			return;
		OntologyGraphNode node = new OntologyGraphNode(entity);
		OntologyGraphEdge edge = new OntologyGraphEdge(edgeType, this, node);
		String uri = edge.getURIAsStr();
		int index = edgeType.value();
		classRelations[index].add(edge);
                allEdges.add(edge);
	}
	
	public void addConnection(EdgeType edgeType, OntologyGraphNode node) {
		if (edgeType == EdgeType.PROPERTY)
			return;
		OntologyGraphEdge edge = new OntologyGraphEdge(edgeType, this, node);
		String uri = edge.getURIAsStr();
		int index = edgeType.value();
		classRelations[index].add(edge);
                allEdges.add(edge);
	}
	
	public void addConnection(OntologyGraphEdge edge) {
		EdgeType edgeType = edge.getEdgeType();
		if (edgeType == EdgeType.PROPERTY)
			return;
		String uri = edge.getURIAsStr();
		int index = edgeType.value();
		classRelations[index].add(edge);
                allEdges.add(edge);
	}
	
	public OntologyObject getObject() {
		return object;
	}
	
	public OntologyClass getAsClass() {
		return (OntologyClass)object;
	}
	
	public OntologyDatatype getAsDatatype() {
		return (OntologyDatatype)object;
	}
	
	public String getLabel() {
		return label;
	}

        public void setLabel(String label) {
            this.label = label;
        }

        public String getSQLDatatype() {
            if (label.equals("double"))
                return "DOUBLE";
            else if (label.equals("date"))
                return "DATE";
            else if (label.equals("dateTime"))
                return "DATETIME";
            else if (label.equals("int"))
                return "INT";
            else
                return "varchar(255)";
        }
	
	@Override
	public int hashCode() {
		if (object.isEntity())
			return ((OntologyEntity)object).getURIAsStr().hashCode();
		else
			return object.toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (object.isEntity())
			return ((OntologyEntity)object).getURIAsStr().equals(((OntologyEntity)((OntologyGraphNode)obj).object).getURIAsStr());
		else
			return object.toString().equals(obj.toString());
	}

        @Override
        public String toString() {
            return object.toString();
        }
}
