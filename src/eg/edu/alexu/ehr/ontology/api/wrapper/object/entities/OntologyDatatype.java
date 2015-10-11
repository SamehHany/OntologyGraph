package eg.edu.alexu.ehr.ontology.api.wrapper.object.entities;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

public class OntologyDatatype extends OntologyEntity {
	
	public OntologyDatatype(OWLDatatype datatype) {
		super((OWLEntity)datatype);
		isClass = false;
	}
	
	public OntologyDatatype(String uri) {
		super((OWLEntity)(new OWLClassImpl(IRI.create(uri))));
		isClass = false;
	}
	
	public OWLDatatype getOWLDatatype() {
		return (OWLDatatype)entity;
	}
}
