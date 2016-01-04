/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eg.edu.alexu.ehr.ontology.api.wrapper;

/**
 *
 * @author Mina R. Waheeb
 */
public class Cardinality {
    private int min;
    private int max;

    public Cardinality() {
        min = 1;
        max = Integer.MAX_VALUE;
        //max = 1;
    }

    public Cardinality(int min, int max) {
        this.min = min;
        this.max = max;
    }
    
    public Cardinality(int card) {
        this.min = card;
        this.max = card;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public String toString() {
        return "[" + min + "," + max + "]";
    }

    @Override
    public int hashCode() {
        return min*max;
    }

    @Override
    public boolean equals(Object obj) {
        Cardinality card = (Cardinality) obj;
        return min == card.min && max == card.max;
    }
}
