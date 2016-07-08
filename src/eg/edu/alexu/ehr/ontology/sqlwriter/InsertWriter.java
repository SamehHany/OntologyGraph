/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eg.edu.alexu.ehr.ontology.sqlwriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author sameh
 */
public class InsertWriter extends SQLWriter {
    private String schema;
    private String table;
    private Set<String> columns;
    private List<String> values;
    
    public InsertWriter(String schema, String table, Collection<String> columns,
            Collection<String> values) {
        this.schema = schema;
        this.table = table;
        this.columns = new LinkedHashSet<>();
        this.columns.addAll(columns);
        this.values = new ArrayList<>();
        this.values.addAll(values);
    }
    
    public InsertWriter(String schema, String table, Collection<String> values) {
        this(schema, table, new ArrayList<>(0), values);
    }
    
    public InsertWriter(String table, Collection<String> columns,
            Collection<String> values) {
        this("", table, columns, values);
    }
    
    public InsertWriter(String table, Collection<String> values) {
        this(table, new ArrayList<>(0), values);
    }
    
    public InsertWriter(String schema, String table) {
        this(schema, table, new ArrayList<>(0), new ArrayList<>(0));
    }
    
    public InsertWriter(String table) {
        this("", table);
    }

    @Override
    public String write() {
        String query = "INSERT INTO " + ((schema == null || schema.equals("")) ?
                table : schema + "." + table);
        if (!columns.isEmpty()) {
            query += "(";
            boolean first = true;
            for (String column : columns) {
                if (!first) {
                    query += ", ";
                } else {
                    first = false;
                }
                query += column;
            }
            query += ")";
        }
        
        query += " VALUES";
        
        query += "(";
        boolean first = true;
        for (String value : values) {
                if (!first) {
                    query += ", ";
                } else {
                    first = false;
                }
                query += value;
            }
        query += ")";
        
        query += ";";
        
        return query;
    }
    
    public void addValue(String value) {
        values.add(value);
    }
    
    public void addValue(String value, String column) {
        values.add(value);
        columns.add(column);
    }
    
    public void addColumn(String column) {
        columns.add(column);
    }
    
    public void checkAndAddColumn(String column) {
        if (!columns.contains(column)) {
            columns.add(column);
        }
    }
}
