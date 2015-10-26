/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eg.edu.alexu.ehr.ontology.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import eg.edu.alexu.ehr.ontology.api.wrapper.Ontology;
import eg.edu.alexu.ehr.ontology.api.wrapper.thing.OntologyObject;
import eg.edu.alexu.ehr.ontology.api.wrapper.thing.OntologyProperty;
import eg.edu.alexu.ehr.ontology.api.wrapper.thing.object.entities.OntologyClass;
import eg.edu.alexu.ehr.ontology.api.wrapper.thing.object.entities.OntologyDatatype;
import org.apache.commons.math3.linear.OpenMapRealMatrix;

/**
 *
 * @author Mina R. Waheeb
 */
public class OntologyGraph1 {
    private Ontology ontology;
    private float weight;
    private List<OntologyGraphNode1> allNodes;
    private List<OntologyGraphNode1> objectNodes;
    private List<OntologyGraphNode1> edgeNodes; // Nodes used  as edges
    private Map<OntologyGraphNode1, Integer> reverseIndex;

    private OpenMapRealMatrix m; // Sparse Matrix

    private Map<OntologyGraphNode1, OntologyGraphNode1> individualToClassPropertyMap;

    private Map<OntologyObject, OntologyGraphNode1> objectToNodeMap;

    public OntologyGraph1(Ontology ontology) {
        build(ontology);
    }

    public void build(Ontology ontology) {
        this.ontology = ontology;
        weight = 0.0f;
        Set<OntologyClass> classesSet = ontology.getClasses();
        Set<OntologyDatatype> datatypeSet = ontology.getDatatypes();
        Set<OntologyProperty> propertiesSet = ontology.getProperties();

        reverseIndex = new HashMap<OntologyGraphNode1, Integer>(classesSet.size() + datatypeSet.size());

        int index = 0;
        for(OntologyClass clss : classesSet) {
            OntologyGraphNode1 node = new OntologyGraphNode1(clss);
            allNodes.add(node);
            objectNodes.add(node);
            reverseIndex.put(node, index++);
        }

        for(OntologyDatatype datatype : datatypeSet) {
            OntologyGraphNode1 node = new OntologyGraphNode1(datatype);
            allNodes.add(node);
            objectNodes.add(node);
            reverseIndex.put(node, index++);
        }
        
        for(OntologyProperty property : propertiesSet) {
            OntologyGraphNode1 node = new OntologyGraphNode1(property);
            allNodes.add(node);
            edgeNodes.add(node);
            reverseIndex.put(node, index++);
        }
    }
}
