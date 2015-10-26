/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eg.edu.alexu.ehr.ontology.api.wrapper.thing;

/**
 *
 * @author Mina R. Waheeb
 */
public interface OntologyObject {
	public boolean isEntity();

	public boolean isValue();

        public boolean isProperty();
}
