package eg.edu.alexu.ehr.ontology.api.wrapper;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyClass;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyDatatype;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyEntity;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLProperty;
import uk.ac.manchester.cs.owl.owlapi.OWL2DatatypeImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDatatypeImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

public class OntologyProperty {
	private OWLProperty property;
	private boolean isObjectProperty;
	
	public OntologyProperty(OWLProperty property) {
		this.property = property;
		if (property instanceof OWLObjectPropertyImpl)
			isObjectProperty = true;
		else
			isObjectProperty = false;
	}
	
	public OntologyProperty(OWLDataProperty property) {
		this.property = (OWLProperty)property;
		isObjectProperty = false;
	}
	
	public OntologyProperty(OWLObjectProperty property) {
		this.property = (OWLProperty)property;
		isObjectProperty = true;
	}
	
	public OntologyProperty(OWLDataPropertyExpression property) {
		this.property = (OWLProperty)property;
		isObjectProperty = false;
	}
	
	public OntologyProperty(OWLObjectPropertyExpression property) {
		this.property = (OWLProperty)property;
		isObjectProperty = true;
	}
	
	public OntologyProperty(String uri) {
		property = new OWLObjectPropertyImpl(IRI.create(uri));
		isObjectProperty = true;
	}
	
	public OntologyProperty(String uri, boolean isObject) {
		if (isObject) {
			property = new OWLObjectPropertyImpl(IRI.create(uri));
			isObjectProperty = true;
		}
		else {
			property = new OWLDataPropertyImpl(IRI.create(uri));
			isObjectProperty = false;
		}
	}
	
	public Set<OntologyClass> getDomains(Ontology ontology) {
		Set<OWLNamedObject> domains = property.getDomains(ontology.getOWLOntology());
		
		Set<OntologyClass> set = new HashSet<OntologyClass>(domains.size());
		for (OWLNamedObject domain : domains)
			set.add(new OntologyClass((OWLClass)domain));
		
		return set;
	}
	
	public Set<OntologyEntity> getRanges(Ontology ontology) {
		Set<OWLNamedObject> ranges = property.getRanges(ontology.getOWLOntology());
		
		Set<OntologyEntity> set = new HashSet<OntologyEntity>(ranges.size());
		for (OWLNamedObject range : ranges) {
			if (range instanceof OWLClassImpl)
				set.add(new OntologyClass((OWLClass)range));
			else if (range instanceof OWLDatatypeImpl || range instanceof OWL2DatatypeImpl)
				set.add(new OntologyDatatype((OWLDatatype)range));
		}
		
		return set;
	}
	
	private Set<OntologyClass> allClasses(Ontology ontology) {
		Set<OWLClass> classes = ontology.getOWLOntology().getClassesInSignature();
		Set<OntologyClass> set = new HashSet<OntologyClass>(classes.size());
		
		for (OWLClass clss : classes)
			set.add(new OntologyClass(clss));
		
		return set;
	}
	
	private Set<OntologyEntity> allClassesAndDatatypes(Ontology ontology) {
		Set<OWLClass> classes = ontology.getOWLOntology().getClassesInSignature();
		Set<OWLDatatype> datatypes = ontology.getOWLOntology().getDatatypesInSignature();
		Set<OntologyEntity> set = new HashSet<OntologyEntity>(classes.size() + datatypes.size());
		
		for (OWLClass clss : classes)
			set.add(new OntologyClass(clss));
		
		for (OWLDatatype datatype : datatypes)
			set.add(new OntologyDatatype(datatype));
		
		return set;
	}
	
	public IRI getIRI() {
		return property.getIRI();
	}
	
	public URI getURI() {
		return property.getIRI().toURI();
	}
	
	public String getURIAsStr() {
		return property.getIRI().toString();
	}
	
	public boolean isObjectProperty() {
		return isObjectProperty;
	}
	
	public boolean isDataProperty() {
		return !isObjectProperty;
	}
	
	@Override
	public int hashCode() {
		return property.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return property.equals(((OntologyProperty)obj).property);
	}
	
	@Override
	public String toString() {
		return property.getIRI().toString();
	}
}
