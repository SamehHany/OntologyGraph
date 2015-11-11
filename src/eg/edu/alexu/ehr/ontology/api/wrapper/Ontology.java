package eg.edu.alexu.ehr.ontology.api.wrapper;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyClass;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyDatatype;
import eg.edu.alexu.ehr.util.Pair;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import uk.ac.manchester.cs.owl.owlapi.OWLDataPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

public class Ontology {

    private OWLOntology ontology;

    private Map<OntologyProperty, Set<OntologyClass>> discoveredDomains;
    private Map<OntologyProperty, Set<OWLObject>> discoveredRages;

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

    public Set<OntologyClass> getDiscoveredDomains(OntologyProperty property) {
        return discoveredDomains.get(property);
    }

    public OWLOntology getOWLOntology() {
        return ontology;
    }

    public Set<OntologyClass> getClasses() {
        Set<OWLClass> classes = ontology.getClassesInSignature();
        Set<OntologyClass> set = new HashSet<OntologyClass>(classes.size());
        for (OWLClass clss : classes) {
            set.add(new OntologyClass(clss));
        }

        return set;
    }

    private void discoverDomains(Map<OWLPropertyExpression,
            Pair<Cardinality, OWLEntity>> card, Set<OntologyProperty> set) {
        discoveredDomains = new HashMap<OntologyProperty, Set<OntologyClass>>();
        for (OWLPropertyExpression property : card.keySet()) {
            OntologyProperty ontologyProperty = null;

            if (property instanceof OWLDataPropertyImpl)
                ontologyProperty
                        = new OntologyProperty((OWLDataProperty)property,
                                card.get(property).getFirst());
            else if (property instanceof OWLObjectPropertyImpl)
                ontologyProperty
                        = new OntologyProperty((OWLObjectProperty)property,
                                card.get(property).getFirst());

            set.add(ontologyProperty);
            Set<OntologyClass> domain = null;
            if (discoveredDomains.containsKey(ontologyProperty))
                domain = discoveredDomains.get(ontologyProperty);
            else {
                domain = new HashSet<OntologyClass>();
                discoveredDomains.put(ontologyProperty, domain);
            }

            domain.add(new OntologyClass(card.get(property).getSecond()));
        }
    }

    public Set<OntologyProperty> getProperties() {
        Map<OWLPropertyExpression, Pair<Cardinality, OWLEntity>> card = getCardinalityInfo();

        Set<OWLDataProperty> dataPropertySet = ontology.getDataPropertiesInSignature();
        Set<OWLObjectProperty> objectPropertySet = ontology.getObjectPropertiesInSignature();
        Set<OntologyProperty> set = new HashSet<OntologyProperty>(dataPropertySet.size()
                + objectPropertySet.size());
        for (OWLDataProperty property : dataPropertySet) {
            Pair<Cardinality, OWLEntity> pair = card.get(property);
            set.add(new OntologyProperty(property, pair == null ? null
                    : pair.getFirst()));
        }
        for (OWLObjectProperty property : objectPropertySet) {
            Pair<Cardinality, OWLEntity> pair = card.get(property);
            set.add(new OntologyProperty(property, pair == null ? null
                    : pair.getFirst()));
        }

        discoverDomains(card, set);

        return set;
    }

    public Set<OntologyDatatype> getDatatypes() {
        Set<OWLDatatype> datatypes = ontology.getDatatypesInSignature();
        Set<OntologyDatatype> set = new HashSet<OntologyDatatype>(datatypes.size());
        for (OWLDatatype datatype : datatypes) {
            set.add(new OntologyDatatype(datatype));
        }

        return set;
    }

    private OWLPropertyExpression getOntologyProperty(OWLClassExpression expr) {
        if (expr instanceof OWLObjectMinCardinality) {
            OWLObjectMinCardinality d = (OWLObjectMinCardinality) expr;
            return d.getProperty();
        } else if (expr instanceof OWLObjectMaxCardinality) {
            OWLObjectMaxCardinality d = (OWLObjectMaxCardinality) expr;
            return d.getProperty();
        } else if (expr instanceof OWLObjectExactCardinality) {
            OWLObjectExactCardinality d = (OWLObjectExactCardinality) expr;
            return d.getProperty();
        } else if (expr instanceof OWLDataMinCardinality) {
            OWLDataMinCardinality d = (OWLDataMinCardinality) expr;
            return d.getProperty();
        } else if (expr instanceof OWLDataMaxCardinality) {
            OWLDataMaxCardinality d = (OWLDataMaxCardinality) expr;
            return d.getProperty();
        } else if (expr instanceof OWLDataExactCardinality) {
            OWLDataExactCardinality d = (OWLDataExactCardinality) expr;
           return  d.getProperty();
        }

        return null;
    }

