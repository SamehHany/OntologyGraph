package eg.edu.alexu.ehr.ontology.api.wrapper.object;

public class OntologyObject {
	protected boolean isEntity;
	
	public boolean isEntity() {
		return isEntity;
	}
	
	public boolean isValue() {
		return !isEntity;
	}
        
        public String getURIAsStr() {
            return null;
        }

        @Override
        public String toString() {
            return null;
        }
        
}
