/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eg.edu.alexu.ehr.util.csv;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sameh
 */
public class CSV {
    public static List<String> parseLine(String line) {
        List<String> list = new ArrayList<String>();
        int len = line.length();
        StringBuilder token = new StringBuilder(len);
        boolean withinQuotations = false;
        char nextChar = 0;
        char lastChar = 0;
        for (int i = 0; i < len; i++) {
            char ch = line.charAt(i);
            nextChar = i+1 < len ? line.charAt(i+1) : 0;
            lastChar = i-1 >= 0 ? line.charAt(i-1) : 0;
            //if (Character.isWhitespace(ch) && !withinQuotations)
                //continue;
            if (ch == ',' && !withinQuotations) {
                list.add(token.toString().trim());
                token = new StringBuilder(len);
            }
            else if (ch == '"' && lastChar != '\\') {
                withinQuotations = !withinQuotations;
                token.append(ch);
            }
            else if (ch == '\\' && nextChar == '"') {
                token.append(nextChar);
            }
            else if (ch == '\'') {
                token.append("\\'");
            }
            else if (ch == '\t') {
                token.append("\\t'");
            }
            else if (!(ch == '"' && lastChar == '\\')){
                token.append(ch);
            }
        }
        list.add(token.toString().trim());
        
        return list;
    }
}