    private Cardinality getObjectMaxCardinality(OWLClassExpression expr,
            Cardinality c) {

        OWLObjectMaxCardinality d = (OWLObjectMaxCardinality) expr;

        if (c == null) {
            c = new Cardinality();
            c.setMax(d.getCardinality());
        } else {
            c.setMax(d.getCardinality());
        }
        return c;
    }

    private Cardinality getDataMinCardinality(OWLClassExpression expr,
            Cardinality c) {

        OWLDataMinCardinality d = (OWLDataMinCardinality) expr;

        if (c == null) {
            c = new Cardinality();
            c.setMin(d.getCardinality());
        } else {
            c.setMin(d.getCardinality());
        }
        return c;
    }

    private Cardinality getDataMaxCardinality(OWLClassExpression expr,
            Cardinality c) {

        OWLDataMaxCardinality d = (OWLDataMaxCardinality) expr;

        if (c == null) {
            c = new Cardinality();
            c.setMax(d.getCardinality());
        } else {
            c.setMax(d.getCardinality());
        }
        return c;
    }

    private Cardinality getDataExactCardinality(OWLClassExpression expr,
            Cardinality c) {

        OWLDataExactCardinality d = (OWLDataExactCardinality) expr;

        if (c == null) {
            c = new Cardinality();
            c.setMax(d.getCardinality());
            c.setMin(d.getCardinality());
        } else {
            c.setMax(d.getCardinality());
            c.setMin(d.getCardinality());
        }
        return c;
    }

    private Map<OWLPropertyExpression,
                Pair<Cardinality, OWLEntity>>
        getCardinalityInfo() {
        Map<OWLPropertyExpression,
                Pair<Cardinality, OWLEntity>> card =
            new HashMap<OWLPropertyExpression,
                    Pair<Cardinality, OWLEntity>>();
        if (ontology != null) {
            for (OWLEntity ent : ontology.getSignature()) {
                //   System.out.println(" getCardinalityInfo "+ ent.getIRI());
                if (ent.getEntityType() == EntityType.CLASS) {
                    OWLClass cls = (OWLClass) ent;

                    for (OWLClassExpression expr : cls.getSuperClasses(
                            ontology)) {
                        //      System.out.println("getCardinalityInfo "+expr.getClassExpressionType() +"\t"+ expr+ ":");
                        OWLPropertyExpression prop = getOntologyProperty(expr);
                        Cardinality c = card.get(prop) == null ? null
                                : card.get(prop).getFirst();
                        
                        if (expr.getClassExpressionType()
                                == ClassExpressionType.DATA_MIN_CARDINALITY) {
                            c = getDataMinCardinality(expr, c);
                            card.put(prop, new Pair(c, ent));
                        } else if (expr.getClassExpressionType()
                                == ClassExpressionType.DATA_MAX_CARDINALITY) {
                            c = getDataMaxCardinality(expr, c);
                            card.put(prop, new Pair(c, ent));
                        } else if (expr.getClassExpressionType()
                                == ClassExpressionType.DATA_EXACT_CARDINALITY) {
                            c = getDataExactCardinality(expr, c);
                            card.put(prop, new Pair(c, ent));
                        } else if (expr.getClassExpressionType()
                                == ClassExpressionType.OBJECT_MAX_CARDINALITY) {
                            System.out.println("getCardinalityInfo " + expr.
                                    getClassExpressionType() + "\t" + expr + ":");

                            c = getObjectMaxCardinality(expr, c);
                            card.put(prop, new Pair(c, ent));
                        }

                    }
                }
            }
        }
        return card;
    }

    @Override
    public String toString() {
        return ontology.toString();
    }
}
