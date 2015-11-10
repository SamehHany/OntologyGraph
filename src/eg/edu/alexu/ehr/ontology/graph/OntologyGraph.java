package eg.edu.alexu.ehr.ontology.graph;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import eg.edu.alexu.ehr.ontology.api.wrapper.Ontology;
import eg.edu.alexu.ehr.ontology.api.wrapper.OntologyProperty;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyClass;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyDatatype;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyEntity;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.values.OntologyIndividual;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.values.OntologyValue;
import eg.edu.alexu.ehr.util.Pair;
import eg.edu.alexu.ehr.util.io.BufferedFileReader;
import eg.edu.alexu.ehr.util.io.BufferedFileWriter;

public class OntologyGraph {

    private Ontology ontology;

    private Set<OntologyGraphNode> classes;

    private Set<OntologyGraphNode> datatypes;

    private Set<OntologyGraphNode> individuals;

    private Set<OntologyGraphNode> literals;

    private Map<OntologyGraphEdge, OntologyGraphEdge> individualToClassPropertyMap;

    private Map<OntologyEntity, OntologyGraphNode> entityMap;

    private Map<OntologyValue, OntologyGraphNode> valueMap;

    private Map<OntologyGraphNode, Partition> partitionMap;

    private Map<String, OntologyGraphObject> uriToNodeMap;

    private Set<OntologyGraphEdge> edges;

    private int noOfedges;

    public void toTable(String fileName) {
        Partition p = new Partition();
        try {
            BufferedFileWriter w = new BufferedFileWriter(fileName);
            w.writeln(p.toSchema());
            w.close();

        } catch (Exception e) {
        }

    }

    public OntologyGraph(Ontology ontology) {
        this.ontology = ontology;
        build(ontology);
    }

