package eg.edu.alexu.ehr.ontology.api.wrapper.thing.object;

import eg.edu.alexu.ehr.ontology.api.wrapper.thing.OntologyThing;

public class OntologyObject  implements OntologyThing {
	protected boolean isEntity;
	
	public boolean isEntity() {
		return isEntity;
	}
	
	public boolean isValue() {
		return !isEntity;
	}

        @Override
        public String toString() {
            return null;
        }
}
