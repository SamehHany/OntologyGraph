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
        private boolean rangeIsEmpty;
	
	public OntologyProperty(OWLProperty property) {
                //cardinality = new Cardinality(-1, -1);
		this.property = property;
		if (property instanceof OWLObjectPropertyImpl)
			isObjectProperty = true;
		else
			isObjectProperty = false;
                rangeIsEmpty = false;
	}
	
	public OntologyProperty(OWLDataProperty property) {
                //cardinality = new Cardinality(-1, -1);
		this.property = (OWLProperty)property;
		isObjectProperty = false;
                rangeIsEmpty = false;
	}
	
	public OntologyProperty(OWLObjectProperty property) {
                //cardinality = new Cardinality(-1, -1);
		this.property = (OWLProperty)property;
		isObjectProperty = true;
                rangeIsEmpty = false;
	}

        /*public OntologyProperty(OWLDataProperty property, Cardinality cardinality) {
               // this.cardinality = cardinality == null ? new Cardinality(-1, -1)
                        //: cardinality;
		this.property = (OWLProperty)property;
		isObjectProperty = false;
                rangeIsEmpty = false;
	}*/

	/*public OntologyProperty(OWLObjectProperty property, Cardinality cardinality) {
                //this.cardinality = cardinality == null ? new Cardinality(-1, -1)
                        //: cardinality;
		this.property = (OWLProperty)property;
		isObjectProperty = true;
                rangeIsEmpty = false;
	}*/
        
        /*public OntologyProperty(OWLDataProperty property, int minCard, int maxCard) {
                //cardinality = new Cardinality(minCard, maxCard);
		this.property = (OWLProperty)property;
		isObjectProperty = false;
                rangeIsEmpty = false;
	}*/

	/*public OntologyProperty(OWLObjectProperty property, int minCard, int maxCard) {
                //cardinality = new Cardinality(minCard, maxCard);
		this.property = (OWLProperty)property;
		isObjectProperty = true;
                rangeIsEmpty = false;
	}*/
	
	public OntologyProperty(OWLDataPropertyExpression property) {
                //cardinality = new Cardinality(-1, -1);
		this.property = (OWLProperty)property;
		isObjectProperty = false;
                rangeIsEmpty = false;
	}
	
	public OntologyProperty(OWLObjectPropertyExpression property) {
                //cardinality = new Cardinality(-1, -1);
		this.property = (OWLProperty)property;
		isObjectProperty = true;
                rangeIsEmpty = false;
	}
	
	public OntologyProperty(String uri) {
                //cardinality = new Cardinality(-1, -1);
		property = new OWLObjectPropertyImpl(IRI.create(uri));
		isObjectProperty = true;
                rangeIsEmpty = false;
	}
	
	public OntologyProperty(String uri, boolean isObject) {
                //cardinality = new Cardinality(-1, -1);
		if (isObject) {
			property = new OWLObjectPropertyImpl(IRI.create(uri));
			isObjectProperty = true;
		}
		else {
			property = new OWLDataPropertyImpl(IRI.create(uri));
			isObjectProperty = false;
		}
                rangeIsEmpty = false;
	}
	
	public Set<OntologyClass> getDomains(Ontology ontology) {
		Set<OWLNamedObject> domains = property.getDomains(ontology.getOWLOntology());
		
		Set<OntologyClass> set = new HashSet<OntologyClass>(domains.size());
		for (OWLNamedObject domain : domains)
			set.add(new OntologyClass((OWLClass)domain));

                Set<OntologyClass> discoveredDOmains = ontology.getDiscoveredDomains(this);
                if (discoveredDOmains != null)
                    set.addAll(discoveredDOmains);

		return set;
	}
	
	public Set<OntologyEntity> getRanges(Ontology ontology) {
		Set<OWLNamedObject> ranges = property.getRanges(ontology.getOWLOntology());
                if (ranges.size() == 0 || ranges == null) {
                    System.out.println("WARNING: range is empty for "
                            + property);
                    ranges = new HashSet<OWLNamedObject>();
                    OWLDatatype stringDatatype = new OWLDatatypeImpl(IRI.create(
                            "http://www.w3.org/TR/2001/" +
                                    "REC-xmlschema-2-20010502/#string"));
                    ranges.add(stringDatatype);
                    rangeIsEmpty = true;
                }
		
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

        /*public void setCardinality(Cardinality cardinality) {
            this.cardinality = cardinality;
        }
        
        public Cardinality getCardinality() {
            return cardinality;
        }*/
	
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

        public void setRangeIsEmpty(boolean rangeIsEmpty) {
            this.rangeIsEmpty = rangeIsEmpty;
        }

        public void setRangeIsEmpty() {
            rangeIsEmpty = true;
        }

        public void resetRangeIsEmpty() {
            rangeIsEmpty = false;
        }

        public boolean rangeIsEmpty() {
            return rangeIsEmpty;
        }
        
        public Cardinality getCardinality(Ontology ontology, OntologyClass clss) {
            return ontology.getCardinality(this, clss);
        }
	
	/*@Override
	public int hashCode() {
		return property.hashCode()
                        ^ (cardinality == null ? 0 : cardinality.hashCode());
	}*/
	
	/*@Override
	public boolean equals(Object obj) {
            if (obj == null)
                return false;
            OntologyProperty otherProp = (OntologyProperty)obj;
            return property.equals(otherProp.property)
                    && (cardinality == null ? otherProp.cardinality == null
                    : cardinality.equals(otherProp.cardinality));
	}*/

        @Override
	public int hashCode() {
		return property.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
            if (obj == null)
                return false;
            return property.equals(((OntologyProperty)obj).property);
	}
	
	@Override
	public String toString() {
		return property.getIRI().toString();
	}

        
}
