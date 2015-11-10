/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eg.edu.alexu.ehr.ontology;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.util.OWLObjectVisitorAdapter;

public class OWLObjectVisitor extends OWLObjectVisitorAdapter {

    @Override
    public void visit(OWLDataMaxCardinality desc) {
        super.visit(desc);
        System.out.println("Max Cardinality =[" + desc.getCardinality() + "]");
    }

    @Override
    public void visit(OWLDataMinCardinality desc) {
        super.visit(desc);
        System.out.println("Min Cardinality =[" + desc.getCardinality() + "]");
    }

    @Override
    public void visit(OWLObjectMaxCardinality desc) {
        super.visit(desc);
        System.out.println("Object Max Cardinality =[" + desc.getCardinality()
                + "]");
    }

    @Override
    public void visit(OWLObjectMinCardinality desc) {
        super.visit(desc);
        System.out.println("Object Min Cardinality =[" + desc.getCardinality()
                + "]");
    }

    @Override
    public void visit(OWLDataExactCardinality desc) {
        super.visit(desc);
        System.out.println("Exact Data Cardinality =[" + desc.getCardinality()
                + "]" + desc.getProperty());
    }

    @Override
    public void visit(OWLObjectExactCardinality desc) {
        super.visit(desc);
        System.out.println("Exact Object Cardinality =[" + desc.getCardinality()
                + "]");
    }

    @Override
    public void visit(OWLAnnotation node) {
        super.visit(node);
        System.out.println("visitng owlannoation node =[" + node + "]");
    }
//            @Override
//	    public void 	visit(OWLAnonymousIndividual individual) {
//                         super.visit(individual);
//        System.out.println("indivual =["+individual+"]");
//            }
//            public void visit(OWLClass c){
//                super.visit(c);
//                System.out.println("*********"+c.getIRI());
//            }

    @Override
    public void visit(OWLDataPropertyRangeAxiom owldpr) {
        super.visit(owldpr);
        System.out.println(owldpr);
    }
}
