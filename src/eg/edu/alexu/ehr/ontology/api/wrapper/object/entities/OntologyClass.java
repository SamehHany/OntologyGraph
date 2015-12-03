package eg.edu.alexu.ehr.ontology.api.wrapper.object.entities;

import java.util.HashSet;
import java.util.Set;
import eg.edu.alexu.ehr.ontology.api.wrapper.Ontology;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.values.OntologyIndividual;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

public class OntologyClass extends OntologyEntity {
    private boolean hasValue;

    public OntologyClass(OWLClass clss) {
        super((OWLEntity) clss);
        isClass = true;
        hasValue = false;
    }

    public OntologyClass(OWLEntity clss) {
        super(clss);
        isClass = true;
        hasValue = false;
    }

    public OntologyClass(String uri) {
        super((OWLEntity) (new OWLClassImpl(IRI.create(uri))));
        isClass = true;
        hasValue = false;
    }
    
    public OntologyClass(OWLClass clss, boolean hasValue) {
        super((OWLEntity) clss);
        isClass = true;
        this.hasValue = hasValue;
    }

    public OntologyClass(OWLEntity clss, boolean hasValue) {
        super(clss);
        isClass = true;
        this.hasValue = hasValue;
    }

    public OntologyClass(String uri, boolean hasValue) {
        super((OWLEntity) (new OWLClassImpl(IRI.create(uri))));
        isClass = true;
        this.hasValue = hasValue;
    }

    public Set<OntologyClass> getSubClasses(Ontology ontology) {
        Set<OWLClassExpression> subclasses = ((OWLClass) entity).getSubClasses(ontology.getOWLOntology());
        Set<OntologyClass> set = new HashSet<OntologyClass>(subclasses.size());
        for (OWLClassExpression subclass : subclasses) {
            set.add(new OntologyClass((OWLClass) subclass));
        }

        return set;
    }
    
    public Set<OntologyClass> getAllDescendants(Ontology ontology) {
        Set<OntologyClass> set = new HashSet();
        
        return getAllDescendants(this, ontology, set);
    }
    
    private Set<OntologyClass> getAllDescendants(OntologyClass clss,
            Ontology ontology, Set<OntologyClass> set) {
        Set<OntologyClass> subclasses = clss.getSubClasses(ontology);
        set.addAll(subclasses);
        
        for (OntologyClass class1 : subclasses) {
            getAllDescendants(class1, ontology, set);
        }
        
        return set;
    }

    public Set<OntologyClass> getEquivalentClasses(Ontology ontology) {
        Set<OWLClassExpression> equivalentClasses
                = ((OWLClass) entity).getEquivalentClasses(ontology.getOWLOntology());
        Set<OntologyClass> set = new HashSet<OntologyClass>(equivalentClasses.size());
        for (OWLClassExpression clss : equivalentClasses) {
            if (clss instanceof OWLClass) {
                set.add(new OntologyClass((OWLClass) clss));
            }
        }

        return set;
    }

    public Set<OntologyClass> getDisjointClasses(Ontology ontology) {
        Set<OWLClassExpression> disjointClasses
                = ((OWLClass) entity).getDisjointClasses(ontology.getOWLOntology());
        Set<OntologyClass> set = new HashSet<OntologyClass>(disjointClasses.size());
        for (OWLClassExpression clss : disjointClasses) {
            set.add(new OntologyClass((OWLClass) clss));
        }

        return set;
    }

    public Set<OntologyIndividual> getIndividuals(Ontology ontology) {
        Set<OWLIndividual> individuals
                = ((OWLClass) entity).getIndividuals(ontology.getOWLOntology());
        Set<OntologyIndividual> set = new HashSet<OntologyIndividual>(individuals.size());
        for (OWLIndividual ind : individuals) {
            set.add(new OntologyIndividual(ind));
        }

        return set;
    }
    
    public boolean hasValue() {
        return hasValue;
    }
}
