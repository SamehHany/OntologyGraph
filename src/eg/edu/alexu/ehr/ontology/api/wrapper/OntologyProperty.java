package eg.edu.alexu.ehr.ontology.api.wrapper;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyClass;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyDatatype;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyEntity;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import uk.ac.manchester.cs.owl.owlapi.OWL2DatatypeImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDatatypeImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectUnionOfImpl;

public class OntologyProperty {

    private OWLProperty property;
    private OWLAnnotationProperty annotationProperty;
    private boolean isObjectProperty;
    private boolean rangeIsEmpty;
    private boolean isAnnotation;
    private Set<OntologyProperty> inverses;

    public OntologyProperty(OWLProperty property) {
        //cardinality = new Cardinality(-1, -1);
        this.property = property;
        annotationProperty = null;
        if (property instanceof OWLObjectPropertyImpl) {
            isObjectProperty = true;
        } else {
            isObjectProperty = false;
        }
        rangeIsEmpty = false;
        isAnnotation = false;
        
        inverses = null;
    }

    public OntologyProperty(OWLDataProperty property) {
        //cardinality = new Cardinality(-1, -1);
        this.property = (OWLProperty) property;
        annotationProperty = null;
        isObjectProperty = false;
        rangeIsEmpty = false;
        isAnnotation = false;
        
        inverses = null;
    }

    public OntologyProperty(OWLObjectProperty property) {
        //cardinality = new Cardinality(-1, -1);
        this.property = (OWLProperty) property;
        annotationProperty = null;
        isObjectProperty = true;
        rangeIsEmpty = false;
        isAnnotation = false;
        
        inverses = null;
    }

    public OntologyProperty(OWLAnnotationProperty property) {
        //cardinality = new Cardinality(-1, -1);
        this.property = null;
        annotationProperty = property;
        isObjectProperty = false;
        rangeIsEmpty = false;
        isAnnotation = true;
        
        inverses = null;
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
        this.property = (OWLProperty) property;
        isObjectProperty = false;
        rangeIsEmpty = false;
        
        inverses = null;
    }

    public OntologyProperty(OWLObjectPropertyExpression property) {
        //cardinality = new Cardinality(-1, -1);
        this.property = (OWLProperty) property;
        isObjectProperty = true;
        rangeIsEmpty = false;
        
        inverses = null;
    }

    public OntologyProperty(String uri) {
        //cardinality = new Cardinality(-1, -1);
        property = new OWLObjectPropertyImpl(IRI.create(uri));
        isObjectProperty = true;
        rangeIsEmpty = false;
        
        inverses = null;
    }

    public OntologyProperty(String uri, boolean isObject) {
        //cardinality = new Cardinality(-1, -1);
        if (isObject) {
            property = new OWLObjectPropertyImpl(IRI.create(uri));
            isObjectProperty = true;
        } else {
            property = new OWLDataPropertyImpl(IRI.create(uri));
            isObjectProperty = false;
        }
        rangeIsEmpty = false;
        
        inverses = null;
    }
    
    public Set<OntologyProperty> calcullateInverses(Ontology ontology) {
        if (!isObjectProperty) {
            inverses = new HashSet();
            return inverses;
        }
        OWLOntology owlontology = ontology.getOWLOntology();
        Set<OWLObjectPropertyExpression> inverses
                        = ((OWLObjectProperty)property).
                                getInverses(owlontology);
        this.inverses = new HashSet(inverses.size());
        for (OWLObjectPropertyExpression inverse : inverses) {
            this.inverses.add(new OntologyProperty(inverse));
        }
        
        return this.inverses;
    }
    
    public Set<OntologyProperty> getInverses(Ontology ontology) {
        if (inverses != null)
            return inverses;
        return calcullateInverses(ontology);
    }
    
    public Set<OntologyProperty> getInverses() {
        return inverses;
    }

    public Set<OntologyClass> getDomains(Ontology ontology) {
        Set<OWLClassExpression> domains;
        OWLOntology owlontology = ontology.getOWLOntology();
        if (!isAnnotation) {
            domains = property.getDomains(owlontology);
            if (isObjectProperty) {
                OWLObjectPropertyExpression objProperty
                        = (OWLObjectPropertyExpression)property;
                Set<OWLObjectPropertyExpression> inverses
                        = objProperty.getInverses(owlontology);
                
                for (OWLObjectPropertyExpression inverse : inverses) {
                    Set<OWLClassExpression> ranges
                            = inverse.getRanges(owlontology);
                    domains.addAll(ranges);
                }
            }
        } else {
            Set<OWLAnnotationPropertyDomainAxiom> set
                    = owlontology.getAnnotationPropertyDomainAxioms(
                            annotationProperty);
            domains = new HashSet(set.size());

            for (OWLAnnotationPropertyDomainAxiom domainAxiom : set) {
                domains.add(new OWLClassImpl(domainAxiom.getDomain()));
            }
        }

        Set<OntologyClass> set = new HashSet(domains.size());
        for (OWLClassExpression domain : domains) {
            if (domain instanceof OWLObjectUnionOfImpl) {
                OWLObjectUnionOf union = (OWLObjectUnionOf)domain;
                Set<OWLClassExpression> clssSet = union.asConjunctSet();
                for (OWLClassExpression clss : clssSet) {
                    System.out.println(clss.toString().toUpperCase());
                }
            } else {
                set.add(new OntologyClass((OWLClass) domain));
            }
        }

        Set<OntologyClass> discoveredDOmains = ontology.getDiscoveredDomains(this);
        if (discoveredDOmains != null) {
            set.addAll(discoveredDOmains);
        }
        
        // Add subclasses
        /*List<OntologyClass> classesToAdd = new ArrayList();
        for (OntologyClass clss : set) {
            classesToAdd.addAll(clss.getAllDescendants(ontology));
        }
        set.addAll(classesToAdd);*/

        return set;
    }