    public void build(Ontology ontology) {
        System.out.println("Building...");
        this.ontology = ontology;
        noOfedges = 0;
        Set<OntologyClass> classesSet = ontology.getClasses();
        Set<OntologyDatatype> datatypeSet = ontology.getDatatypes();
        Set<OntologyProperty> properties = ontology.getProperties();
        Set<OntologyIndividual> individualsSet = new HashSet<OntologyIndividual>();

        classes = new HashSet<OntologyGraphNode>(classesSet.size());
        datatypes = new HashSet<OntologyGraphNode>(datatypeSet.size());
        individuals = new HashSet<OntologyGraphNode>(individualsSet.size());
        literals = new HashSet<OntologyGraphNode>();
        individualToClassPropertyMap = new HashMap<OntologyGraphEdge, OntologyGraphEdge>();

        entityMap
                = new HashMap<OntologyEntity, OntologyGraphNode>(classesSet.
                        size());
        valueMap
                = new HashMap<OntologyValue, OntologyGraphNode>(individualsSet.
                        size());

        partitionMap
                = new HashMap<OntologyGraphNode, Partition>(classesSet.size()
                        + datatypeSet.size());

        uriToNodeMap
                = new HashMap<String, OntologyGraphObject>(classesSet.size()
                        + datatypeSet.size());

        //System.out.println("Reading classes.");
        edges = new HashSet<OntologyGraphEdge>(classesSet.size());

        for (OntologyClass clss : classesSet) {
            String uri = clss.toString();
            String label = getLabelFromUri(uri);
            OntologyGraphNode node = new OntologyGraphNode(clss, label);
            classes.add(node);
            entityMap.put(clss, node);
            uriToNodeMap.put(uri, node);

            Set<OntologyIndividual> individualsLocalSet = clss.
                    getIndividuals(ontology);
            for (OntologyIndividual individual : individualsLocalSet) {
                node = new OntologyGraphNode(individual);
                if (!individuals.contains(node)) {
                    individuals.add(node);
                    valueMap.put(individual, node);
                }
            }
        }
        //System.out.println("Classes read.");

        //System.out.println("Reading Datatypes.");
        for (OntologyDatatype datatype : datatypeSet) {
            String uri = datatype.toString();
            String label = getLabelFromUri(uri);
            OntologyGraphNode node = new OntologyGraphNode(datatype, label);
            datatypes.add(node);
            entityMap.put(datatype, node);
            uriToNodeMap.put(uri, node);
        }
        //System.out.println("Datatypes read.");

        //System.out.println("Reading properties.");
        //Map<OntologyProperty, Cardinality> cardinfo
        //= ontology.getCardinalityInfo();
        int noOfProperties = 0;
        System.out.println("Number of Properties = " + properties.size());
        for (OntologyProperty property : properties) {
            //check the cardanlity

            System.out.println("C--" + property + " " + property.
                    getCardinality());
            Set<OntologyClass> domains = property.getDomains(ontology);
            Set<OntologyEntity> ranges = property.getRanges(ontology);

            //System.out.println("Property: " + ++noOfProperties);
            for (OntologyClass domain : domains) {
                OntologyGraphNode domainNode = entityMap.get(domain);
                for (OntologyEntity range : ranges) {
                    OntologyGraphNode rangeNode;
                    if (entityMap.containsKey(range)) {
                        rangeNode = entityMap.get(range);
                    } else {
                        rangeNode = new OntologyGraphNode(range);
                        addEntity(rangeNode, entityMap);
                    }

                    domainNode.addConnection(property, rangeNode);
                    noOfedges++;
                    edges.add(domainNode.lastEdgeAdded());
                }
            }
        }
        //System.out.println("Properties read.");

        //System.out.println("Connecting classes.");
        for (OntologyClass clss : classesSet) {
            Set<OntologyClass> equivalentClasses = clss.getEquivalentClasses(
                    ontology);
            Set<OntologyClass> disjointClasses = clss.getDisjointClasses(
                    ontology);
            Set<OntologyClass> subclasses = clss.getSubclasses(ontology);
            Set<OntologyIndividual> instances = clss.getIndividuals(ontology);

            OntologyGraphNode class1 = entityMap.get(clss);

            //System.out.println("Connecting equivalent classes.");
            for (OntologyClass clss2 : equivalentClasses) {
                if (clss.equals(clss2)) {
                    continue;
                }
                OntologyGraphNode class2 = entityMap.get(clss2);
                class1.addConnection(EdgeType.EQUIVALENTTO, class2);
                class2.addConnection(EdgeType.EQUIVALENTTO, class1);
                noOfedges += 2;
                setAndAddInverseEdges(class1, class2);
            }

            //System.out.println("Connecting disjoint classes.");
            for (OntologyClass clss2 : disjointClasses) {
                OntologyGraphNode class2 = entityMap.get(clss2);
                class1.addConnection(EdgeType.DISJOINTWITH, class2);
                class2.addConnection(EdgeType.DISJOINTWITH, class1);
                noOfedges += 2;
                setAndAddInverseEdges(class1, class2);
            }

            //System.out.println("Connecting subclasses.");
            for (OntologyClass clss2 : subclasses) {
                OntologyGraphNode class2 = entityMap.get(clss2);
                class1.addConnection(EdgeType.SUBCLASS, class2);
                class2.addConnection(EdgeType.SUPERCLASS, class1);
                noOfedges += 2;
                setAndAddInverseEdges(class1, class2);
            }

            //System.out.println("Connecting instances.");
            for (OntologyIndividual ind : instances) {
                OntologyGraphNode individual = valueMap.get(ind);
                individuals.add(individual);
                class1.addConnection(EdgeType.INSTANCE, individual);
                individual.addConnection(EdgeType.INSTANCEOF, class1);
                noOfedges += 2;
                setAndAddInverseEdges(class1, individual);

                Map<OntologyProperty, Set<OntologyValue>> indValues = ind.
                        getPropertyValues(ontology);
                Set<OntologyProperty> keys = indValues.keySet();

                //System.out.println("Connecting instances to properties.");
                // Individual properties
                // should not be commented
//                for (OntologyProperty property : keys) {
//                    Set<OntologyValue> values = indValues.get(property);
//                    for (OntologyValue value : values) {
//                        addValue(value, valueMap);
//                        OntologyGraphNode individual2 = valueMap.get(value);
//                        individual.addConnection(property, individual2);
//                        connectIndividualEdgeToClassEdge(individual, property,
//                                individual2);
//                        noOfedges++;
//                        edges.add(individual.lastEdgeAdded());
//                    }
//                }
            }
        }

        System.out.println("Number of nodes: " + (classes.size() + datatypes.
                size()));
        System.out.println("Number of edges: " + edges.size());
        System.out.println("Number of Individuals: " + individuals.size());
    }

