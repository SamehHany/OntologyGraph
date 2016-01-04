/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eg.edu.alexu.ehr.util.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author sameh
 */
public class Table {
    private String schema;
    private String tableName;
    private ArrayList<String> attrs;
    private DBDatatype[] datatypes;
    private List<Integer> primaryKeyIndexes;
    private int numOfAttrs;
    private List<String> primaryKeys;
    private List<DBDatatype> primaryKeysDatatypes;
    
    public Table(String schema, String tableName, ArrayList<String> attrs,
            DBDatatype[] datatypes, int primaryKeyIndex) {
        initialize(schema, tableName, attrs, datatypes, primaryKeyIndex);
    }
    
    public Table(String schema, String tableName, ArrayList<String> attrs,
            DBDatatype[] datatypes, List<Integer> primaryKeyIndexes) {
        initialize(schema, tableName, attrs, datatypes, primaryKeyIndexes);
    }
    
    public Table(String tableName, ArrayList<String> attrs,
            DBDatatype[] datatypes, int primaryKeyIndex) {
        List<Integer> keys = new ArrayList(1);
        keys.add(primaryKeyIndex);
        initialize("public", tableName, attrs, datatypes, keys);
    }
    
    public Table(String tableName, ArrayList<String> attrs,
            DBDatatype[] datatypes, List<Integer> primaryKeyIndexes) {
        initialize("public", tableName, attrs, datatypes, primaryKeyIndexes);
    }
    
    public Table(String schema, String tableName, ArrayList<String> attrs,
            DBDatatype[] datatypes) {
        initialize(schema, tableName, attrs, datatypes);
    }
    
    public Table(String tableName, ArrayList<String> attrs,
            DBDatatype[] datatypes) {
        initialize("public", tableName, attrs, datatypes);
    }
    
    private void initialize(String schema, String tableName,
            ArrayList<String> attrs, DBDatatype[] datatypes) {
        List<Integer> keys = new ArrayList(0);
        initialize(schema, tableName, attrs, datatypes, keys);
    }
    
    private void initialize(String schema, String tableName,
            ArrayList<String> attrs, DBDatatype[] datatypes,
            int primaryKeyIndex) {
        List<Integer> keys = new ArrayList(1);
        keys.add(primaryKeyIndex);
        initialize(schema, tableName, attrs, datatypes, keys);
    }
    
    private void initialize(String schema, String tableName,
            ArrayList<String> attrs, DBDatatype[] datatypes,
            List<Integer> primaryKeyIndexes) {
        this.schema = schema;
        this.tableName = tableName;
        numOfAttrs = attrs.size();
        this.attrs = new ArrayList<String>(numOfAttrs);
        for (int i = 0; i < numOfAttrs; i++)
            this.attrs.add(attrs.get(i));
        this.datatypes = new DBDatatype[numOfAttrs];
        for (int i = 0; i < numOfAttrs; i++)
            this.datatypes[i] = datatypes[i];
        this.primaryKeyIndexes
                = new ArrayList<Integer>(primaryKeyIndexes.size());
        for (int index: primaryKeyIndexes)
            this.primaryKeyIndexes.add(index);
        primaryKeys = null;
        primaryKeysDatatypes = null;
    }
    
    public String getSchema() {
        return schema;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public ArrayList<String> getAttrs() {
        return (ArrayList<String>)Collections.unmodifiableList(attrs);
    }
    
    public DBDatatype[] getDatatypes() {
        return datatypes.clone();
    }
    
    public String getAttrs(int index) {
        return attrs.get(index);
    }
    
    public DBDatatype getDatatypes(int index) {
        return datatypes[index];
    }
    
    public List<String> getPrimaryKey() {
        if (primaryKeys != null)
            return Collections.unmodifiableList(primaryKeys);
        
        primaryKeys = new ArrayList<String>(primaryKeyIndexes.size());
        for (int index: primaryKeyIndexes)
            primaryKeys.add(attrs.get(index));
        
        return primaryKeys;
    }
    
    public List<DBDatatype> getPrimaryKeyDatatype() {
        if (primaryKeysDatatypes != null)
            return Collections.unmodifiableList(primaryKeysDatatypes);
        
        primaryKeysDatatypes
                = new ArrayList<DBDatatype>(primaryKeyIndexes.size());
        for (int index: primaryKeyIndexes)
            primaryKeysDatatypes.add(datatypes[index]);
        
        return primaryKeysDatatypes;
    }
    
    @Override
    public int hashCode() {
        return 37*schema.hashCode() + tableName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Table other = (Table) obj;
        if (!Objects.equals(this.schema, other.schema)) {
            return false;
        }
        if (!Objects.equals(this.tableName, other.tableName)) {
            return false;
        }
        //if (!Arrays.deepEquals(this.attrs, other.attrs)) {
        if (!Objects.deepEquals(this.attrs, other.attrs)) {
            return false;
        }
        if (!Objects.deepEquals(this.datatypes, other.datatypes)) {
            return false;
        }
        if (!Objects.equals(this.primaryKeyIndexes, other.primaryKeyIndexes)) {
            return false;
        }
        return true;
    }
}
