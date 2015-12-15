package eg.edu.alexu.ehr.ontology.api.wrapper.object.values;

import org.semanticweb.owlapi.model.OWLObject;

import eg.edu.alexu.ehr.ontology.api.wrapper.object.OntologyObject;

abstract public class OntologyValue extends OntologyObject {
	protected OWLObject obj;
	protected boolean isIndividual;
	
	public OntologyValue(OWLObject object) {
		obj = object;
		isEntity = false;
	}
	
	public boolean isIndividual() {
		return isIndividual;
	}
	
	public boolean isLiteral() {
		return !isIndividual;
	}
        
        @Override
        public String getURIAsStr() {
            return toString();
        }
	
	@Override
	public String toString() {
		return obj.toString();
	}
	
	@Override
	public int hashCode() {
		return obj.toString().hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return obj.toString().equals(other.toString());
	}
}