    private String getLabelFromUri(String uri) {
        int len = uri.length();
        int end = uri.charAt(len - 1) == '/' ? len - 1 : len;
        for (int i = len - 1; i >= 0; i--) {
            char ch = uri.charAt(i);
            if (ch == '#' || ch == '/') {
                return uri.substring(i + 1, end);
            }
        }

        return "";
    }

    private void collapseEequivalentClasses() {
        // Collapse equivalent classes
        Set<OntologyGraphNode> skipSet = new HashSet<OntologyGraphNode>();
        Map<OntologyGraphNode, OntologyGraphNode> equivalenceMap
                = new HashMap<OntologyGraphNode, OntologyGraphNode>();

        for (OntologyGraphNode clss : classes) {
            if (skipSet.contains(clss)) {
                continue;
            }
            Set<OntologyGraphNode> equivalentClasses
                    = clss.getNextNodes(EdgeType.EQUIVALENTTO);
            for (OntologyGraphNode equivalentClass : equivalentClasses) {
                if (clss == equivalentClass) {
                    continue;
                }
                skipSet.add(equivalentClass);
                equivalenceMap.put(equivalentClass, clss);
                Set<OntologyGraphEdge> equivalentClassEdges = equivalentClass.
                        getEdges();
                // Connect edges of equivalent classes
                for (OntologyGraphEdge edge : equivalentClassEdges) {
                    //if (edge.isInverse())
                    //continue;
                    this.edges.remove(edge);
                    if (clss.edgeExists(edge) || edge.getNextNode().equals(clss)) {
                        continue;
                    }
                    clss.addConnection(edge);
                    this.edges.add(clss.lastEdgeAdded());
                }
            }
        }

        for (OntologyGraphNode clss : skipSet) {
            classes.remove(clss);
        }

        // Connect edges to equivalent classes (edges going to equivalent classes)
        List<OntologyGraphEdge> edgesToRemove = new ArrayList<OntologyGraphEdge>();
        List<OntologyGraphEdge> edgesToAdd = new ArrayList<OntologyGraphEdge>();
        for (OntologyGraphEdge edge : this.edges) {
            if (equivalenceMap.containsKey(edge.getNextNode())) {
                OntologyGraphNode equivalentClass = edge.getNextNode();
                OntologyGraphNode clss = equivalenceMap.get(equivalentClass);
                OntologyGraphNode subject = edge.getPreviousNode();
                edgesToRemove.add(edge);
                if (subject.edgeExists(edge, clss)) {
                    continue;
                }

                subject.addConnection(edge, clss);
                edgesToAdd.add(subject.lastEdgeAdded());
            }
        }

        for (OntologyGraphEdge edge : edgesToRemove) {
            this.edges.remove(edge);
        }
        for (OntologyGraphEdge edge : edgesToAdd) {
            this.edges.add(edge);
        }

        for (OntologyGraphEdge edge : edges) {
            String uri = edge.getURIAsStr();
            uriToNodeMap.put(uri, edge);
        }

        for (OntologyGraphNode clss : classes) {
            Set<OntologyGraphNode> superclasses = clss.getNextNodes(
                    EdgeType.SUPERCLASS);

        }
    }

    private void setAndAddInverseEdges(OntologyGraphNode class1,
            OntologyGraphNode class2) {
        OntologyGraphEdge lastAdded1
                = class1.lastEdgeAdded();
        OntologyGraphEdge lastAdded2
                = class2.lastEdgeAdded();
        lastAdded1.inverseOf(lastAdded2);
        lastAdded2.inverseOf(lastAdded1);
        lastAdded2.setIsInverse();
        edges.add(lastAdded1);
        edges.add(lastAdded2);
    }

