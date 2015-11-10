/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eg.edu.alexu.ehr.util;

/**
 *
 * @author sameh
 */
public class Pair<T1, T2> {
    private T1 first;
    private T2 second;
    
    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
    
    public T1 getFirst() {
        return first;
    }
    
    public T2 getSecond() {
        return second;
    }

    @Override
    public int hashCode() {
        int firstHash = first == null ? 0 : first.hashCode();
        int secondHash = second == null ? 0 : second.hashCode();
        return firstHash ^ secondHash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        Pair<T1, T2> pair = (Pair<T1, T2>)obj;

        boolean firstEquals = first == null ? pair.first == null
                : first.equals(pair.first);
        boolean secondEquals = second == null ? pair.second == null
                : second.equals(pair.second);

        return firstEquals && secondEquals;
    }
}
