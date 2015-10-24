package eg.edu.alexu.ehr.ontology.api.wrapper;

import eg.edu.alexu.ehr.ontology.api.wrapper.thing.OntologyProperty;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import eg.edu.alexu.ehr.ontology.api.wrapper.thing.object.entities.OntologyClass;
import eg.edu.alexu.ehr.ontology.api.wrapper.thing.object.entities.OntologyDatatype;

public class Ontology {
	private OWLOntology ontology;
	
	public Ontology(String pathToOWLFile) {
		File file = new File(pathToOWLFile);
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;
		try {
			ontology = manager.loadOntologyFromOntologyDocument(file);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.ontology = ontology;
	}
	
	public Ontology(OWLOntology ontology) {
		this.ontology = ontology;
	}
	
	public OWLOntology getOWLOntology() {
		return ontology;
	}
	
	public Set<OntologyClass> getClasses() {
		Set<OWLClass> classes = ontology.getClassesInSignature();
		Set<OntologyClass> set = new HashSet<OntologyClass>(classes.size());
		for (OWLClass clss : classes)
			set.add(new OntologyClass(clss));
		
		return set;
	}
	
	public Set<OntologyProperty> getProperties() {
		Set<OWLDataProperty> dataPropertySet = ontology.getDataPropertiesInSignature();
		Set<OWLObjectProperty> objectPropertySet = ontology.getObjectPropertiesInSignature();
		Set<OntologyProperty> set = new HashSet<OntologyProperty>(dataPropertySet.size()
				+ objectPropertySet.size());
		for (OWLDataProperty property: dataPropertySet)
			set.add(new OntologyProperty(property));
		for (OWLObjectProperty property: objectPropertySet)
			set.add(new OntologyProperty(property));
		
		return set;
	}
	
	public Set<OntologyDatatype> getDatatypes() {
		Set<OWLDatatype> datatypes = ontology.getDatatypesInSignature();
		Set<OntologyDatatype> set = new HashSet<OntologyDatatype>(datatypes.size());
		for (OWLDatatype datatype : datatypes)
			set.add(new OntologyDatatype(datatype));
		
		return set;
	}
	
	@Override
	public String toString() {
		return ontology.toString();
	}
}
