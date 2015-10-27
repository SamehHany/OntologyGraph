package eg.edu.alexu.ehr.ontology.api.wrapper.thing.object.values;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import eg.edu.alexu.ehr.ontology.api.wrapper.Ontology;
import eg.edu.alexu.ehr.ontology.api.wrapper.thing.OntologyProperty;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

public class OntologyIndividual extends OntologyValue {
	
	public OntologyIndividual(OWLIndividual individual) {
		super((OWLObject)individual);
		isIndividual = true;
	}
	
	public OntologyIndividual(String uri) {
		super((OWLObject)(new OWLNamedIndividualImpl(IRI.create(uri))));
		isIndividual = true;
	}
	
	public Map<OntologyProperty, Set<OntologyValue>> getPropertyValues(Ontology ontology) {
		OWLOntology owlontology = ontology.getOWLOntology();
		Map<OWLDataPropertyExpression, Set<OWLLiteral>> dataMap =
				((OWLIndividual)obj).getDataPropertyValues(owlontology);
		Map<OWLObjectPropertyExpression, Set<OWLIndividual>> objectMap =
				((OWLIndividual)obj).getObjectPropertyValues(owlontology);
		Map<OntologyProperty, Set<OntologyValue>> map =
				new HashMap<OntologyProperty, Set<OntologyValue>>(dataMap.size() + objectMap.size());
		
		Set<OWLDataPropertyExpression> dataKeys = dataMap.keySet();
		for (OWLDataPropertyExpression dataProperty : dataKeys) {
			Set<OWLLiteral> literals = dataMap.get(dataProperty);
			OntologyProperty property = new OntologyProperty(dataProperty);
			Set<OntologyValue> set = new HashSet<OntologyValue>(literals.size());
			for (OWLLiteral literal : literals)
				set.add(new OntologyLiteral(literal));
			map.put(property, set);
		}
		
		Set<OWLObjectPropertyExpression> objectKeys = objectMap.keySet();
		for (OWLObjectPropertyExpression objectProperty : objectKeys) {
			Set<OWLIndividual> individuals = objectMap.get(objectProperty);
			OntologyProperty property = new OntologyProperty(objectProperty);
			Set<OntologyValue> set = new HashSet<OntologyValue>(individuals.size());
			for (OWLIndividual individual : individuals)
				set.add(new OntologyIndividual(individual));
			map.put(property, set);
		}
		
		return map;
	}
	
	public IRI getIRI() {
		return IRI.create(((OWLIndividual)obj).toString());
	}
	
	public URI getURIAsURIDatatype() {
		URI uri = null;
		try {
			uri = new URI(((OWLIndividual)obj).toString());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uri;
	}

        @Override
	public String getURI() {
		return ((OWLIndividual)obj).toString();
	}
}
