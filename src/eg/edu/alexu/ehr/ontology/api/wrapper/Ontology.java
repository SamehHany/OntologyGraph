package eg.edu.alexu.ehr.ontology.api.wrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyClass;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyDatatype;
import eg.edu.alexu.ehr.util.Pair;
import eg.edu.alexu.ehr.util.io.BufferedFileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
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
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLRestriction;
import uk.ac.manchester.cs.owl.owlapi.OWLDataPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

public class Ontology {

    private OWLOntology ontology;

    private Map<OntologyProperty, Set<OntologyClass>> discoveredDomains;

    private Map<OntologyProperty, Set<OWLObject>> discoveredRages;
    
    private Set<OWLClass> classesHaveValue;
    
    private Set<OWLClassExpression> discard;

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
        discard = new HashSet();
        //getCardinalityInfo();
    }

    public Ontology(OWLOntology ontology) {
        this.ontology = ontology;
        discard = new HashSet();
        //getCardinalityInfo();
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
            //if (discard.contains(clss))
            //    continue;
            boolean hasValue = false;
            for (OWLClassExpression expr : clss.getSuperClasses(
                            ontology)) {
                if (expr instanceof OWLObjectHasValue)
                            hasValue = true;
            }
            set.add(new OntologyClass(clss, hasValue));
        }

        return set;
    }

    private void discoverDomains(
            Map<Pair<OWLPropertyExpression, OWLEntity>, Cardinality> card,
            Set<OntologyProperty> set) {
        discoveredDomains = new HashMap<OntologyProperty, Set<OntologyClass>>();
        for (Pair<OWLPropertyExpression, OWLEntity> pair : card.keySet()) {
            OWLPropertyExpression property = pair.getFirst();
            OWLEntity entity = pair.getSecond();
            
            OntologyProperty ontologyProperty = null;

            if (property instanceof OWLDataPropertyImpl) {
                ontologyProperty
                        = new OntologyProperty((OWLDataProperty) property);
            } else if (property instanceof OWLObjectPropertyImpl) {
                ontologyProperty
                        = new OntologyProperty((OWLObjectProperty) property);
            }
            
            set.add(ontologyProperty);
            addCardinality(ontologyProperty,
                    new OntologyClass(entity), card.get(pair));
            
            Set<OntologyClass> domain = null;
            if (discoveredDomains.containsKey(ontologyProperty)) {
                domain = discoveredDomains.get(ontologyProperty);
            } else {
                domain = new HashSet<OntologyClass>();
                discoveredDomains.put(ontologyProperty, domain);
            }

            domain.add(new OntologyClass(entity));
        }
    }
    
    /*private Map<String, List<Pair<String, Cardinality>>> getCardinalitiesFromFile() {
        Map<String, List<Pair<String, Cardinality>>> map =
                new HashMap<String, List<Pair<String, Cardinality>>>();
        try {
            BufferedFileReader rd = new BufferedFileReader("cardinality");
            
            String line;
            while ((line = rd.readLine()) != null) {
                if (line == "") continue;
                String [] tokens = line.split(">\\s*,\\s*<");
                String propertyURI = tokens[0].trim().substring(1).trim();
                tokens = tokens[1].split(">\\s*:");
                String subjectURI = tokens[0].trim();
                int card = Integer.parseInt(tokens[1].trim());
                if (!map.containsKey(subjectURI))
                    map.put(subjectURI,
                            new ArrayList<Pair<String, Cardinality>>());
                map.get(subjectURI).add(new Pair(propertyURI, card));
                //map.put(new Pair(propertyURI, subjectURI),
                //        new Cardinality(card));
                //System.out.println(propertyURI + " - " + subjectURI + ": " +
                //        card);
            }
            
            rd.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Ontology.class.getName()).log(Level.SEVERE, null,
                ex);
        } catch (IOException ex) {
            Logger.getLogger(Ontology.class.getName()).log(Level.SEVERE, null,
                ex);
        }
        
        return map;
    }*/
    
    private Map<String, Cardinality> getCardinalitiesFromFile() {
        Map<String, Cardinality> map = new HashMap<String, Cardinality>();
        try {
            BufferedFileReader rd = new BufferedFileReader("cardinality");
            
            String line;
            while ((line = rd.readLine()) != null) {
                if (line == "") continue;
                String [] tokens = line.split(">\\s*:");
                String propertyURI = tokens[0].trim().substring(1).trim();
                int card = Integer.parseInt(tokens[1].trim());
                map.put(propertyURI, new Cardinality(card));
                //map.put(new Pair(propertyURI, subjectURI),
                //        new Cardinality(card));
                //System.out.println(propertyURI + " - " + subjectURI + ": " +
                //        card);
            }
            
            rd.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Ontology.class.getName()).log(Level.SEVERE, null,
                    ex);
        } catch (IOException ex) {
            Logger.getLogger(Ontology.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
        
        return map;
    }

    public Set<OntologyProperty> getProperties() {
        map = new HashMap();
        classesHaveValue = new HashSet();
        Pair<Map<Pair<OWLPropertyExpression, OWLEntity>, Cardinality>,
                Map<OWLPropertyExpression, List<OWLEntity>>> pair
                = getCardinalityInfo();
        /* Returns a pair containing the Map of property, entity pairs to a
         * cardinality and a map of a property to a list of entities
         */

        Map<Pair<OWLPropertyExpression, OWLEntity>, Cardinality> card
                = pair.getFirst();
        Map<OWLPropertyExpression, List<OWLEntity>> propEntMap
                = pair.getSecond();

        Set<OWLDataProperty> dataPropertySet
                = ontology.getDataPropertiesInSignature();
        Set<OWLObjectProperty> objectPropertySet
                = ontology.getObjectPropertiesInSignature();
        Set<OWLAnnotationProperty> annotationPropertySet
                = ontology.getAnnotationPropertiesInSignature();
        Set<OntologyProperty> set =
                new HashSet<OntologyProperty>(dataPropertySet.size() +
                        objectPropertySet.size());
        
        //Map<Pair<String, String>, Cardinality> cardMap =
        //        getCardinalitiesFromFile();
        Map<String, Cardinality> cardMap = getCardinalitiesFromFile();
        for (OWLDataProperty property : dataPropertySet) {
            OntologyProperty ontProp = new OntologyProperty(property);
            set.add(ontProp);
            
            //String propURI = property.getIRI().toString();
            if (cardMap.containsKey(property.getIRI().toString())) {
                addCardinality(ontProp,
                        cardMap.get((property.getIRI().toString())));
                continue;
            }
            if (!propEntMap.containsKey(property)) {
                addCardinality(ontProp, new Cardinality());
                
                continue;
            }

            for (OWLEntity ent : propEntMap.get(property)) {
                Cardinality c = card.get(new Pair(property, ent));
                addCardinality(ontProp, new OntologyClass(ent), c);
                
                /*String uri = ent.getIRI().toString();
                Pair<String, String> key = new Pair(propURI, uri);
                if (cardMap.containsKey(key)) {
                    c = cardMap.get(key);
                    addCardinality(ontProp, new OntologyClass(ent), c);
                }*/
            }
        }
        for (OWLObjectProperty property : objectPropertySet) {
            OntologyProperty ontProp = new OntologyProperty(property);
            set.add(ontProp);
            
            //String propURI = property.getIRI().toString();
            if (cardMap.containsKey(property.getIRI().toString())) {
                addCardinality(ontProp,
                        cardMap.get((property.getIRI().toString())));
                continue;
            }
            if (!propEntMap.containsKey(property)) {
                addCardinality(ontProp, new Cardinality());
                
                continue;
            }

            for (OWLEntity ent : propEntMap.get(property)) {
                Cardinality c = card.get(new Pair(property, ent));
                addCardinality(ontProp, new OntologyClass(ent), c);
                
                /*String uri = ent.getIRI().toString();
                Pair<String, String> key = new Pair(propURI, uri);
                if (cardMap.containsKey(key)) {
                    c = cardMap.get(key);
                    addCardinality(ontProp, new OntologyClass(ent), c);
                }*/
            }
        }
        for (OWLAnnotationProperty property : annotationPropertySet) {
            OntologyProperty ontProp = new OntologyProperty(property);
            OWLDataProperty dataProperty
                    = new OWLDataPropertyImpl(property.getIRI());
            set.add(ontProp);
            
            //String propURI = property.getIRI().toString();
            if (cardMap.containsKey(property.getIRI().toString())) {
                addCardinality(ontProp,
                        cardMap.get((property.getIRI().toString())));
                continue;
            }
            if (!propEntMap.containsKey(dataProperty)) {
                addCardinality(ontProp, new Cardinality());
                
                continue;
            }

            for (OWLEntity ent : propEntMap.get(dataProperty)) {
                Cardinality c = card.get(new Pair(dataProperty, ent));
                addCardinality(ontProp, new OntologyClass(ent), c);
                
                /*String uri = ent.getIRI().toString();
                Pair<String, String> key = new Pair(propURI, uri);
                if (cardMap.containsKey(key)) {
                    c = cardMap.get(key);
                    addCardinality(ontProp, new OntologyClass(ent), c);
                }*/
            }
        }

        discoverDomains(card, set);

        return set;
    }

    public Set<OntologyDatatype> getDatatypes() {
        Set<OWLDatatype> datatypes = ontology.getDatatypesInSignature();
        Set<OntologyDatatype> set = new HashSet<OntologyDatatype>(datatypes.
                size());
        for (OWLDatatype datatype : datatypes) {
            set.add(new OntologyDatatype(datatype));
        }

        return set;
    }

    private OWLPropertyExpression getOntologyProperty(OWLClassExpression expr) {
        if (expr instanceof OWLObjectMinCardinality) {
            OWLObjectMinCardinality d = (OWLObjectMinCardinality) expr;
            discard.add(d);
            return d.getProperty();
        } else if (expr instanceof OWLObjectMaxCardinality) {
            OWLObjectMaxCardinality d = (OWLObjectMaxCardinality) expr;
            discard.add(d);
            return d.getProperty();
        } else if (expr instanceof OWLObjectExactCardinality) {
            OWLObjectExactCardinality d = (OWLObjectExactCardinality) expr;
            discard.add(d);
            return d.getProperty();
        } else if (expr instanceof OWLDataMinCardinality) {
            OWLDataMinCardinality d = (OWLDataMinCardinality) expr;
            discard.add(d);
            return d.getProperty();
        } else if (expr instanceof OWLDataMaxCardinality) {
            OWLDataMaxCardinality d = (OWLDataMaxCardinality) expr;
            discard.add(d);
            return d.getProperty();
        } else if (expr instanceof OWLDataExactCardinality) {
            OWLDataExactCardinality d = (OWLDataExactCardinality) expr;
            discard.add(d);
            return d.getProperty();
        } /*else if (expr instanceof OWLObjectIntersectionOf) {
            System.out.println("INTERSECTION FOUND");
        }*/ else if (expr instanceof OWLObjectHasValue) {
            OWLObjectHasValue d = (OWLObjectHasValue) expr;
            discard.add(d);
            return d.getProperty();
        } else if (expr instanceof OWLRestriction) {
            OWLRestriction d = (OWLRestriction) expr;
            discard.add(d);
            return d.getProperty();
        }

        return null;
    }

    private Cardinality getObjectMinCardinality(OWLClassExpression expr,
            Cardinality c) {

        OWLObjectMinCardinality d = (OWLObjectMinCardinality) expr;

        if (c == null) {
            c = new Cardinality();
            c.setMin(d.getCardinality());
        } else {
            c.setMin(d.getCardinality());
        }
        return c;
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

    private Cardinality getObjectExactCardinality(OWLClassExpression expr,
            Cardinality c) {

        OWLObjectExactCardinality d = (OWLObjectExactCardinality) expr;

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

    private Pair<Map<Pair<OWLPropertyExpression, OWLEntity>, Cardinality>, Map<OWLPropertyExpression, List<OWLEntity>>>
            getCardinalityInfo() {
        Map<Pair<OWLPropertyExpression, OWLEntity>, Cardinality> card
                = new HashMap();
        Map<OWLPropertyExpression, List<OWLEntity>> propEntMap
                = new HashMap();
        if (ontology != null) {
            for (OWLEntity ent : ontology.getSignature()) {
                //   System.out.println(" getCardinalityInfo "+ ent.getIRI());
                if (ent.getEntityType() == EntityType.CLASS) {
                    OWLClass cls = (OWLClass) ent;

                    for (OWLClassExpression expr : cls.getSuperClasses(
                            ontology)) {
                        //      System.out.println("getCardinalityInfo "+expr.getClassExpressionType() +"\t"+ expr+ ":");
                        OWLPropertyExpression prop = getOntologyProperty(expr);
                        if (prop == null) {
                            continue;
                        }

                        if (!propEntMap.containsKey(prop)) {
                            List<OWLEntity> list = new ArrayList<OWLEntity>();
                            propEntMap.put(prop, list);
                        }

                        propEntMap.get(prop).add(ent);

                        Cardinality c = new Cardinality();

                        /*
                         * Cardinality c = card.get(prop) == null ?
                         * new Cardinality(-1, -1)
                         * : card.get(prop).getFirst();
                         */
                        calcCardinality(expr, c, card, prop, ent);
                        int x = 0;
                    }
                    
                    
                }
            }
        }
        return new Pair(card, propEntMap);
    }

    private void calcCardinality(OWLClassExpression expr, Cardinality c,
            Map<Pair<OWLPropertyExpression, OWLEntity>, Cardinality> card,
            OWLPropertyExpression prop, OWLEntity ent) {
        switch (expr.getClassExpressionType()) {
            case DATA_MIN_CARDINALITY:
                c = getDataMinCardinality(expr, c);
                break;
            case DATA_MAX_CARDINALITY:
                c = getDataMaxCardinality(expr, c);
                break;
            case DATA_EXACT_CARDINALITY:
                c = getDataExactCardinality(expr, c);
                break;
            case OBJECT_MIN_CARDINALITY:
                c = getObjectMinCardinality(expr, c);
                break;
            case OBJECT_MAX_CARDINALITY:
                c = getObjectMaxCardinality(expr, c);
                break;
            case OBJECT_EXACT_CARDINALITY:
                c = getObjectExactCardinality(expr, c);
                break;
            default:
                int x = 0; // do nothing
        }

        card.put(new Pair(prop, ent), c);
    }
    
    
    
    private Map<Pair<OntologyProperty, OntologyClass>, Cardinality> map;
    
    private void addCardinality(OntologyProperty property, OntologyClass clss,
            Cardinality cardinality) {
        Pair<OntologyProperty, OntologyClass> pair = new Pair(property, clss);
        map.put(pair, cardinality);
    }
    
    private void addCardinality(OntologyProperty property,
            Cardinality cardinality) {
        Pair<OntologyProperty, OntologyClass> pair = new Pair(property, null);
        map.put(pair, cardinality);
    }
    
    private void removeCardinality(OntologyProperty property,
            OntologyClass clss) {
        Pair<OntologyProperty, OntologyClass> pair = new Pair(property, clss);
        map.remove(pair);
    }
    
    public Cardinality getCardinality(OntologyProperty property,
            OntologyClass clss) {
        Pair<OntologyProperty, OntologyClass> nullPair = new Pair(property,
                null);
        if (map.containsKey(nullPair)) {
            return map.get(nullPair);
        }
        
        Pair<OntologyProperty, OntologyClass> pair = new Pair(property, clss);
        
        Cardinality card = map.containsKey(pair) ? map.get(pair)
                : map.get(new Pair(property, null));
        
        return card != null ? card : new Cardinality();
    }

    @Override
    public String toString() {
        return ontology.toString();
    }

}