    public Set<OntologyEntity> getRanges(Ontology ontology) {
        Set<OWLEntity> ranges;
        OWLOntology owlontology = ontology.getOWLOntology();
        if (!isAnnotation) {
            ranges = property.getRanges(ontology.getOWLOntology());
            if (isObjectProperty) {
                OWLObjectPropertyExpression objProperty
                        = (OWLObjectPropertyExpression)property;
                Set<OWLObjectPropertyExpression> inverses
                        = objProperty.getInverses(owlontology);
                
                for (OWLObjectPropertyExpression inverse : inverses) {
                    Set<OWLClassExpression> domains
                            = inverse.getDomains(owlontology);
                    for (OWLClassExpression domain : domains) {
                        ranges.add((OWLEntity) domain);
                    }
                }
            }
        } else {
            Set<OWLAnnotationPropertyRangeAxiom> set
                    = ontology.getOWLOntology()
                    .getAnnotationPropertyRangeAxioms(
                            annotationProperty);
            ranges = new HashSet(set.size());

            for (OWLAnnotationPropertyRangeAxiom rangeAxiom : set) {
                if (isObjectProperty)
                    ranges.add(new OWLClassImpl(rangeAxiom.getRange()));
                else
                    ranges.add(new OWLDatatypeImpl(rangeAxiom.getRange()));
            }
        }
        if (ranges.isEmpty() || ranges == null) {
            System.out.println("WARNING: range is empty for "
                    + (isAnnotation ? annotationProperty : property));
            ranges = new HashSet();
            OWLDatatype stringDatatype = new OWLDatatypeImpl(IRI.create(
                    "http://www.w3.org/TR/2001/"
                    + "REC-xmlschema-2-20010502/#string"));
            ranges.add(stringDatatype);
            rangeIsEmpty = true;
        }

        Set<OntologyEntity> set = new HashSet(ranges.size());
        for (OWLEntity range : ranges) {
            if (range instanceof OWLClassImpl) {
                set.add(new OntologyClass((OWLClass) range));
            } else if (range instanceof OWLDatatypeImpl || range instanceof OWL2DatatypeImpl) {
                set.add(new OntologyDatatype((OWLDatatype) range));
            }
        }
        
        // Add subclasses
        /*List<OntologyClass> classesToAdd = new ArrayList();
        for (OntologyEntity clss : set) {
            if (clss instanceof OntologyClass)
                classesToAdd.addAll(((OntologyClass)clss).
                        getAllDescendants(ontology));
        }
        set.addAll(classesToAdd);*/

        return set;
    }

    private Set<OntologyClass> allClasses(Ontology ontology) {
        Set<OWLClass> classes = ontology.getOWLOntology().getClassesInSignature();
        Set<OntologyClass> set = new HashSet(classes.size());

        for (OWLClass clss : classes) {
            set.add(new OntologyClass(clss));
        }

        return set;
    }

    private Set<OntologyEntity> allClassesAndDatatypes(Ontology ontology) {
        Set<OWLClass> classes = ontology.getOWLOntology().getClassesInSignature();
        Set<OWLDatatype> datatypes = ontology.getOWLOntology().getDatatypesInSignature();
        Set<OntologyEntity> set = new HashSet(classes.size() + datatypes.size());

        for (OWLClass clss : classes) {
            set.add(new OntologyClass(clss));
        }

        for (OWLDatatype datatype : datatypes) {
            set.add(new OntologyDatatype(datatype));
        }

        return set;
    }

    /*public void setCardinality(Cardinality cardinality) {
     this.cardinality = cardinality;
     }
        
     public Cardinality getCardinality() {
     return cardinality;
     }*/
    public IRI getIRI() {
        return isAnnotation ? annotationProperty.getIRI() : property.getIRI();
    }

    public URI getURI() {
        return isAnnotation ? annotationProperty.getIRI().toURI() :
                property.getIRI().toURI();
    }

    public String getURIAsStr() {
        return isAnnotation ? annotationProperty.getIRI().toString() :
                property.getIRI().toString();
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
        return isAnnotation ? annotationProperty.hashCode() :
                property.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!isAnnotation)
            return property.equals(((OntologyProperty) obj).property);
        else
            return annotationProperty.
                    equals(((OntologyProperty) obj).annotationProperty);
    }

    @Override
    public String toString() {
        return isAnnotation ? annotationProperty.toString() :
                property.getIRI().toString();
    }

}
