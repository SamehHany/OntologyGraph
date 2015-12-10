package eg.edu.alexu.ehr.ontology.graph;

import eg.edu.alexu.ehr.ontology.Main;
import eg.edu.alexu.ehr.ontology.api.wrapper.Cardinality;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import eg.edu.alexu.ehr.ontology.api.wrapper.Ontology;
import eg.edu.alexu.ehr.ontology.api.wrapper.OntologyProperty;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.OntologyObject;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyClass;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyDatatype;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.entities.OntologyEntity;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.values.OntologyIndividual;
import eg.edu.alexu.ehr.ontology.api.wrapper.object.values.OntologyValue;
import eg.edu.alexu.ehr.util.Pair;
import eg.edu.alexu.ehr.util.csv.CSV;
import eg.edu.alexu.ehr.util.db.Table;
import eg.edu.alexu.ehr.util.io.BufferedFileReader;
import eg.edu.alexu.ehr.util.io.BufferedFileWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.semanticweb.owlapi.model.OWLAnnotation;

public class OntologyGraph {

    private Ontology ontology;

    private Set<OntologyGraphNode> classes;

    private Set<OntologyGraphNode> datatypes;

    private Set<OntologyGraphNode> individuals;

    private Set<OntologyGraphNode> literals;

    private Map<OntologyGraphEdge, OntologyGraphEdge> individualToClassPropertyMap;

    private Map<OntologyEntity, OntologyGraphNode> entityMap;

    private Map<OntologyValue, OntologyGraphNode> valueMap;

    private Map<OntologyGraphNode, Partition> partitionMap;

    private Map<String, OntologyGraphObject> uriToNodeMap;

    private Map<OntologyProperty, Set<OntologyGraphEdge>> propertyToEdgeMap;

    private Set<OntologyGraphEdge> edges;

    private int noOfedges;

    private Map<String, List<String>> tables;
    private Map<OntologyGraphNode, String> nodeTableMap;
    private Map<OntologyGraphEdge, Pair<String, String>> edgeTableAttrMap;
    private Map<OntologyGraphEdge, Pair<String, String>> propertyTableMap;
    private Map<String, String> labelTableMap;
    
    private Set<Table> tablesSet;

    private static final String propertyQueriesPath = "property-queries/";
    private static final String singlePropertyQueriesPath
            = "single-property-queries/";
    private static final String propertyDataPath = "property-data/";
    private static final String queryFileExtension = ".q";
    
    private static final String schemaName = "ontology";
    private static final String propertySchema = "PropertyData";
    
    private static final String xsdStringType = "^^xsd:string";
    private static final String owlStringType = xsdStringType;

    private void initializeTableMaps() {
        tables = new HashMap();
        nodeTableMap = new HashMap();
        edgeTableAttrMap = new HashMap();
        propertyTableMap = new HashMap();
        labelTableMap = new HashMap();
    }

    private void addAttrToTable(String schema, String table, String attr) {
        String tableFullName = schema + "." + table;
        List<String> list = tables.get(tableFullName);
        if (list == null) {
            list = new ArrayList();
            tables.put(tableFullName, list);
        }

        list.add(attr);
    }

    private void addTable(OntologyGraphNode node, String schema, String table) {
        nodeTableMap.put(node, schema + "." + table);
    }

    private void addTable(OntologyGraphEdge edge, String schema, String table,
            String attr) {
        edgeTableAttrMap.put(edge, new Pair(schema + "." + table, attr));
    }

    private void addPropertyToSchema(OntologyGraphEdge edge, String schema,
            String table, String attr) {
        String tableFullName = schema + "." + table;
        Pair<String, String> pair = new Pair(tableFullName, attr);
        propertyTableMap.put(edge, pair);
    }

    private void addAttrToSchema(String schema, String table, String attr,
            OntologyGraphEdge edge) {
        addAttrToTable(schema, table, attr);
        addPropertyToSchema(edge, schema, table, attr);
    }

    public void insertValues(String inpath, String outpath) {
        for (OWLAnnotation a : ontology.getOWLOntology().getAnnotations()) {
            System.out.println(a);
        }
    }

