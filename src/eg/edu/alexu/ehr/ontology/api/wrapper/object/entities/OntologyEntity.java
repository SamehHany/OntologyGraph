package eg.edu.alexu.ehr.ontology.api.wrapper.object.entities;

import java.net.URI;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.OntologyObject;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;

abstract public class OntologyEntity extends OntologyObject { // Class or datatype
	protected OWLEntity entity;
	protected boolean isClass;
	public OntologyEntity(OWLEntity object) {
		entity = object;
		isEntity = true;
	}
	
	public boolean isClass() {
		return isClass;
	}
	
	public boolean isDatatype() {
		return !isClass;
	}
	
	public IRI getIRI() {
		return ((OWLEntity)entity).getIRI();
	}
	
	public URI getURI() {
		return ((OWLEntity)entity).getIRI().toURI();
	}
	
	public String getURIAsStr() {
		return ((OWLEntity)entity).getIRI().toString();
	}
	
	@Override
	public int hashCode() {
		return entity.getIRI().toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return entity.getIRI().toString().equals(((OntologyEntity)obj).entity.getIRI().toString());
	}
	
	@Override
	public String toString() {
		return entity.getIRI().toString();
	}
        public  void getOWLClassExpression(){

           for( OWLClassExpression o: entity.getNestedClassExpressions()){
               if (o instanceof  OWLDataMinCardinality)
                   System.out.print("data min"+o);
                if (o instanceof  OWLDataMaxCardinality)
                   System.out.print("data max"+o);
                 if (o instanceof  OWLObjectExactCardinality)
                   System.out.print("object exact"+o);
                 if(o instanceof OWLCardinalityRestriction)
                          System.out.print("card exact"+o);
           }
int i=0;
        }

}