    private void connectIndividualEdgeToClassEdge(OntologyGraphNode individual,
            OntologyProperty property, OntologyGraphNode object) {
        Set<OntologyGraphNode> classes
                = individual.getNextNodes(EdgeType.INSTANCEOF);
        Set<OntologyGraphNode> objectClasses
                = object.getNextNodes(EdgeType.INSTANCEOF);
        Map<OntologyGraphNode, OntologyGraphEdge> classPropertyEdges
                = new HashMap<OntologyGraphNode, OntologyGraphEdge>(classes.
                        size());
        for (OntologyGraphNode clss : classes) {
            Set<OntologyGraphEdge> edgesSet = clss.getEdges(property);
            if (edgesSet.size() == 0) {
                clss.addConnection(property, objectClasses.iterator().next());
                edges.add(clss.lastEdgeAdded());
                edgesSet = clss.getEdges(property);
            }
            classPropertyEdges.put(clss, edgesSet.iterator().next());
        }
        Set<OntologyGraphEdge> edges = individual.getEdges(property);
        for (OntologyGraphEdge edge : edges) {
            for (OntologyGraphNode clss : classes) {
                OntologyGraphEdge propertyEdge = classPropertyEdges.get(clss);
                if (propertyEdge == null) {
                    continue;
                }
                individualToClassPropertyMap.put(edge, propertyEdge);
                propertyEdge.incrementWeight(OntologyGraphEdge.propertyWeight);
            }
        }
    }

    private void addEntity(OntologyGraphNode entity,
            Map<OntologyEntity, OntologyGraphNode> map) {
        if (classes.contains(entity) || datatypes.contains(entity)) {
            return;
        }
        NodeType nodeType = entity.getNodeType();
        if (nodeType == NodeType.CLASS) {
            classes.add(entity);
        } else {
            datatypes.add(entity);
        }
        map.put((OntologyEntity) entity.getObject(), entity);
    }

    private void addEntity(OntologyEntity entity,
            Map<OntologyEntity, OntologyGraphNode> map) {
        OntologyGraphNode node = new OntologyGraphNode(entity);
        if (classes.contains(node) || datatypes.contains(node)) {
            return;
        }
        NodeType nodeType = node.getNodeType();
        if (nodeType == NodeType.CLASS) {
            classes.add(node);
        } else {
            datatypes.add(node);
        }
        map.put(entity, node);
    }

    private void addValue(OntologyGraphNode value,
            Map<OntologyValue, OntologyGraphNode> map) {
        if (individuals.contains(value) || literals.contains(value)) {
            return;
        }
        NodeType nodeType = value.getNodeType();
        if (nodeType == NodeType.INDIVIDUAL) {
            individuals.add(value);
        } else {
            literals.add(value);
        }
        map.put((OntologyValue) value.getObject(), value);
    }

    private void addValue(OntologyValue value,
            Map<OntologyValue, OntologyGraphNode> map) {
        OntologyGraphNode node = new OntologyGraphNode(value);
        if (individuals.contains(node) || literals.contains(node)) {
            return;
        }
        NodeType nodeType = node.getNodeType();
        if (nodeType == NodeType.INDIVIDUAL) {
            individuals.add(node);
        } else {
            literals.add(node);
        }
        map.put(value, node);
    }

    @Override
    public String toString() {
        //String ret = "";
        StringBuffer strBuffer = new StringBuffer("");
        //strBuffer.append(" String Buffer");

        for (OntologyGraphNode subject : classes) {
            Set<OntologyGraphEdge> edges = subject.getEdges();
            for (OntologyGraphEdge predicate : edges) {
                OntologyGraphNode object = predicate.getNextNode();
                strBuffer.append(subject + " -- " + predicate + " -- " + object
                        + "\n");
            }
        }

        for (OntologyGraphNode subject : individuals) {
            Set<OntologyGraphEdge> edges = subject.getEdges();
            for (OntologyGraphEdge predicate : edges) {
                OntologyGraphNode object = predicate.getNextNode();
                strBuffer.append(subject + " -- " + predicate + " -- " + object
                        + "\n");
            }
        }

        return strBuffer.toString();
    }

