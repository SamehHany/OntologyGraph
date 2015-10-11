package eg.edu.alexu.ehr.ontology.api.wrapper.object.entities;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;

import eg.edu.alexu.ehr.ontology.api.wrapper.Ontology;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.values.OntologyIndividual;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

public class OntologyClass extends OntologyEntity {
	
	public OntologyClass(OWLClass clss) {
		super((OWLEntity)clss);
		isClass = true;
	}
	
	public OntologyClass(String uri) {
		super((OWLEntity)(new OWLClassImpl(IRI.create(uri))));
		isClass = true;
	}
	
	public Set<OntologyClass> getSubclasses(Ontology ontology) {
		Set<OWLClassExpression> subclasses = ((OWLClass)entity).getSubClasses(ontology.getOWLOntology());
		Set<OntologyClass> set = new HashSet<OntologyClass>(subclasses.size());
		for (OWLClassExpression subclass : subclasses)
			set.add(new OntologyClass((OWLClass)subclass));
		
		return set;
	}
	
	public Set<OntologyClass> getEquivalentClasses(Ontology ontology) {
		Set<OWLClassExpression> equivalentClasses =
				((OWLClass)entity).getEquivalentClasses(ontology.getOWLOntology());
		Set<OntologyClass> set = new HashSet<OntologyClass>(equivalentClasses.size());
		for (OWLClassExpression clss : equivalentClasses)
			set.add(new OntologyClass((OWLClass)clss));
		
		return set;
	}
	
	public Set<OntologyClass> getDisjointClasses(Ontology ontology) {
		Set<OWLClassExpression> disjointClasses =
				((OWLClass)entity).getDisjointClasses(ontology.getOWLOntology());
		Set<OntologyClass> set = new HashSet<OntologyClass>(disjointClasses.size());
		for (OWLClassExpression clss : disjointClasses)
			set.add(new OntologyClass((OWLClass)clss));
		
		return set;
	}
	
	public Set<OntologyIndividual> getIndividuals(Ontology ontology) {
		Set<OWLIndividual> individuals =
				((OWLClass)entity).getIndividuals(ontology.getOWLOntology());
		Set<OntologyIndividual> set = new HashSet<OntologyIndividual>(individuals.size());
		for (OWLIndividual ind : individuals)
			set.add(new OntologyIndividual(ind));
		
		return set;
	}
}
