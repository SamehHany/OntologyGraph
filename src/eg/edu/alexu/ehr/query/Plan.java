/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eg.edu.alexu.ehr.query;

import eg.edu.alexu.ehr.util.io.BufferedFileReader;
import eg.edu.alexu.ehr.util.json.JSONArray;
import eg.edu.alexu.ehr.util.json.JSONObject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sameh
 */
public class Plan {
    private Map<Object, Object> map;
    
    public Plan() {
        map = null;
    }
    
    public Plan(String path) {
        build(path);
    }
    
    public Plan(JSONObject obj) {
        build(obj);
    }

    public void build(String path) {
        //JSONParser parser = new JSONParser();
        BufferedFileReader reader = null;
        String jsonText = "";
        try {
            reader = new BufferedFileReader(path);
            jsonText = reader.read();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Plan.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (IOException ex) {
            Logger.getLogger(Plan.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        //System.out.println(jsonText);
        JSONObject obj = (new JSONObject(jsonText)).getJSONArray("Report").
                getJSONObject(0);
        build(obj);
    }
    
    public void build(JSONObject obj) {
        map = new HashMap();
        Plan.this.processJSONObjects(obj);
    }
    
    private int indLevel = 0;
    
    private void processJSONObjects(JSONObject obj) {
        Set<String> keys = obj.keySet();
        for (String key : keys) {
            Object o = obj.get(key);
            Object value = null;
            if (o instanceof JSONObject) {
                value = new Plan((JSONObject)o);
            } else if (o instanceof JSONArray) {
                value = JSONArray2List(o);
            }
            else
                value = o;
            
            map.put(key, value);
        }
    }
    
    private void processJSONObjects(Object obj) {
        Plan.this.processJSONObjects((JSONObject)obj);
    }
    
    private List<Object> JSONArray2List(JSONArray array) {
        List<Object> list = new ArrayList();
        for (Object obj : array) {
            if (obj instanceof JSONObject) {
                list.add(new Plan((JSONObject)obj));
            }
            else if (obj instanceof JSONArray) {
                list.add(JSONArray2List(obj));
            }
            else {
                list.add(obj);
            }
        }
        
        return list;
    }
    
    private List<Object> JSONArray2List(Object obj) {
        return JSONArray2List((JSONArray)obj);
    }
    
    private String toString(int indLevel) {
        return null;
    }
    
    @Override
    public String toString() {
        String ret = "";
        int indLevel = 0;
        
        for (Object key : map.keySet()) {
            
        }
        
        return ret;
    }
    
    @Override
    public int hashCode() {
        return map.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        Map<Object, Object> otherMap = (Map)obj;
        for (Object key : map.keySet()) {
            if (!otherMap.containsKey(key))
                return false;
            if (!map.get(key).equals(otherMap.get(key)))
                return false;
        }
        
        return true;
    }
}