    public void save(String filepath) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filepath), "utf-8"))) {
            for (OntologyGraphNode subject : classes) {
                Set<OntologyGraphEdge> edges = subject.getEdges();
                for (OntologyGraphEdge predicate : edges) {
                    OntologyGraphNode object = predicate.getNextNode();
                    writer.write(subject + " -- \"" + predicate.getWeight()
                            + "\"@"
                            + predicate.getPureURI() + " -- " + object + "\n");
                }
            }

            for (OntologyGraphNode subject : individuals) {
                Set<OntologyGraphEdge> edges = subject.getEdges();
                for (OntologyGraphEdge predicate : edges) {
                    OntologyGraphNode object = predicate.getNextNode();
                    writer.write(subject + " -- " + predicate.getWeight()
                            + "\"@"
                            + predicate.getPureURI() + " -- " + object + "\n");
                }
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean skipEdge(OntologyGraphEdge edge) {
        return edge.isDisjoint() || edge.isInstance()
                || edge.isInverse() || edge.isSubclass();
    }

    public void saveAsGraph() {
        String nodeNumberPath = "graph-node-number.txt";
        String graphPath = "graph.txt";

        int indexCounter = 0;
        Map<OntologyGraphObject, Integer> reverseIndex
                = new HashMap<OntologyGraphObject, Integer>();
        BufferedFileWriter bw = null;
        try {
            bw = new BufferedFileWriter(nodeNumberPath);
            for (OntologyGraphNode clss : classes) {
                bw.writeln(clss + ": " + ++indexCounter);
                reverseIndex.put(clss, indexCounter);
            }
            for (OntologyGraphNode datatype : datatypes) {
                bw.writeln(datatype + ": " + ++indexCounter);
                reverseIndex.put(datatype, indexCounter);
            }
            for (OntologyGraphEdge edge : edges) {
                if (skipEdge(edge)) {
                    continue;
                }
                bw.writeln(edge + ": " + ++indexCounter);
                reverseIndex.put(edge, indexCounter);
            }
            bw.close();

            int numberOfNodes = classes.size() + datatypes.size() + edges.size();

            Map<Integer, Float>[] adjacencyArray = new Map[numberOfNodes];
            for (int i = 0; i < adjacencyArray.length; i++) {
                adjacencyArray[i] = new HashMap<Integer, Float>();
            }

            int noOfEdges = 0;
            for (OntologyGraphEdge edge : edges) {
                if (skipEdge(edge)) {
                    continue;
                }
                OntologyGraphNode subject = edge.getPreviousNode();
                OntologyGraphNode object = edge.getNextNode();
                if (subject.isValue() || object.isValue()) {
                    continue;
                }
                int subjectNumber = reverseIndex.get(subject) - 1;
                int predicateNumber = reverseIndex.get(edge) - 1;
                int objectNumber = reverseIndex.get(object) - 1;

                if (!adjacencyArray[subjectNumber].containsKey(predicateNumber)) {
                    adjacencyArray[subjectNumber].put(predicateNumber, edge.
                            getWeight());
                    adjacencyArray[predicateNumber].put(subjectNumber, edge.
                            getWeight());
                    noOfEdges++;
                } else {
                    float newWeight = adjacencyArray[subjectNumber].get(
                            predicateNumber) + edge.getWeight();
                    adjacencyArray[subjectNumber].
                            put(predicateNumber, newWeight);
                    adjacencyArray[predicateNumber].
                            put(subjectNumber, newWeight);
                }

                if (!adjacencyArray[predicateNumber].containsKey(objectNumber)) {
                    adjacencyArray[predicateNumber].put(objectNumber, edge.
                            getWeight());
                    adjacencyArray[objectNumber].put(predicateNumber, edge.
                            getWeight());
                    noOfEdges++;
                } else {
                    float newWeight = adjacencyArray[predicateNumber].get(
                            objectNumber) + edge.getWeight();
                    adjacencyArray[predicateNumber].put(objectNumber, newWeight);
                    adjacencyArray[objectNumber].put(predicateNumber, newWeight);
                }

                /*
                 * if (!adjacencyArray[objectNumber].containsKey(subjectNumber)) {
                 * adjacencyArray[objectNumber].put(subjectNumber, edge.getWeight());
                 * } else {
                 * float newWeight = adjacencyArray[objectNumber].get(subjectNumber) + edge.getWeight();
                 * adjacencyArray[objectNumber].put(subjectNumber, newWeight);
                 * }
                 */
            }

            noOfEdges = 0;
            for (Map<Integer, Float> map : adjacencyArray) {
                noOfEdges += map.size();
            }

            noOfEdges /= 2;

            bw = new BufferedFileWriter(graphPath);
            bw.writeln(numberOfNodes + " " + noOfEdges + " 001");

            for (Map<Integer, Float> map : adjacencyArray) {
                int size = map.size();
                if (size < 1) {
                    bw.writeln();
                    continue;
                }
                Iterator<Integer> keys = map.keySet().iterator();
                int key = keys.next();
                int value = (int) Math.floor((double) map.get(key));
                bw.write("" + (key + 1) + " " + value);
                while (keys.hasNext()) {
                    key = keys.next();
                    //value = map.get(key);
                    value = (int) Math.floor((double) map.get(key));
                    bw.write(" " + (key + 1) + " " + value);
                }
                bw.writeln();
            }

            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(OntologyGraph.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public void generateSchema(int noOfPartitions) {
        generateSchema("graph.txt.part." + noOfPartitions);
    }

    public void generateSchema() {
        generateSchema("graph.txt.part.10");
    }

    public void generateSchema(String partitionsPath) {
        generateSchema("ontology.sql", partitionsPath);
    }

    public void generateSchema(String schemaPath, String partitionsPath) {
        BufferedFileWriter writer = null;
        BufferedFileReader reader = null;
        try {
            writer = new BufferedFileWriter(schemaPath);
            reader = new BufferedFileReader(partitionsPath);
            Map<Integer, OntologyGraphObject> idMap = readIds();

            String line;
            int nodeId = 0;
            Set<Partition> partitions = new HashSet<Partition>();
            Map<Integer, Partition> map = new HashMap<Integer, Partition>();
            while ((line = reader.readLine()) != null) {
                int partitionId = Integer.parseInt(line);
                OntologyGraphObject node = idMap.get(++nodeId);
                if (map.containsKey(partitionId)) {
                    map.get(partitionId).add(node);
                } else {
                    Partition partition = new Partition(partitionId);
                    partition.add(node);
                    map.put(partitionId, partition);
                    partitions.add(partition);
                }
            }
            reader.close();

            writer.writeln("CREATE DATABASE Ontology");
            writer.writeln();
            //noOfObjectsInSet
            for (Partition partition : partitions) {
                writer.writeln(partition.toSchema());
            }
            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(OntologyGraph.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    private List<Pair<String, String>> labelsAndDatatypes(OntologyGraphNode node,
            Set<OntologyGraphObject> set) {
        Set<OntologyGraphEdge> edges = node.getEdges();
        List<Pair<String, String>> ret
                = new ArrayList<Pair<String, String>>(edges.size());
        for (OntologyGraphEdge edge : edges) {
            if (skipEdge(edge)) {
                continue;
            }

            OntologyGraphNode next = edge.getNextNode();
            if (!set.contains(next)) {
                continue;
            }
            Pair<String, String> pair;
            if (next.isClass()) {
                pair = new Pair<String, String>(edge.getLabel(), "LONG");
            } else {
                pair = new Pair<String, String>(edge.getLabel(), next.
                        getSQLDatatype());
            }
            ret.add(pair);
        }

        return ret;
    }

    private int noOfObjectsInSet(OntologyGraphNode subject,
            Set<OntologyGraphObject> set) {
        Set<OntologyGraphEdge> edges = subject.getEdges();

        int count = 0;
        for (OntologyGraphEdge edge : edges) {
            if (!edge.isProperty()) {
                continue;
            }
            OntologyGraphNode node = edge.getNextNode();
            if (set.contains(node)) {
                count++;
            }
        }

        return count;
    }

    private Map<Integer, OntologyGraphObject> readIds() {
        return readIds("graph-node-number.txt");
    }

    private Map<Integer, OntologyGraphObject> readIds(String path) {
        Map<Integer, OntologyGraphObject> map
                = new HashMap<Integer, OntologyGraphObject>(classes.size()
                        + datatypes.size());
        try {
            BufferedFileReader br = new BufferedFileReader(
                    "graph-node-number.txt");
            String line;
            while ((line = br.readLine()) != null) {
                int len = line.length();
                String uri = "";
                int id = 0;
                for (int i = len - 1; i >= 0; i--) {
                    if (line.charAt(i) == ':' && line.charAt(i + 1) == ' ') {
                        uri = line.substring(0, i);
                        id = Integer.parseInt(line.substring(i + 2));
                        break;
                    }
                }

                OntologyGraphObject node = uriToNodeMap.get(uri);
                map.put(id, node);
            }

            br.close();

            return map;
        } catch (IOException ex) {
            Logger.getLogger(OntologyGraph.class.getName()).
                    log(Level.SEVERE, null, ex);
            return null;
        }

    }

    private class Partition {

        private int id;

        private Set<OntologyGraphObject> nodes;

        public Partition(int id) {
            this.id = id;
            nodes = new HashSet<OntologyGraphObject>();
        }

        public Partition(int id, int size) {
            this.id = id;
            nodes = new HashSet<OntologyGraphObject>(size);
        }

        public Partition(int id, Set<OntologyGraphObject> list) {
            this.id = id;
            nodes = list;
        }

        public Partition() {
            nodes = new HashSet<OntologyGraphObject>(classes.size());
            nodes.addAll(classes);
            for (OntologyGraphEdge edge : edges) {
                if (!edge.isProperty()) {
                    continue;
                }
                if (edge.getPreviousNode().isClass() && (edge.getNextNode().
                        isClass() || edge.getNextNode().isDataType())) {
                    nodes.add(edge);
                }
            }
        }

        public void add(OntologyGraphObject node) {
            nodes.add(node);
        }

        public int getId() {
            return id;
        }

        public Set<OntologyGraphObject> getAllNodes() {
            return nodes;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public boolean equals(Object partition) {
            return id == ((Partition) partition).id;
        }

        @Override
        public String toString() {
            return "" + id;
        }

        public String toSchema() {
            String DDL = "";
            Set<OntologyGraphObject> graphObjects = getAllNodes();

            OntologyGraphNode subject = null;// = (OntologyGraphNode)nodes.iterator().next();
            for (OntologyGraphObject graphObject : graphObjects) {
                if (graphObject instanceof OntologyGraphNode) {
                    subject = (OntologyGraphNode) graphObject;
                    break;
                }
            }
            if (subject == null) {
                return DDL;
            }

            int noOfObjects = 0;
            int noOfNodes = 0;
            for (OntologyGraphObject objectNode : graphObjects) {
                if (!(objectNode instanceof OntologyGraphNode)) {
                    continue;
                }
                noOfNodes++;
                OntologyGraphNode node = (OntologyGraphNode) objectNode;
                if (!node.isClass()) {
                    continue;
                }
                int tmpNoOfSubjects = noOfObjectsInSet(node, graphObjects);
                if (tmpNoOfSubjects > noOfObjects) {
                    subject = node;
                    noOfObjects = tmpNoOfSubjects;
                }
            }

            String tableName = subject.getLabel();
            DDL = DDL + "CREATE TABLE " + tableName;
            DDL = DDL + "(" + "\n";

            List<Pair<String, String>> labelsAndDatatypes
                    = labelsAndDatatypes(subject, graphObjects);
            Iterator<Pair<String, String>> iterator = labelsAndDatatypes.
                    iterator();
                    //Pair<String, String> labelAndDatatype = iterator.next();

            //writer.write("\t" + labelAndDatatype.getFirst()
            //        + " " + labelAndDatatype.getSecond());
            DDL = DDL + "\tId LONG";

            while (iterator.hasNext()) {
                Pair<String, String> labelAndDatatype = iterator.next();

                DDL = DDL + (",\n");
                DDL = DDL + "\t" + labelAndDatatype.getFirst()
                        + " " + labelAndDatatype.getSecond();

            }

            DDL = DDL + "\n";
            DDL = DDL + ");\n";
            DDL = DDL + "\n";

            if (noOfNodes - noOfObjects > 0) {
                DDL = DDL + ("CREATE TABLE " + tableName + "Triple") + "\n";
                DDL = DDL + ("(") + "\n";
                DDL = DDL + ("\tId LONG," + "\n");
                DDL = DDL + ("\tSubject LONG,") + "\n";

                DDL = DDL + ("\tPredicate LONG,") + "\n";
                DDL = DDL + ("\tObject LONG,") + "\n";
                DDL = DDL + (")") + "\n";

            }
            return DDL;
        }

    }

}