    /*public String getSparql(String uri, String obdaPath) {
     String query = "";
     Map<String, String> prefixes = getPrefixes(obdaPath);
     Set<String> propertiesInOBDA = getPropertiesInOBDA(obdaPath);

     for (String key : prefixes.keySet()) {
     query += "PREFIX " + prefixes.get(key) + ": <" + key + ">\n";
     }

     query += "select";

     OntologyGraphNode node = (OntologyGraphNode) uriToNodeMap.get(uri);
     Set<OntologyGraphEdge> properties = node.getEdges(EdgeType.PROPERTY);
     for (OntologyGraphEdge property : properties) {
     String label = property.getLabel();
     String propertyURI = property.getProperty().getURIAsStr();
     String attr = getAttrStr(prefixes, propertyURI, label);
     if (propertiesInOBDA.contains(attr)) {
     query += " ?" + label;
     }
     }

     query += "\nwhere {\n";
     String clss = getAttrStr(prefixes, uri, node.getLabel());

     query += "  ?x a " + clss;

     for (OntologyGraphEdge property : properties) {
     String label = property.getLabel();
     String propertyURI = property.getProperty().getURIAsStr();
     String attr = getAttrStr(prefixes, propertyURI, label);

     if (propertiesInOBDA.contains(attr)) {
     query += ";\n       " + attr + " ?" + label;
     }
     }

     query += ".\n}";

     return query;
     }*/
    public static void ontop(String obdaPath, String owlPath, String queryPath,
            String outPath)
            throws IOException {
        Runtime run = Runtime.getRuntime();

        String command = "./ontop/ontop query -m " + obdaPath + " -t " + owlPath
                + " -q " + queryPath + " -o " + outPath;
        Process p = run.exec(command);
        try {
            p.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String nameFromLabel(String label) {
        return label.replaceAll("[-.]", "");
    }

    //private void extractSinglePropertyQueries() {

    //}
    
    //private String detectDatatype
    
    private String getReferenceFromToken(String token) {
        //System.out.println(token);
        return token.substring(1, token.length()-1);
    }
    
    private String getStringFromToken(String token) {
        //System.out.println(token);
        return "\'" + token.substring(1, token.lastIndexOf('\"')) + "\'";
    }
    
    public void populateSchemaWithData() {
        for (OntologyGraphNode clss: classes) {
            
        }
    }
    
    public void insertIntoPropertyTables(String outpath) {
        BufferedFileWriter bw = null;
        try {
            bw = new BufferedFileWriter(outpath);
            bw.writeln("CREATE SCHEMA IF NOT EXISTS " + propertySchema + ";");
            bw.writeln();
        } catch (IOException ex) {
            Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        File []files = (new File(propertyDataPath)).listFiles();
        
        Set<String> createdTables = new HashSet<String>();
        
        for (File file: files) {
            BufferedFileReader br = null;
            try {
                br = new BufferedFileReader(file);
                br.readLine();
                String line = br.readLine();
                while (line != null && line.equals("")) { }
                List<String> csvLine = CSV.parseLine(line);
                boolean firstIsReference;
                boolean secondIsReference;
                String token = csvLine.get(0);
                if (token.charAt(0) == '<' &&
                        token.charAt(token.length()-1) == '>') {
                    firstIsReference = true;
                } else {
                    firstIsReference = false;
                }
                token = csvLine.get(1);
                if (token.charAt(0) == '<' &&
                        token.charAt(token.length()-1) == '>') {
                    secondIsReference = true;
                } else {
                    secondIsReference = false;
                }
                
                br.close();
                br = new BufferedFileReader(file);
                
                String path = file.getPath();
                String classLabel = path.split("/")[1].split("_", 2)[0];
                String propertyLabel = path.split("/")[1].split("_", 2)[1];
                propertyLabel = propertyLabel.substring(0,
                        propertyLabel.lastIndexOf("."));
                String tableName = propertySchema + "." + classLabel +
                        firstToUpperCase(propertyLabel);
                line = br.readLine();
                if (line != null && !createdTables.contains(tableName)) {
                    List<String> labels = CSV.parseLine(line);
                    bw.writeln("CREATE TABLE " + tableName + "(");
                    if (firstIsReference) {
                        bw.writeln("\t" + labels.get(0) +
                                " INTEGER,");
                    } else {
                        bw.writeln("\t" + labels.get(0) +
                                " VARCHAR(255),");
                    }
                    if (secondIsReference) {
                        bw.writeln("\t" + labels.get(1) +
                                " INTEGER");
                    } else {
                        bw.writeln("\t" + labels.get(1) +
                                " VARCHAR(255)");
                    }
                    /*boolean first = true;
                    for (String label: labels) {
                        if (!first)
                            bw.writeln(",");
                        first = false;
                        bw.write("\t" + label + " VARCHAR(255)");
                    }*/
                    bw.writeln(");");
                    bw.writeln();
                    createdTables.add(tableName);
                }
                while ((line = br.readLine()) != null) {
                    List<String> list = CSV.parseLine(line);
                    bw.writeln("INSERT INTO " + tableName + " VALUES");
                    bw.write("\t(");
                    if (firstIsReference) {
                        bw.write(getReferenceFromToken(list.get(0)));
                    } else {
                        bw.write(getStringFromToken(list.get(0)));
                    }
                    bw.write(", ");
                    if (secondIsReference) {
                        bw.write(getReferenceFromToken(list.get(1)));
                    } else {
                        bw.write(getStringFromToken(list.get(1)));
                    }
                    /*boolean first = true;
                    for (String value: list) {
                        if (!first)
                            bw.write(", ");
                        first = false;
                        bw.write("'" + value + "'");
                    }*/
                    bw.writeln(");");
                }
                br.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<Pair<String, OntologyGraphEdge>>
            getAttrFromDB(OntologyGraphNode node,
                    Set<OntologyGraphEdge> properties, Set<String> propertiesInOBDA,
                    Map<String, String> prefixes) {
        Map<String, OntologyGraphEdge> labelPropMap
                = new HashMap();
        for (OntologyGraphEdge property : properties) {
            String label = property.getLabel();
            String attr = getAttrStr(prefixes,
                    property.getProperty().getURIAsStr(), label);
            if (!propertiesInOBDA.contains(attr)) {
                continue;
            }
            labelPropMap.put(label, property);
        }

        String clssName = nameFromLabel(node.getLabel());

        BufferedFileReader br;
        BufferedFileWriter bw = null;

        List<Pair<String, OntologyGraphEdge>> propertyFiles
                = new ArrayList();

        try {
            br = new BufferedFileReader(propertyQueriesPath +
                    clssName + queryFileExtension);
            String line = "";
            String propertyName;
            String query = "";
            boolean skip = false;
            while ((line = br.readLine()) != null) {
                //line = line.trim();
                int len = line.length();
                if (len < 1) {
                    continue;
                }
                if (line.charAt(0) == '[' && line.charAt(len - 1) == ']') {
                    if (bw != null) {
                        bw.close();
                    }
                    query = "";
                    String[] tokens = line.split("\"");
                    propertyName = tokens[tokens.length - 2];
                    String fileName = singlePropertyQueriesPath
                            + clssName + "_" + propertyName + queryFileExtension;
                    OntologyGraphEdge property = labelPropMap.get(propertyName);
                    if (property == null) {
                        skip = true;
                        continue;
                    }
                    skip = false;
                    propertyFiles.add(new Pair(fileName, property));
                    bw = new BufferedFileWriter(fileName);
                } else {
                    if (skip) {
                        continue;
                    }
                    bw.writeln(line);
                }
            }
            bw.close();
            br.close();

        } catch (IOException ex) {
            //Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE,
            //null, ex);
        }

        return propertyFiles;
    }

    /*public String getSparqlQueries(String uri, String obdaPath) {
     OntologyGraphNode node = (OntologyGraphNode) uriToNodeMap.get(uri);
     return getSparqlQueries(node, obdaPath);
     }*/
    public void getAllData(String obdaPath, String owlPath) {
        int index = 0;
        for (OntologyGraphNode clss : classes) {
            System.out.println((index++) + ": " + clss);
            getData(clss.getURIAsStr(), obdaPath, owlPath);
        }
    }

    public void getData(String uri, String obdaPath,
            String owlPath) {
        OntologyGraphNode node = (OntologyGraphNode) uriToNodeMap.get(uri);
        String queries = "";
        Map<String, String> prefixes = getPrefixes(obdaPath);
        Set<String> propertiesInOBDA = getPropertiesInOBDA(obdaPath);

        Set<OntologyGraphEdge> properties = node.getEdges(EdgeType.PROPERTY);
        generateSparqlQueries(node, properties, prefixes, propertiesInOBDA);

        List<Pair<String, OntologyGraphEdge>> propertyFiles
                = getAttrFromDB(node, properties, propertiesInOBDA, prefixes);

        /*for (Pair<String, OntologyGraphEdge> pair : propertyFiles) {
            String filePath = pair.getFirst();
            OntologyGraphEdge property = pair.getSecond();
            try {
                ontop(obdaPath, owlPath, filePath, propertyDataPath
                        + property.getPreviousNode().getLabel() + "_"
                        + property.getLabel());
            } catch (IOException ex) {
                Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/

    }

    private boolean generateSparqlQueries(OntologyGraphNode node,
            Set<OntologyGraphEdge> properties, Map<String, String> prefixes,
            Set<String> propertiesInOBDA) {
        String queries = "";
        for (OntologyGraphEdge property : properties) {
            String label = property.getLabel();
            String propertyURI = property.getProperty().getURIAsStr();
            String attr = getAttrStr(prefixes, propertyURI, label);
            if (!propertiesInOBDA.contains(attr)) {
                continue;
            }
            queries += "[QueryItem=\"" + label + "\"]\n";
            for (String key : prefixes.keySet()) {
                queries += "PREFIX " + prefixes.get(key) + ": <" + key + ">\n";
            }

            String clss = getAttrStr(prefixes, node.getURIAsStr(),
                    node.getLabel());
            queries += "select ?x ?" + label + "\n"
                    + "where {\n"
                    + "  ?x a " + clss + ";\n"
                    + "       " + attr + " ?" + label + ".\n"
                    + "}\n\n";

        }

        try {
            if (!queries.equals("")) {
                BufferedFileWriter bw
                        = new BufferedFileWriter(propertyQueriesPath
                                + nameFromLabel(node.getLabel())
                                + queryFileExtension);
                bw.write(queries);
                bw.close();
            }
            return true;
        } catch (IOException ex) {
            Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE,
                    null, ex);
            return false;
        }
    }

    public boolean generateSparqlQueries(OntologyGraphNode node,
            String obdaPath, String owlPath) {
        Map<String, String> prefixes = getPrefixes(obdaPath);
        Set<String> propertiesInOBDA = getPropertiesInOBDA(obdaPath);

        Set<OntologyGraphEdge> properties = node.getEdges(EdgeType.PROPERTY);
        return generateSparqlQueries(node, properties, prefixes,
                propertiesInOBDA);
    }

    public boolean generateSparqlQueries(String obdaPath, String owlPath) {
        Map<String, String> prefixes = getPrefixes(obdaPath);
        Set<String> propertiesInOBDA = getPropertiesInOBDA(obdaPath);

        boolean succeeded = true;
        for (OntologyGraphNode clss : classes) {
            Set<OntologyGraphEdge> properties = clss.getEdges(EdgeType.PROPERTY);
            boolean success = generateSparqlQueries(clss, properties, prefixes,
                    propertiesInOBDA);
            if (!success) {
                succeeded = false;
            }
        }

        return succeeded;
    }

    /*public void getSparqlQueries(OntologyGraphNode node, String obdaPath,
     String outPath) {
     String sparql = getSparqlQueries(node, obdaPath);
     try {
     BufferedFileWriter bw = new BufferedFileWriter(outPath);
     bw.write(sparql);
     bw.close();
     } catch (IOException ex) {
     Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE,
     null, ex);
     }
     }
    
     public void getSparqlQueries(String uri, String obdaPath, String outPath) {
     String sparql = getSparqlQueries(uri, obdaPath);
     try {
     BufferedFileWriter bw = new BufferedFileWriter(outPath);
     bw.write(sparql);
     bw.close();
     } catch (IOException ex) {
     Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE,
     null, ex);
     }
     }*/
    private String getAttrStr(Map<String, String> prefixes,
            String uri, String label) {
        String[] pair = uri.split("#");
        String attr = null;
        if (pair.length == 2) {
            attr = prefixes.get(pair[0] + "#") + ":" + label;
        } else {
            int index = uri.lastIndexOf("/");
            String namespace = uri.substring(0, index) + "/";
            String attrName = uri.substring(index + 1);

            attr = prefixes.get(namespace) + ":" + attrName;
        }

        return attr;
    }

    private Map<String, String> getPrefixes(String path) {
        BufferedFileReader br;
        Map<String, String> map = new HashMap<String, String>();
        try {
            br = new BufferedFileReader(path);
            String line = "";
            while (!line.equals("[PrefixDeclaration]")) {
                line = br.readLine().trim();
            }
            while (!(line = br.readLine().trim()).equals("")) {
                String[] pair = line.split(":", 2);
                map.put(pair[1].trim(), pair[0].trim());
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        return map;
    }

    private Set<String> getPropertiesInOBDA(String path) {
        BufferedFileReader br;
        Set<String> set = new HashSet();

        try {
            br = new BufferedFileReader(path);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }

                String[] tokens = line.split("[ \t][ \t]*");
                if (tokens.length < 4) {
                    continue;
                }

                if (!tokens[0].equals("target")) {
                    continue;
                }

                if (!tokens[2].equals("a")) {
                    set.add(tokens[2]);
                } else {
                    set.add(tokens[3]);
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE, null, ex);
        }

        return set;
    }

    private void writeSuperClassReferences(BufferedFileWriter w,
            OntologyGraphNode clss, List<String> foreignKeys,
            String schemaName, String tableName) {
        Set<OntologyGraphNode> superClasses
                = clss.getNextNodes(EdgeType.SUPERCLASS);
        Iterator<OntologyGraphNode> it = superClasses.iterator();

        try {
            if (it.hasNext()) {
                w.write(",");
            }

            while (it.hasNext()) {
                OntologyGraphNode sprclss = it.next();
                String sprclssName = sprclss.getLabel().replaceAll("[-.]", "");
                String attrName = firstToLowerCase(sprclssName + "_id");
                w.writeln();
                w.write("\t" + attrName + " INTEGER");
                if (it.hasNext()) {
                    w.write(",");
                }

                w.write(" -- REFERENCES " + sprclssName
                        + "(id)");

                foreignKeys.add(getForeignKey(schemaName, tableName,
                        attrName, sprclssName, "id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean skipProperty(OntologyProperty property, OntologyClass subject,
            OntologyObject object) {
        if (!(object instanceof OntologyClass)) {
            return false;
        }
        OntologyClass obj = (OntologyClass) object;
        Set<OntologyProperty> inverses = property.getInverses(ontology);
        for (OntologyProperty inverse : inverses) {
            if (property.getCardinality(ontology, subject).getMax()
                    < inverse.getCardinality(ontology, obj).getMax()) {
                return true;
            }
        }

        return false;
    }

    public void toTable(String fileName) {
        initializeTableMaps();
        Set<Pair<OntologyProperty, OntologyObject>> usedProperties
                = new HashSet();
        try {
            BufferedFileWriter w = new BufferedFileWriter(fileName);
            //w.writeln("CREATE DATABASE Ontology");
            
            w.writeln("CREATE SCHEMA IF NOT EXISTS " + schemaName + ";");
            List<String> foreignKeys = new ArrayList();
            for (OntologyGraphNode clss : classes) {
                if (clss.hasValue()) {
                    continue;
                }
                Set<OntologyGraphEdge> edges = clss.getEdges(EdgeType.PROPERTY);
                String tableName = clss.getLabel().replaceAll("[-.]", "");
                String tableFullName = schemaName + "." + tableName;
                if (tableName.equals("")) {
                    continue;
                }
                w.writeln();
                w.writeln("CREATE TABLE " + tableFullName + "(");
                addTable(clss, schemaName, tableName);
                w.write("\tid SERIAL PRIMARY KEY");

                writeSuperClassReferences(w, clss, foreignKeys, schemaName,
                        tableName);

                List<OntologyGraphEdge> binary = new ArrayList();
                Iterator<OntologyGraphEdge> it = edges.iterator();
                //if (it.hasNext()) {
                //w.write(",");
                //}
                while (it.hasNext()) {
                    OntologyGraphEdge edge = it.next();
                    OntologyGraphNode object = edge.getNextNode();
                    if (skipProperty(edge.getProperty(), clss.getAsClass(),
                            object.getObject())) {
                        continue;
                    }
                    String foreignTableName
                            = object.getLabel().replaceAll("[-.]", "");

                    OntologyProperty property = edge.getProperty();
                    OntologyClass domain = clss.getAsClass();
                    Cardinality card
                            = property.getCardinality(ontology, domain);
                    if (foreignTableName.equals("")
                            || edge.getLabel().equals("")) {
                        continue;
                    }

                    // Skip if inverse of property was assigned to the current
                    // table
                    Set<OntologyProperty> inverses
                            = property.getInverses(ontology);
                    boolean doContinue = false;
                    for (OntologyProperty inverse : inverses) {
                        Pair<OntologyProperty, OntologyObject> pair
                                = new Pair(inverse, clss.getObject());
                        if (usedProperties.contains(pair)) {
                            doContinue = true;
                            break;
                        }
                    }
                    if (doContinue) {
                        continue;
                    }

                    Pair<OntologyProperty, OntologyObject> pair
                            = new Pair(edge.getProperty(), object.getObject());
                    usedProperties.add(pair);

                    if (card.getMin() > 1 || card.getMax() > 1) {
                        binary.add(edge);
                        continue;
                    }

                    w.write(",");
                    if (object.isClass()) {
                        w.writeln();
                        w.write("\t" + edge.getLabel() + " INTEGER");
                        if (it.hasNext()) {
                            w.write(",");
                        }

                        w.write(" -- REFERENCES " + foreignTableName
                                + "(id)");
                        foreignKeys.add(getForeignKey(schemaName, tableName,
                                edge.getLabel(), foreignTableName, "id"));
                        addAttrToSchema(schemaName, tableName,
                                edge.getLabel(), edge);
                        addTable(edge, schemaName, tableName, edge.getLabel());

                    } else if (object.isDataType()) {
                        w.writeln();
                        w.write("\t" + edge.getLabel() + " "
                                + edge.getNextNode().getSQLDatatype());
                        if (it.hasNext()) {
                            w.write(",");
                        }
                        addAttrToSchema(schemaName, tableName,
                                edge.getLabel(), edge);
                        addTable(edge, schemaName, tableName, edge.getLabel());
                    }
                }
                w.writeln();
                w.writeln(");");

                for (OntologyGraphEdge edge : binary) {
                    OntologyGraphNode object = edge.getNextNode();
                    String subjectName = clss.getLabel().replaceAll("[-.]", "");
                    String objectName
                            = object.getLabel().replaceAll("[-.]", "");
                    //objectName = objectName;
                    String propertyName = firstToUpperCase(edge.getLabel());
                    String binaryTableName = subjectName + propertyName;
                    String binaryTableFullName = schemaName + "."
                            + binaryTableName;
                    w.writeln();
                    w.writeln("CREATE TABLE " + binaryTableFullName + "(");
                    addTable(edge, binaryTableName, tableName, objectName);

                    if (object.isClass()) {
                        w.writeln("\t" + subjectName
                                + " INTEGER, -- REFERENCES "
                                + subjectName + "(id)" + ",");
                        w.writeln("\t" + objectName
                                + " INTEGER -- REFERENCES "
                                + objectName + "(id)");
                        w.writeln(");");

                        OntologyGraphEdge inverse = edge.getInverseEdge();
                        if (inverse != null) {
                            String inversePropertyName
                                    = firstToUpperCase(inverse.getLabel());
                            addAttrToSchema(schemaName, binaryTableName,
                                    inversePropertyName, inverse);
                        }
                        addAttrToSchema(schemaName, binaryTableName,
                                propertyName, edge);

                        foreignKeys.add(getForeignKey(schemaName,
                                binaryTableName, objectName,
                                objectName, "id"));
                    } else if (object.isDataType()) {
                        //w.writeln("\t" + subjectName + " "
                        //        + clss.getSQLDatatype() + ",");
                        w.writeln("\t" + subjectName
                                + " INTEGER, -- REFERENCES "
                                + subjectName + "(id)" + ",");
                        w.writeln("\t" + objectName + " "
                                + object.getSQLDatatype());
                        w.writeln(");");
                    }
                    
                    foreignKeys.add(getForeignKey(schemaName,
                                binaryTableName, subjectName,
                                subjectName, "id"));

                    w.writeln();
                    w.writeln("ALTER TABLE ONLY " + binaryTableFullName);
                    w.writeln("\tADD CONSTRAINT " + subjectName.toLowerCase()
                            + "_" + objectName.toLowerCase() + "_pkey"
                            + " PRIMARY KEY (" + subjectName + ", "
                            + objectName + ");");
                }
            }

            w.writeln();
            w.writeln();

            for (String constraintStmt : foreignKeys) {
                w.writeln(constraintStmt);
            }

            w.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String firstToUpperCase(String str) {
        int len = str.length();
        if (len == 0) {
            return str;
        } else if (len == 1) {
            return str.substring(0, 1).toUpperCase();
        }
        return str.substring(0, 1).toUpperCase()
                + str.substring(1);
    }

    private String firstToLowerCase(String str) {
        int len = str.length();
        if (len == 0) {
            return str;
        } else if (len == 1) {
            return str.substring(0, 1).toLowerCase();
        }
        return str.substring(0, 1).toLowerCase()
                + str.substring(1);
    }

    private String getForeignKey(String schemaName, String table, String attr,
            String foreignTable, String foreignAttr) {
        return "ALTER TABLE ONLY " + schemaName + "." + table + "\n    "
                + "ADD CONSTRAINT " + table.toLowerCase() + "_"
                + attr.toLowerCase() + "_reference" +
                " FOREIGN KEY (" + attr + ") REFERENCES " + schemaName +
                "." + foreignTable + "(" + foreignAttr + ");";
    }

    public OntologyGraph(Ontology ontology) {
        this.ontology = ontology;
        build(ontology);
    }

    private void addEdge(OntologyGraphEdge edge) {
        edges.add(edge);
        if (!edge.isProperty()) {
            return;
        }
        OntologyProperty property = edge.getProperty();
        if (propertyToEdgeMap.containsKey(property)) {
            propertyToEdgeMap.get(property).add(edge);
        } else {
            Set<OntologyGraphEdge> set = new HashSet<OntologyGraphEdge>();
            set.add(edge);
            propertyToEdgeMap.put(edge.getProperty(), set);
        }
    }

    private OntologyGraphNode getEntityNode(OntologyEntity entity) {
        OntologyGraphNode node = entityMap.get(entity);
        if (node == null) {
            node = new OntologyGraphNode(entity);
            entityMap.put(entity, node);
        }

        return node;
    }

    private void addClassToNodes(OntologyClass clss) {
        String uri = clss.toString();
        String label = getLabelFromUri(uri);
        OntologyGraphNode node = new OntologyGraphNode(clss, label);
        classes.add(node);
        entityMap.put(clss, node);
        uriToNodeMap.put(uri, node);

        Set<OntologyIndividual> individualsLocalSet = clss.
                getIndividuals(ontology);
        for (OntologyIndividual individual : individualsLocalSet) {
            node = new OntologyGraphNode(individual);
            if (!individuals.contains(node)) {
                individuals.add(node);
                valueMap.put(individual, node);
            }
        }
        
        /*Set<OntologyClass> superClasses = clss.getSubClasses(ontology);
        for (OntologyClass superClass : superClasses) {
            addClassToNodes(superClass);
        }*/
    }

    public void build(Ontology ontology) {
        System.out.println("Building...");
        this.ontology = ontology;
        noOfedges = 0;
        Set<OntologyClass> classesSet = ontology.getClasses();
        Set<OntologyDatatype> datatypeSet = ontology.getDatatypes();
        Set<OntologyProperty> properties = ontology.getProperties();
        Set<OntologyIndividual> individualsSet = new HashSet<OntologyIndividual>();

        classes = new HashSet<OntologyGraphNode>(classesSet.size());
        datatypes = new HashSet<OntologyGraphNode>(datatypeSet.size());
        individuals = new HashSet<OntologyGraphNode>(individualsSet.size());
        literals = new HashSet<OntologyGraphNode>();
        individualToClassPropertyMap = new HashMap<OntologyGraphEdge, OntologyGraphEdge>();

        entityMap
                = new HashMap<OntologyEntity, OntologyGraphNode>(classesSet.
                        size());
        valueMap
                = new HashMap<OntologyValue, OntologyGraphNode>(individualsSet.
                        size());

        partitionMap
                = new HashMap<OntologyGraphNode, Partition>(classesSet.size()
                        + datatypeSet.size());

        uriToNodeMap
                = new HashMap<String, OntologyGraphObject>(classesSet.size()
                        + datatypeSet.size());

        propertyToEdgeMap
                = new HashMap<OntologyProperty, Set<OntologyGraphEdge>>(properties.size());

        //System.out.println("Reading classes.");
        edges = new HashSet<OntologyGraphEdge>(classesSet.size());

        for (OntologyClass clss : classesSet) {
            addClassToNodes(clss);
        }
        //System.out.println("Classes read.");

        //System.out.println("Reading Datatypes.");
        for (OntologyDatatype datatype : datatypeSet) {
            String uri = datatype.toString();
            String label = getLabelFromUri(uri);
            OntologyGraphNode node = new OntologyGraphNode(datatype, label);
            datatypes.add(node);
            entityMap.put(datatype, node);
            uriToNodeMap.put(uri, node);
        }
        //System.out.println("Datatypes read.");

        //System.out.println("Reading properties.");
        //Map<OntologyProperty, Cardinality> cardinfo
        //= ontology.getCardinalityInfo();
        int noOfProperties = 0;
        System.out.println("Number of Properties = " + properties.size());
        for (OntologyProperty property : properties) {
            property.getInverses(ontology);
            //check the cardanlity

            //System.out.println("C--" + property + " " + property.
            //getCardinality());
            Set<OntologyClass> domains = property.getDomains(ontology);
            Set<OntologyEntity> ranges = property.getRanges(ontology);

            //System.out.println("Property: " + ++noOfProperties);
            for (OntologyClass domain : domains) {
                System.out.println("C--" + property + " - " + domain + " "
                        + property.getCardinality(ontology, domain));
                OntologyGraphNode domainNode = getEntityNode(domain);
                for (OntologyEntity range : ranges) {
                    OntologyGraphNode rangeNode;
                    if (entityMap.containsKey(range)) {
                        rangeNode = entityMap.get(range);
                    } else {
                        rangeNode = new OntologyGraphNode(range);
                        addEntity(rangeNode, entityMap);
                    }

                    domainNode.addConnection(property, rangeNode);
                    noOfedges++;
                    addEdge(domainNode.lastEdgeAdded());
                }
            }
        }
        //System.out.println("Properties read.");

        //System.out.println("Connecting classes.");
        for (OntologyClass clss : classesSet) {
            Set<OntologyClass> equivalentClasses = clss.getEquivalentClasses(
                    ontology);
            Set<OntologyClass> disjointClasses = clss.getDisjointClasses(
                    ontology);
            Set<OntologyClass> subclasses = clss.getSubClasses(ontology);
            Set<OntologyIndividual> instances = clss.getIndividuals(ontology);

            OntologyGraphNode class1 = getEntityNode(clss);

            //System.out.println("Connecting equivalent classes.");
            for (OntologyClass clss2 : equivalentClasses) {
                if (clss.equals(clss2)) {
                    continue;
                }
                OntologyGraphNode class2 = getEntityNode(clss2);
                class1.addConnection(EdgeType.EQUIVALENTTO, class2);
                class2.addConnection(EdgeType.EQUIVALENTTO, class1);
                noOfedges += 2;
                setAndAddInverseEdges(class1, class2);
            }

            //System.out.println("Connecting disjoint classes.");
            for (OntologyClass clss2 : disjointClasses) {
                OntologyGraphNode class2 = getEntityNode(clss2);
                class1.addConnection(EdgeType.DISJOINTWITH, class2);
                class2.addConnection(EdgeType.DISJOINTWITH, class1);
                noOfedges += 2;
                setAndAddInverseEdges(class1, class2);
            }

            //System.out.println("Connecting subclasses.");
            for (OntologyClass clss2 : subclasses) {
                OntologyGraphNode class2 = getEntityNode(clss2);
                class1.addConnection(EdgeType.SUBCLASS, class2);
                class2.addConnection(EdgeType.SUPERCLASS, class1);
                noOfedges += 2;
                setAndAddInverseEdges(class1, class2);
            }

            //System.out.println("Connecting instances.");
            for (OntologyIndividual ind : instances) {
                OntologyGraphNode individual = valueMap.get(ind);
                individuals.add(individual);
                class1.addConnection(EdgeType.INSTANCE, individual);
                individual.addConnection(EdgeType.INSTANCEOF, class1);
                noOfedges += 2;
                setAndAddInverseEdges(class1, individual);

                Map<OntologyProperty, Set<OntologyValue>> indValues = ind.
                        getPropertyValues(ontology);
                Set<OntologyProperty> keys = indValues.keySet();

                //System.out.println("Connecting instances to properties.");
                //Individual properties
                for (OntologyProperty property : keys) {
                    Set<OntologyValue> values = indValues.get(property);
                    for (OntologyValue value : values) {
                        addValue(value, valueMap);
                        OntologyGraphNode individual2 = valueMap.get(value);
                        individual.addConnection(property, individual2);
                        connectIndividualEdgeToClassEdge(individual, property,
                                individual2);
                        noOfedges++;
                        addEdge(individual.lastEdgeAdded());
                    }
                }
            }
        }

        System.out.println("Number of nodes: " + (classes.size() + datatypes.
                size()));
        System.out.println("Number of edges: " + edges.size());
        System.out.println("Number of Individuals: " + individuals.size());
    }

    private String getLabelFromUri(String uri) {
        int len = uri.length();
        int end = uri.charAt(len - 1) == '/' ? len - 1 : len;
        for (int i = len - 1; i >= 0; i--) {
            char ch = uri.charAt(i);
            if (ch == '#' || ch == '/') {
                return uri.substring(i + 1, end);
            }
        }

        return "";
    }

    private void collapseEequivalentClasses() {
        // Collapse equivalent classes
        Set<OntologyGraphNode> skipSet = new HashSet<OntologyGraphNode>();
        Map<OntologyGraphNode, OntologyGraphNode> equivalenceMap
                = new HashMap<OntologyGraphNode, OntologyGraphNode>();

        for (OntologyGraphNode clss : classes) {
            if (skipSet.contains(clss)) {
                continue;
            }
            Set<OntologyGraphNode> equivalentClasses
                    = clss.getNextNodes(EdgeType.EQUIVALENTTO);
            for (OntologyGraphNode equivalentClass : equivalentClasses) {
                if (clss == equivalentClass) {
                    continue;
                }
                skipSet.add(equivalentClass);
                equivalenceMap.put(equivalentClass, clss);
                Set<OntologyGraphEdge> equivalentClassEdges = equivalentClass.
                        getEdges();
                // Connect edges of equivalent classes
                for (OntologyGraphEdge edge : equivalentClassEdges) {
                    //if (edge.isInverse())
                    //continue;
                    this.edges.remove(edge);
                    if (clss.edgeExists(edge) || edge.getNextNode().equals(clss)) {
                        continue;
                    }
                    clss.addConnection(edge);
                    addEdge(clss.lastEdgeAdded());
                }
            }
        }

        for (OntologyGraphNode clss : skipSet) {
            classes.remove(clss);
        }

        // Connect edges to equivalent classes (edges going to equivalent classes)
        List<OntologyGraphEdge> edgesToRemove = new ArrayList<OntologyGraphEdge>();
        List<OntologyGraphEdge> edgesToAdd = new ArrayList<OntologyGraphEdge>();
        for (OntologyGraphEdge edge : this.edges) {
            if (equivalenceMap.containsKey(edge.getNextNode())) {
                OntologyGraphNode equivalentClass = edge.getNextNode();
                OntologyGraphNode clss = equivalenceMap.get(equivalentClass);
                OntologyGraphNode subject = edge.getPreviousNode();
                edgesToRemove.add(edge);
                if (subject.edgeExists(edge, clss)) {
                    continue;
                }

                subject.addConnection(edge, clss);
                edgesToAdd.add(subject.lastEdgeAdded());
            }
        }

        for (OntologyGraphEdge edge : edgesToRemove) {
            this.edges.remove(edge);
        }
        for (OntologyGraphEdge edge : edgesToAdd) {
            addEdge(edge);
        }

        for (OntologyGraphEdge edge : edges) {
            String uri = edge.getURIAsStr();
            uriToNodeMap.put(uri, edge);
        }
    }

    private void setAndAddInverseEdges(OntologyGraphNode class1,
            OntologyGraphNode class2) {
        OntologyGraphEdge lastAdded1
                = class1.lastEdgeAdded();
        OntologyGraphEdge lastAdded2
                = class2.lastEdgeAdded();
        lastAdded1.inverseOf(lastAdded2);
        lastAdded2.inverseOf(lastAdded1);
        lastAdded2.setIsInverse();
        addEdge(lastAdded1);
        addEdge(lastAdded2);
    }

    private void connectIndividualEdgeToClassEdge(OntologyGraphNode individual,
            OntologyProperty property, OntologyGraphNode object) {
        Set<OntologyGraphEdge> propEdges = propertyToEdgeMap.get(property);
        if (propEdges != null) {
            List<OntologyGraphEdge> remove = new ArrayList<OntologyGraphEdge>();
            for (OntologyGraphEdge edge : propEdges) {
                if (edge.rangeIsEmpty()) {
                    remove.add(edge);
                    edges.remove(edge);
                    OntologyGraphNode prevNode = edge.getPreviousNode();
                    prevNode.removeConnection(edge);
                }
            }
            for (OntologyGraphEdge edge : remove) {
                propEdges.remove(edge);
            }
        }

        Set<OntologyGraphNode> classes
                = individual.getNextNodes(EdgeType.INSTANCEOF);
        Set<OntologyGraphNode> objectClasses
                = object.getNextNodes(EdgeType.INSTANCEOF);
        Map<OntologyGraphNode, OntologyGraphEdge> classPropertyEdges
                = new HashMap<OntologyGraphNode, OntologyGraphEdge>(classes.
                        size());

        if (objectClasses.size() > 0) {
            for (OntologyGraphNode clss : classes) {
                Set<OntologyGraphEdge> edgesSet = clss.getEdges(property);
                if (edgesSet.size() == 0) {
                    clss.addConnection(property, objectClasses.iterator().next());
                    addEdge(clss.lastEdgeAdded());
                    edgesSet = clss.getEdges(property);
                }
                classPropertyEdges.put(clss, edgesSet.iterator().next());
            }
        }
        Set<OntologyGraphEdge> edges = individual.getEdges(property);
        for (OntologyGraphEdge edge : edges) {
            for (OntologyGraphNode clss : classes) {
                OntologyGraphEdge propertyEdge = classPropertyEdges.get(clss);
                if (propertyEdge == null) {
                    continue;
                }
                individualToClassPropertyMap.put(edge, propertyEdge);
                propertyEdge.incrementWeight(OntologyGraphEdge.propertyWeight);
            }
        }
    }

    private void addEntity(OntologyGraphNode entity,
            Map<OntologyEntity, OntologyGraphNode> map) {
        if (classes.contains(entity) || datatypes.contains(entity)) {
            return;
        }
        NodeType nodeType = entity.getNodeType();
        if (nodeType == NodeType.CLASS) {
            classes.add(entity);
        } else {
            datatypes.add(entity);
        }
        map.put((OntologyEntity) entity.getObject(), entity);
    }

    private void addEntity(OntologyEntity entity,
            Map<OntologyEntity, OntologyGraphNode> map) {
        OntologyGraphNode node = new OntologyGraphNode(entity);
        if (classes.contains(node) || datatypes.contains(node)) {
            return;
        }
        NodeType nodeType = node.getNodeType();
        if (nodeType == NodeType.CLASS) {
            classes.add(node);
        } else {
            datatypes.add(node);
        }
        map.put(entity, node);
    }

    private void addValue(OntologyGraphNode value,
            Map<OntologyValue, OntologyGraphNode> map) {
        if (individuals.contains(value) || literals.contains(value)) {
            return;
        }
        NodeType nodeType = value.getNodeType();
        if (nodeType == NodeType.INDIVIDUAL) {
            individuals.add(value);
        } else {
            literals.add(value);
        }
        map.put((OntologyValue) value.getObject(), value);
    }

    private void addValue(OntologyValue value,
            Map<OntologyValue, OntologyGraphNode> map) {
        OntologyGraphNode node = new OntologyGraphNode(value);
        if (individuals.contains(node) || literals.contains(node)) {
            return;
        }
        NodeType nodeType = node.getNodeType();
        if (nodeType == NodeType.INDIVIDUAL) {
            individuals.add(node);
        } else {
            literals.add(node);
        }
        map.put(value, node);
    }

    @Override
    public String toString() {
        //String ret = "";
        StringBuffer strBuffer = new StringBuffer("");
        //strBuffer.append(" String Buffer");

        for (OntologyGraphNode subject : classes) {
            Set<OntologyGraphEdge> edges = subject.getEdges();
            for (OntologyGraphEdge predicate : edges) {
                OntologyGraphNode object = predicate.getNextNode();
                strBuffer.append(subject + " -- " + predicate + " -- " + object
                        + "\n");
            }
        }

        for (OntologyGraphNode subject : individuals) {
            Set<OntologyGraphEdge> edges = subject.getEdges();
            for (OntologyGraphEdge predicate : edges) {
                OntologyGraphNode object = predicate.getNextNode();
                strBuffer.append(subject + " -- " + predicate + " -- " + object
                        + "\n");
            }
        }

        return strBuffer.toString();
    }

    public void save(String filepath) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filepath), "utf-8"))) {
            for (OntologyGraphNode subject : classes) {
                Set<OntologyGraphEdge> edges = subject.getEdges();
                for (OntologyGraphEdge predicate : edges) {
                    OntologyGraphNode object = predicate.getNextNode();
                    writer.write(subject + " -- \"" + predicate.getWeight()
                            + "\"@"
                            + predicate.getPureURI() + " -- " + object + "\n");
                }
            }

            for (OntologyGraphNode subject : individuals) {
                Set<OntologyGraphEdge> edges = subject.getEdges();
                for (OntologyGraphEdge predicate : edges) {
                    OntologyGraphNode object = predicate.getNextNode();
                    writer.write(subject + " -- " + predicate.getWeight()
                            + "\"@"
                            + predicate.getPureURI() + " -- " + object + "\n");
                }
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean skipEdge(OntologyGraphEdge edge) {
        return edge.isDisjoint() || edge.isInstance()
                || edge.isInverse() || edge.isSubclass();
    }

    public void saveAsGraph() {
        String nodeNumberPath = "graph-node-number.txt";
        String graphPath = "graph.txt";

        int indexCounter = 0;
        Map<OntologyGraphObject, Integer> reverseIndex
                = new HashMap<OntologyGraphObject, Integer>();
        BufferedFileWriter bw = null;
        try {
            bw = new BufferedFileWriter(nodeNumberPath);
            for (OntologyGraphNode clss : classes) {
                bw.writeln(clss + ": " + ++indexCounter);
                reverseIndex.put(clss, indexCounter);
            }
            for (OntologyGraphNode datatype : datatypes) {
                bw.writeln(datatype + ": " + ++indexCounter);
                reverseIndex.put(datatype, indexCounter);
            }
            for (OntologyGraphEdge edge : edges) {
                if (skipEdge(edge)) {
                    continue;
                }
                bw.writeln(edge + ": " + ++indexCounter);
                reverseIndex.put(edge, indexCounter);
            }
            bw.close();

            int numberOfNodes = classes.size() + datatypes.size() + edges.size();

            Map<Integer, Float>[] adjacencyArray = new Map[numberOfNodes];
            for (int i = 0; i < adjacencyArray.length; i++) {
                adjacencyArray[i] = new HashMap<Integer, Float>();
            }

            int noOfEdges = 0;
            for (OntologyGraphEdge edge : edges) {
                if (skipEdge(edge)) {
                    continue;
                }
                OntologyGraphNode subject = edge.getPreviousNode();
                OntologyGraphNode object = edge.getNextNode();
                if (subject.isValue() || object.isValue()) {
                    continue;
                }
                int subjectNumber = reverseIndex.get(subject) - 1;
                int predicateNumber = reverseIndex.get(edge) - 1;
                int objectNumber = reverseIndex.get(object) - 1;

                if (!adjacencyArray[subjectNumber].containsKey(predicateNumber)) {
                    adjacencyArray[subjectNumber].put(predicateNumber, edge.
                            getWeight());
                    adjacencyArray[predicateNumber].put(subjectNumber, edge.
                            getWeight());
                    noOfEdges++;
                } else {
                    float newWeight = adjacencyArray[subjectNumber].get(
                            predicateNumber) + edge.getWeight();
                    adjacencyArray[subjectNumber].
                            put(predicateNumber, newWeight);
                    adjacencyArray[predicateNumber].
                            put(subjectNumber, newWeight);
                }

                if (!adjacencyArray[predicateNumber].containsKey(objectNumber)) {
                    adjacencyArray[predicateNumber].put(objectNumber, edge.
                            getWeight());
                    adjacencyArray[objectNumber].put(predicateNumber, edge.
                            getWeight());
                    noOfEdges++;
                } else {
                    float newWeight = adjacencyArray[predicateNumber].get(
                            objectNumber) + edge.getWeight();
                    adjacencyArray[predicateNumber].put(objectNumber, newWeight);
                    adjacencyArray[objectNumber].put(predicateNumber, newWeight);
                }

                /*
                 * if (!adjacencyArray[objectNumber].containsKey(subjectNumber)) {
                 * adjacencyArray[objectNumber].put(subjectNumber, edge.getWeight());
                 * } else {
                 * float newWeight = adjacencyArray[objectNumber].get(subjectNumber) + edge.getWeight();
                 * adjacencyArray[objectNumber].put(subjectNumber, newWeight);
                 * }
                 */
            }

            noOfEdges = 0;
            for (Map<Integer, Float> map : adjacencyArray) {
                noOfEdges += map.size();
            }

            noOfEdges /= 2;

            bw = new BufferedFileWriter(graphPath);
            bw.writeln(numberOfNodes + " " + noOfEdges + " 001");

            for (Map<Integer, Float> map : adjacencyArray) {
                int size = map.size();
                if (size < 1) {
                    bw.writeln();
                    continue;
                }
                Iterator<Integer> keys = map.keySet().iterator();
                int key = keys.next();
                int value = (int) Math.floor((double) map.get(key));
                bw.write("" + (key + 1) + " " + value);
                while (keys.hasNext()) {
                    key = keys.next();
                    //value = map.get(key);
                    value = (int) Math.floor((double) map.get(key));
                    bw.write(" " + (key + 1) + " " + value);
                }
                bw.writeln();
            }

            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(OntologyGraph.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public void generateSchema(int noOfPartitions) {
        generateSchema("graph.txt.part." + noOfPartitions);
    }

    public void generateSchema() {
        generateSchema("graph.txt.part.10");
    }

    public void generateSchema(String partitionsPath) {
        generateSchema("ontology.sql", partitionsPath);
    }

    public void generateSchema(String schemaPath, String partitionsPath) {
        BufferedFileWriter writer = null;
        BufferedFileReader reader = null;
        try {
            writer = new BufferedFileWriter(schemaPath);
            reader = new BufferedFileReader(partitionsPath);
            Map<Integer, OntologyGraphObject> idMap = readIds();

            String line;
            int nodeId = 0;
            Set<Partition> partitions = new HashSet<Partition>();
            Map<Integer, Partition> map = new HashMap<Integer, Partition>();
            while ((line = reader.readLine()) != null) {
                int partitionId = Integer.parseInt(line);
                OntologyGraphObject node = idMap.get(++nodeId);
                if (map.containsKey(partitionId)) {
                    map.get(partitionId).add(node);
                } else {
                    Partition partition = new Partition(partitionId);
                    partition.add(node);
                    map.put(partitionId, partition);
                    partitions.add(partition);
                }
            }
            reader.close();

            writer.writeln("CREATE DATABASE Ontology");
            writer.writeln();
            //noOfObjectsInSet
            for (Partition partition : partitions) {
                writer.writeln(partition.toSchema());
            }
            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(OntologyGraph.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    private List<Pair<String, String>> labelsAndDatatypes(OntologyGraphNode node,
            Set<OntologyGraphObject> set) {
        Set<OntologyGraphEdge> edges = node.getEdges();
        List<Pair<String, String>> ret
                = new ArrayList<Pair<String, String>>(edges.size());
        for (OntologyGraphEdge edge : edges) {
            if (skipEdge(edge)) {
                continue;
            }

            OntologyGraphNode next = edge.getNextNode();
            if (!set.contains(next)) {
                continue;
            }
            Pair<String, String> pair;
            if (next.isClass()) {
                pair = new Pair<String, String>(edge.getLabel(), "LONG");
            } else {
                pair = new Pair<String, String>(edge.getLabel(), next.
                        getSQLDatatype());
            }
            ret.add(pair);
        }

        return ret;
    }

    private int noOfObjectsInSet(OntologyGraphNode subject,
            Set<OntologyGraphObject> set) {
        Set<OntologyGraphEdge> edges = subject.getEdges();

        int count = 0;
        for (OntologyGraphEdge edge : edges) {
            if (!edge.isProperty()) {
                continue;
            }
            OntologyGraphNode node = edge.getNextNode();
            if (set.contains(node)) {
                count++;
            }
        }

        return count;
    }

    private Map<Integer, OntologyGraphObject> readIds() {
        return readIds("graph-node-number.txt");
    }

    private Map<Integer, OntologyGraphObject> readIds(String path) {
        Map<Integer, OntologyGraphObject> map
                = new HashMap<Integer, OntologyGraphObject>(classes.size()
                        + datatypes.size());
        try {
            BufferedFileReader br = new BufferedFileReader(
                    "graph-node-number.txt");
            String line;
            while ((line = br.readLine()) != null) {
                int len = line.length();
                String uri = "";
                int id = 0;
                for (int i = len - 1; i >= 0; i--) {
                    if (line.charAt(i) == ':' && line.charAt(i + 1) == ' ') {
                        uri = line.substring(0, i);
                        id = Integer.parseInt(line.substring(i + 2));
                        break;
                    }
                }

                OntologyGraphObject node = uriToNodeMap.get(uri);
                map.put(id, node);
            }

            br.close();

            return map;
        } catch (IOException ex) {
            Logger.getLogger(OntologyGraph.class.getName()).
                    log(Level.SEVERE, null, ex);
            return null;
        }

    }

    private class Partition {

        private int id;

        private Set<OntologyGraphObject> nodes;

        public Partition(int id) {
            this.id = id;
            nodes = new HashSet<OntologyGraphObject>();
        }

        public Partition(int id, int size) {
            this.id = id;
            nodes = new HashSet<OntologyGraphObject>(size);
        }

        public Partition(int id, Set<OntologyGraphObject> list) {
            this.id = id;
            nodes = list;
        }

        public Partition() {
            nodes = new HashSet<OntologyGraphObject>(classes.size());
            nodes.addAll(classes);
            for (OntologyGraphEdge edge : edges) {
                if (!edge.isProperty()) {
                    continue;
                }
                if (edge.getPreviousNode().isClass() && (edge.getNextNode().
                        isClass() || edge.getNextNode().isDataType())) {
                    nodes.add(edge);
                }
            }
        }

        public void add(OntologyGraphObject node) {
            nodes.add(node);
        }

        public int getId() {
            return id;
        }

        public Set<OntologyGraphObject> getAllNodes() {
            return nodes;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public boolean equals(Object partition) {
            return id == ((Partition) partition).id;
        }

        @Override
        public String toString() {
            return "" + id;
        }

        public String toSchema() {
            String DDL = "";
            Set<OntologyGraphObject> graphObjects = getAllNodes();

            OntologyGraphNode subject = null;// = (OntologyGraphNode)nodes.iterator().next();
            for (OntologyGraphObject graphObject : graphObjects) {
                if (graphObject instanceof OntologyGraphNode) {
                    subject = (OntologyGraphNode) graphObject;
                    break;
                }
            }
            if (subject == null) {
                return DDL;
            }

            int noOfObjects = 0;
            int noOfNodes = 0;
            for (OntologyGraphObject objectNode : graphObjects) {
                if (!(objectNode instanceof OntologyGraphNode)) {
                    continue;
                }
                noOfNodes++;
                OntologyGraphNode node = (OntologyGraphNode) objectNode;
                if (!node.isClass()) {
                    continue;
                }
                int tmpNoOfSubjects = noOfObjectsInSet(node, graphObjects);
                if (tmpNoOfSubjects > noOfObjects) {
                    subject = node;
                    noOfObjects = tmpNoOfSubjects;
                }
            }

            String tableName = subject.getLabel();
            DDL = DDL + "CREATE TABLE " + tableName;
            DDL = DDL + "(" + "\n";

            List<Pair<String, String>> labelsAndDatatypes
                    = labelsAndDatatypes(subject, graphObjects);
            Iterator<Pair<String, String>> iterator = labelsAndDatatypes.
                    iterator();
                    //Pair<String, String> labelAndDatatype = iterator.next();

            //writer.write("\t" + labelAndDatatype.getFirst()
            //        + " " + labelAndDatatype.getSecond());
            DDL = DDL + "\tId LONG";

            while (iterator.hasNext()) {
                Pair<String, String> labelAndDatatype = iterator.next();

                DDL = DDL + (",\n");
                DDL = DDL + "\t" + labelAndDatatype.getFirst()
                        + " " + labelAndDatatype.getSecond();

            }

            DDL = DDL + "\n";
            DDL = DDL + ");\n";
            DDL = DDL + "\n";

            if (noOfNodes - noOfObjects > 0) {
                DDL = DDL + ("CREATE TABLE " + tableName + "Triple") + "\n";
                DDL = DDL + ("(") + "\n";
                DDL = DDL + ("\tId LONG," + "\n");
                DDL = DDL + ("\tSubject LONG,") + "\n";

                DDL = DDL + ("\tPredicate LONG,") + "\n";
                DDL = DDL + ("\tObject LONG,") + "\n";
                DDL = DDL + (")") + "\n";

            }
            return DDL;
        }

    }

}
