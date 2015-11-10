/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eg.edu.alexu.ehr.ontology;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import eg.edu.alexu.ehr.ontology.graph.OntologyGraph;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

class Axiom {

    static OWLObjectVisitor objectVisitor = new OWLObjectVisitor();

    public static void processClasses(OWLOntology ontology, OntologyGraph g) {

        if (ontology != null) {
            for (OWLEntity ent : ontology.getSignature()) {
                // System.out.println(ent.getEntityType().toString() + " " + ent);
                //ent.accept(objectVisitor);
                System.out.println(ent.getIRI());
                if (ent.getEntityType() == EntityType.CLASS) {
                    OWLClass cls = (OWLClass) ent;
                    for (OWLAnnotation a : cls.getAnnotations(ontology)) {
                        //  a.accept(objectVisitor);
                        // System.out.println("\t\t"+a);
                    }
                    for (OWLIndividual a : cls.getIndividuals(ontology)) {
                        //a.accept(objectVisitor);
                        //System.out.println("\t\t**\t"+a);
                    }
                    for (OWLClassExpression expr : cls.getSuperClasses(ontology)) {
                        System.out.println("\t!!!!!" + expr + ":::" + expr.
                                getClassExpressionType());
                        //expr.accept(objectVisitor);
                    }
                }
                System.out.println("---");
            }
        }
    }

    public static void processAxioms(OWLOntology ontology) {

        if (ontology != null) {
            Set<OWLAxiom> axiomSet = ontology.getAxioms();
            HashMap<String, Integer> axiomsMap = new HashMap<String, Integer>();
            if (axiomSet != null && axiomSet.size() > 0) {
                Iterator<OWLAxiom> setIter = axiomSet.iterator();
                OWLAxiom axiom = null;
                while (setIter.hasNext()) {
                    axiom = setIter.next();
                    //System.out.println(axiom);
                    if (axiomsMap.containsKey(axiom.getAxiomType().getName())) {
                        axiomsMap.put(axiom.getAxiomType().getName(), axiomsMap.
                                get(axiom.getAxiomType().getName()) + 1);
                    } else {
                        axiomsMap.put(axiom.getAxiomType().getName(), 1);
                    }
                    axiom.accept(objectVisitor);
                }
                System.out.println("-------------- Axiom Info for Ontology =["
                        + ontology + "]");
                System.out.println("No.of Axiom Types =[" + axiomsMap.size()
                        + "]");
                Iterator<String> mapIter = axiomsMap.keySet().iterator();
                String axiomType = null;
                while (mapIter.hasNext()) {
                    axiomType = mapIter.next();
                    System.out.println("Axiom Type =[" + axiomType
                            + "] No.of Axioms =[" + axiomsMap.get(axiomType)
                            + "]");
                }
                System.out.println(
                        "-------------- ------------------------------------------");
            }
        }
    }

    public static void processProperties(OWLOntology ontology) {

        if (ontology != null) {
            for (OWLDataProperty dataprop : ontology.
                    getDataPropertiesInSignature()) {
                System.out.println("data prob" + dataprop);
                for (OWLAnonymousIndividual vid : dataprop.
                        getAnonymousIndividuals()) {
                    System.out.println("vid " + vid);
                }
                for (OWLDataRange cls : dataprop.getRanges(ontology)) {
                    System.out.println("cls " + cls);
                }

                for (OWLClass cls : dataprop.getClassesInSignature()) {
                    System.out.println("cls " + cls);
                }
                dataprop.accept(objectVisitor);

                Set<OWLDataPropertyRangeAxiom> ranges
                        = ontology.getDataPropertyRangeAxioms(dataprop);

                for (OWLDataPropertyRangeAxiom range : ranges) {
                    System.out.println(range);
                }
            }
        }

    }

    public static void processAnnotation(OWLOntology ontology) {

        if (ontology != null) {
            Set<OWLAnnotation> annotationSet = ontology.getAnnotations();
            HashMap<String, Integer> annotationsMap = new HashMap<>();
            if (annotationSet != null && annotationSet.size() > 0) {
                Iterator<OWLAnnotation> setIter = annotationSet.iterator();
                OWLAnnotation annotation = null;
                while (setIter.hasNext()) {
                    annotation = setIter.next();
                    System.out.println(annotation);
                    String s = annotation.getProperty().toString() + ":"
                            + annotation.getValue().toString();
                    if (annotationsMap.containsKey(s)) {
                        annotationsMap.put(s, annotationsMap.get(s) + 1);
                    } else {
                        annotationsMap.put(s, 1);
                    }
                    annotation.accept(objectVisitor);
                }
                System.out.println(
                        "-------------- Annotation Info for Ontology =["
                        + ontology + "]");
                System.out.println("No.of Annotation Types =[" + annotationsMap.
                        size() + "]");
                Iterator<String> annotationIter = annotationsMap.keySet().
                        iterator();
                String annotationType = null;
                while (annotationIter.hasNext()) {
                    annotationType = annotationIter.next();
                    System.out.println("Annotation Type =[" + annotationType
                            + "] No.of annotation =[" + annotationsMap.get(
                                    annotationType) + "]");
                }
                System.out.println(
                        "-------------- ------------------------------------------");
            }
        }
    }

}
