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
import eg.edu.alexu.ehr.ontology.api.wrapper.object.values.OntologyIndividual;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.values.OntologyLiteral;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.values.OntologyValue;

public class OntologyGraphNode implements OntologyGraphObject {

    private OntologyObject object;
    NodeType nodeType;
    //private Map<EdgeType, List<OntologyGraphEdge>> edges;
    private List<OntologyGraphEdge>[] edgesByType;
    private Map<String, List<OntologyGraphEdge>> properties;
    private Set<OntologyGraphEdge> allEdges;
    private OntologyGraphEdge lastEdgeAdded;
    private String label;

    public OntologyGraphNode(OntologyObject object) {
        this.object = object;
        setNodeType(object);
        label = getLabelFromUri(object.getURIAsStr());
        EdgeType edgeType = EdgeType.PROPERTY;
        edgesByType = (List<OntologyGraphEdge>[]) (new List[edgeType.noOfValues()]);
        for (int i = 0; i < edgeType.noOfValues(); i++) {
            edgesByType[i] = new LinkedList<OntologyGraphEdge>();
        }
        properties = new HashMap<String, List<OntologyGraphEdge>>();
        allEdges = new HashSet<OntologyGraphEdge>();
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
        edgesByType = (List<OntologyGraphEdge>[]) (new List[edgeType.noOfValues()]);
        for (int i = 0; i < edgeType.noOfValues(); i++) {
            edgesByType[i] = new LinkedList<OntologyGraphEdge>();
        }
        properties = new HashMap<String, List<OntologyGraphEdge>>();
        allEdges = new HashSet<OntologyGraphEdge>();
    }

    public OntologyGraphNode(String uri, String label, boolean isClass) {
        if (isClass) {
            object = new OntologyClass(uri);
        } else {
            object = new OntologyDatatype(uri);
        }
        setNodeType(object);
        this.label = label;
        EdgeType edgeType = EdgeType.PROPERTY;
        edgesByType = (List<OntologyGraphEdge>[]) (new List[edgeType.noOfValues()]);
        properties = new HashMap<String, List<OntologyGraphEdge>>();
        allEdges = new HashSet<OntologyGraphEdge>();
    }
    
    private String getLabelFromUri(String uri) {
            int len = uri.length();
            int end = uri.charAt(len-1) == '/' ? len-1 : len;
            for (int i = len-1; i >= 0; i--) {
                char ch = uri.charAt(i);
                if (ch == '#' || ch == '/')
                    return uri.substring(i+1, end);
            }

            return uri;
        }

    public boolean isValue() {
        if (nodeType == NodeType.INDIVIDUAL || nodeType == NodeType.LITERAL) {
            return true;
        }
        return false;
    }

    public boolean isDataType() {
        return nodeType == NodeType.DATATYPE;
    }

    public boolean isClass() {
        return nodeType == NodeType.CLASS;
    }

    public OntologyGraphEdge lastEdgeAdded() {
        return lastEdgeAdded;
    }

    public Set<OntologyGraphEdge> getEdges() {
        Set<OntologyGraphEdge> set = new HashSet<OntologyGraphEdge>(allEdges.size());
        for (OntologyGraphEdge edge : allEdges) {
            set.add(edge);
        }

        return set;
    }

    public boolean hasValue() {
        if (isClass()) {
            return ((OntologyClass) object).hasValue();
        }

        return false;
    }

    public Set<OntologyGraphEdge> getEdges(EdgeType connection) {
        int index = connection.value();
        List<OntologyGraphEdge> list = edgesByType[index];
        Set<OntologyGraphEdge> set = new HashSet<OntologyGraphEdge>(list.size());
        for (OntologyGraphEdge edge : list) {
            set.add(edge);
        }
        return set;
    }

    public Set<OntologyGraphEdge> getEdges(String uri) {
        List<OntologyGraphEdge> list = properties.get(uri);
        if (list == null) {
            return new HashSet<OntologyGraphEdge>();
        }
        Set<OntologyGraphEdge> set = new HashSet<OntologyGraphEdge>(list.size());
        for (OntologyGraphEdge edge : list) {
            set.add(edge);
        }
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
        List<OntologyGraphEdge> list = edgesByType[index];
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
            if (((OntologyEntity) object).isClass()) {
                nodeType = NodeType.CLASS;
            } else {
                nodeType = NodeType.DATATYPE;
            }
        } else {
            if (((OntologyValue) object).isIndividual()) {
                nodeType = NodeType.INDIVIDUAL;
            } else {
                nodeType = NodeType.LITERAL;
            }
        }

