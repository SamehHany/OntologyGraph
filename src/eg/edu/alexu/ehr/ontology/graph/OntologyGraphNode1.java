/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eg.edu.alexu.ehr.ontology.graph;

import eg.edu.alexu.ehr.ontology.api.wrapper.thing.OntologyObject;
import eg.edu.alexu.ehr.ontology.api.wrapper.thing.object.entities.OntologyObjectType;
import eg.edu.alexu.ehr.ontology.api.wrapper.thing.object.values.OntologyValue;

/**
 *
 * @author Mina R. Waheeb
 */
public class OntologyGraphNode1 {

    private OntologyObject object;

    private NodeType nodeType;

    private String label;

    public OntologyGraphNode1(OntologyObject object) {
        this.object = object;
        setNodeType(object);
        label = "";
    }

    private NodeType setNodeType(OntologyObject object) {
        if (object.isEntity()) {
            if (((OntologyObjectType) object).isClass()) {
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

}
