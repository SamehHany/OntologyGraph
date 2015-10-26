package eg.edu.alexu.ehr.ontology.api.wrapper.thing.object;

import eg.edu.alexu.ehr.ontology.api.wrapper.thing.OntologyObject;

public class OntologyEntity  implements OntologyObject {
	protected boolean isEntity;
	
	public boolean isEntity() {
		return isEntity;
	}
	
	public boolean isValue() {
		return !isEntity;
	}

        public boolean isProperty() {
            return false;
        }

        @Override
        public String toString() {
            return null;
        }
}