        return nodeType;
    }

    public void addConnection(OntologyProperty property,
            OntologyGraphNode node) {
        Set<OntologyProperty> inverses = property.getInverses();
        OntologyGraphEdge edge = new OntologyGraphEdge(property, this,
                node);
        String uri = property.getURIAsStr();
        if (properties.containsKey(uri)) {
            properties.get(uri).add(edge);
        } else {
            List<OntologyGraphEdge> list
                    = new LinkedList<OntologyGraphEdge>();
            list.add(edge);
            properties.put(uri, list);
        }
        int index = edge.getEdgeType().value();
        edgesByType[index].add(edge);
        allEdges.add(edge);
        lastEdgeAdded = edge;
    }

    public void addConnection(OntologyProperty property,
            OntologyEntity entity) {
        OntologyGraphNode node = new OntologyGraphNode(entity);
        OntologyGraphEdge edge = new OntologyGraphEdge(property, this,
                node);
        String uri = property.getURIAsStr();
        if (properties.containsKey(uri)) {
            properties.get(uri).add(edge);
        } else {
            List<OntologyGraphEdge> list
                    = new LinkedList<OntologyGraphEdge>();
            list.add(edge);
            properties.put(uri, list);
        }
        int index = edge.getEdgeType().value();
        edgesByType[index].add(edge);
        allEdges.add(edge);
        lastEdgeAdded = edge;
    }

    public void addConnection(EdgeType edgeType, OntologyEntity entity) {
        if (edgeType == EdgeType.PROPERTY) {
            return;
        }
        OntologyGraphNode node = new OntologyGraphNode(entity);
        OntologyGraphEdge edge = new OntologyGraphEdge(edgeType, this,
                node);
        String uri = edge.getURIAsStr();
        int index = edgeType.value();
        edgesByType[index].add(edge);
        allEdges.add(edge);
        lastEdgeAdded = edge;
    }

    public void addConnection(EdgeType edgeType, OntologyGraphNode node) {
        if (edgeType == EdgeType.PROPERTY) {
            return;
        }
        OntologyGraphEdge edge = new OntologyGraphEdge(edgeType, this,
                node);
        String uri = edge.getURIAsStr();
        int index = edgeType.value();
        edgesByType[index].add(edge);
        allEdges.add(edge);
        lastEdgeAdded = edge;
    }

    public void addConnection(OntologyGraphEdge edge) {
        addConnection(edge, edge.getNextNode());
    }

    public void addConnection(OntologyGraphEdge edge,
            OntologyGraphNode object) {
        EdgeType edgeType = edge.getEdgeType();
        if (edgeType == EdgeType.PROPERTY) {
            OntologyProperty property = edge.getProperty();
            addConnection(property, object);
        } else {
            addConnection(edgeType, object);
        }
    }

    public void removeConnection(OntologyGraphEdge edge) {
        List<OntologyGraphEdge> remove = new ArrayList<OntologyGraphEdge>();
        for (OntologyGraphEdge localEdge : allEdges) {
            if (localEdge.equals(edge)) {
                remove.add(localEdge);
                EdgeType edgeType = edge.getEdgeType();
                int index = edgeType.value();
                edgesByType[index].remove(edge);
            }
        }

        for (OntologyGraphEdge localEdge : remove) {
            allEdges.remove(localEdge);
        }
    }

    public boolean edgeExists(OntologyGraphEdge edge) {
        for (OntologyGraphEdge e : allEdges) {
            if (edgesAreEqual(edge, e)) {
                return true;
            }
        }

        return false;
    }

    public boolean edgeExists(OntologyGraphEdge edge, OntologyGraphNode next) {
        for (OntologyGraphEdge e : allEdges) {
            if (edgesAreEqual(edge, e, next)) {
                return true;
            }
        }

        return false;
    }

    private boolean edgesAreEqual(OntologyGraphEdge edge1, OntologyGraphEdge edge2) {
        OntologyProperty property1 = edge1.getProperty();
        OntologyProperty property2 = edge2.getProperty();
        if ((property1 == null && property2 == null)
                || (property1 != null ? property1.equals(property2) : false)) {
            return edge1.getEdgeType() == edge2.getEdgeType()
                    && edge1.getNextNode().equals(edge2.getNextNode());
        }

        return false;
    }

    private boolean edgesAreEqual(OntologyGraphEdge edge1, OntologyGraphEdge edge2, OntologyGraphNode next) {
        OntologyProperty property1 = edge1.getProperty();
        OntologyProperty property2 = edge2.getProperty();
        if ((property1 == null && property2 == null)
                || (property1 != null ? property1.equals(property2) : false)) {
            return edge1.getEdgeType() == edge2.getEdgeType()
                    && edge1.getNextNode().equals(next);
        }

        return false;
    }

    public OntologyObject getObject() {
        return object;
    }

    public OntologyClass getAsClass() {
        return (OntologyClass) object;
    }

    public OntologyDatatype getAsDatatype() {
        return (OntologyDatatype) object;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getURIAsStr() {
        return object.getURIAsStr();
    }

    public String getSQLDatatype() {
        if (label.equalsIgnoreCase("double")) {
            return "DOUBLE";
        } else if (label.equalsIgnoreCase("date")) {
            return "DATE";
        } else if (label.equalsIgnoreCase("dateTime")) {
            return "DATETIME";
        } else if (label.equalsIgnoreCase("int")
                || label.equalsIgnoreCase("uint")
                || label.equalsIgnoreCase("int4")
                || label.equalsIgnoreCase("uint4")
                || label.equalsIgnoreCase("integer")
                || label.equalsIgnoreCase("positiveInteger")
                || label.equalsIgnoreCase("signedInteger")
                || label.equalsIgnoreCase("unsignedInteger")) {
            return "INT";
        } else if (label.equalsIgnoreCase("int8")
                || label.equalsIgnoreCase("uint8")
                || label.equalsIgnoreCase("integer8")
                || label.equalsIgnoreCase("long")
                || label.equalsIgnoreCase("longint")
                || label.equalsIgnoreCase("longinteger")) {
            return "INT8";
        } else {
            return "VARCHAR(255)";
        }
    }

    @Override
    public int hashCode() {
        if (object.isEntity()) {
            return ((OntologyEntity) object).getURIAsStr().hashCode();
        } else {
            return object.toString().hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof OntologyGraphNode) {
            if (object.isEntity()) {
                return ((OntologyEntity) object).getURIAsStr().equals(((OntologyEntity) ((OntologyGraphNode) obj).object).getURIAsStr());
            } else {
                return object.toString().equals(obj.toString());
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return object.toString();
    }

    public void getOWLClassExpression() {
        ((OntologyEntity) object).getOWLClassExpression();
    }
}
