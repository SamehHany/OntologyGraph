package eg.edu.alexu.ehr.ontology.api.wrapper.thing.object.values;

import eg.edu.alexu.ehr.ontology.api.wrapper.thing.object.entities.OntologyDatatype;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;

public class OntologyLiteral extends OntologyValue {
	private String literal;
	private OntologyDatatype datatype;
	public OntologyLiteral(OWLLiteral individual) {
		super((OWLObject)individual);
		isIndividual = false;
		literal = individual.getLiteral();
		datatype = new OntologyDatatype(individual.getDatatype());
	}
	
	public OntologyLiteral(String literal, String lang, OWLDatatype datatype) {
		super((OWLObject)(new OWLLiteralImpl(literal, lang, datatype)));
		isIndividual = false;
		this.datatype = new OntologyDatatype(datatype);
	}
	
	public OntologyLiteral(String literal, String lang, OntologyDatatype datatype) {
		super((OWLObject)(new OWLLiteralImpl(literal, lang, datatype.getOWLDatatype())));
		isIndividual = false;
		this.datatype = datatype;
	}

        public OntologyDatatype getDatatype() {
            return datatype;
        }

        @Override
        public String toString() {
            return "\"" + literal + "\"@" + obj.toString();
        }
}
