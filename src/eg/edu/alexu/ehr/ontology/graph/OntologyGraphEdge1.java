/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eg.edu.alexu.ehr.ontology.graph;

/**
 *
 * @author sameh
 */
public class OntologyGraphEdge1 {

    private OntologyGraphNode prev;
    private OntologyGraphNode next;

    private float weight;

    OntologyGraphEdge1(OntologyGraphNode prev, OntologyGraphNode next) {
        this.prev = prev;
        this.next = next;

        weight = 1.0f;
    }

    OntologyGraphEdge1(OntologyGraphNode prev, OntologyGraphNode next, float weight) {
        this.prev = prev;
        this.next = next;

        this.weight = weight;
    }

    public OntologyGraphNode getPrev() {
        return prev;
    }

    public OntologyGraphNode getNext() {
        return next;
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
        weight += x;
    }

    public void decrementWeight() {
        weight--;
    }

    public void decrementWeight(float x) {
        weight -= x;
    }

    public void multiplyWeightBy(float p) {
        weight *= p;
    }

    public void divideWeightBy(float d) {
        weight /= d;
    }
}
