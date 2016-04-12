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
import java.io.Reader;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import jdk.nashorn.internal.runtime.regexp.joni.MatcherFactory;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.coode.owlapi.rdfxml.parser.AnonymousNodeChecker;
import org.coode.owlapi.rdfxml.parser.OWLRDFConsumer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import uk.ac.manchester.cs.owl.owlapi.turtle.parser.TurtleParser;

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

    private Map<String, OntologyGraphObject> uriToGraphObjectMap;

    private Map<OntologyProperty, Set<OntologyGraphEdge>> propertyToEdgeMap;

    private Set<OntologyGraphEdge> edges;

    private int noOfedges; // useless

    private Map<String, List<String>> tables;
    private Map<OntologyGraphNode, String> nodeTableMap;
    private Map<OntologyGraphEdge, Pair<String, String>> edgeTableAttrMap;
    private Map<OntologyGraphEdge, Pair<String, String>> propertyTableMap;
    private Map<String, String> labelTableMap;
    private Map<String, OntologyGraphObject> labelObjectMap;

    private Set<Table> tablesSet;

    private static final String propertyQueriesPath = "property-queries/";
    private static final String classQueriesPath = "class-queries/";
    private static final String singlePropertyQueriesPath
            = "single-property-queries/";
    private static final String propertyDataPath = "property-data/";
    private static final String classIdsPath = "class-ids/";
    private static final String queryFileExtension = ".q";

    private static final String schemaName = "ontology";
    private static final String propertySchema = "propertydata";

    private static final String xsdStringType = "^^xsd:string";
    private static final String owlStringType = xsdStringType;
    private static final String obdaPath = "graph.obda";

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
        edgeTableAttrMap.put(edge, new Pair<String, String>(schema
                + "." + table, attr));
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

    private String getTabs(String firstStr, int n) {
        String ret = "";
        int noTabs = ((8 * n - firstStr.length() - 1) / 8) + 1;
        for (int i = 0; i < noTabs; i++) {
            ret += "\t";
        }

        return ret;
    }

    private String getOBDAId(String uri, Map<String, String> reversePrefixes) {
        int index = uri.lastIndexOf("#");
        if (index != -1) {
            index++;
            String namespace = uri.substring(0, index);
            String objId = uri.substring(index);
            String id = reversePrefixes.get(namespace);
            return id + ":" + objId;
        } else {
            index = uri.lastIndexOf("/");
            if (index != -1) {
                index++;
                String namespace = uri.substring(0, index);
                String objId = uri.substring(index);
                String id = reversePrefixes.get(namespace);
                return id + ":" + objId;
            }
        }

        return uri;
    }

    public void generateOBDAFile(String owlFilePath) {
        BufferedFileReader br = null;
        BufferedFileWriter bw = null;
        Map<String, String> prefixes = new HashMap();
        Map<String, String> reversePrefixes = new HashMap();
        try {
            br = new BufferedFileReader(owlFilePath);
            String data = br.read();

            Pattern r
                    = Pattern.compile("<\\!.*ENTITY\\s+\\S+\\s+\"\\S*\"\\s*>");
            Matcher matcher = r.matcher(data);
            while (matcher.find()) {
                String match = matcher.group(0);
                String[] tokens = match.split("\"");
                String[] left = tokens[0].trim().split("\\s");
                String id = left[left.length - 1];
                String uri = tokens[1].split("\"")[0].trim();
                //System.out.println(namespace + ": " + uri);
                prefixes.put(id, uri);
                reversePrefixes.put(uri, id);
                System.out.println(id + ": " + uri);
            }

            bw = new BufferedFileWriter("graph.obda");
            bw.writeln("[PrefixDeclaration]");
            for (String id : prefixes.keySet()) {
                String uri = prefixes.get(id);

                bw.writeln(id + ":" + getTabs(id + ":", 2) + uri);
            }
            bw.writeln();

            bw.writeln("[SourceDeclaration]");
            bw.writeln("sourceUri\timdb-obda");
            bw.writeln("connectionUrl\tjdbc:postgresql://localhost/postgres");
            bw.writeln("username\tsameh");
            bw.writeln("password\t\"\"");
            bw.writeln("driverClass\torg.postgresql.Driver");

            bw.writeln();

            bw.writeln("[MappingDeclaration] @collection [[");
            boolean first = true;
            for (OntologyGraphNode clss : classes) {
                String table = nodeTableMap.get(clss);
                if (table == null) {
                    continue;
                }
                if (!first) {
                    bw.writeln();
                }
                first = false;
                bw.writeln("mappingId" + getTabs("mappingId", 2)
                        + table.split("\\.")[1]);
                bw.writeln("target" + getTabs("target", 2) + "{id}"
                        + " a " + getOBDAId(clss.getURIAsStr(), reversePrefixes)
                        + " .");
                bw.writeln("source" + getTabs("source", 2) + "select id from "
                        + table);

            }
            for (OntologyGraphEdge edge : edges) {
                Pair<String, String> tableAttr = edgeTableAttrMap.get(edge);
                OntologyGraphNode subjectNode = edge.getPreviousNode();
                OntologyGraphNode objectNode = edge.getNextNode();
                if (tableAttr == null) {
                    continue;
                }
                bw.writeln();
                String subjectName
                        = nameFromLabel(edge.getPreviousNode().getLabel());
                String table = tableAttr.getFirst();
                String attr = tableAttr.getSecond();
                bw.writeln("mappingId" + getTabs("mappingId", 2)
                        + edge.getLabel());
                bw.write("target" + getTabs("target", 2) + "{id} "
                        + getOBDAId(edge.getProperty().getURIAsStr(),
                                reversePrefixes) + " {" + attr
                        + "}");
                if (objectNode.isDataType()) {
                    bw.write("^^xsd:"
                            + objectNode.getDatatypeForOBDA());
                }
                bw.writeln(" .");
                bw.writeln("source" + getTabs("source", 2) + "select id, "
                        + attr + " from " + table);
            }
            bw.writeln("]]");
            bw.close();

        } catch (IOException ex) {
            Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE,
                    null, ex);
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
            Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE,
                    null, ex);
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
        return token.substring(1, token.length() - 1);
    }

    private String getStringFromToken(String token) {
        //System.out.println(token);
        //return "\'" + token.substring(1, token.lastIndexOf('\"')) + "\'";
        return token.substring(1, token.lastIndexOf('\"'));
    }

    public void populateSchemaWithData() {
        for (OntologyGraphNode clss : classes) {

        }
    }

    public void insertIntoPropertyTables(String outpath) {
        Map<String, List<String>> tableAttrs = new HashMap();
        BufferedFileWriter bw = null;
        try {
            bw = new BufferedFileWriter(outpath);
            bw.writeln("CREATE SCHEMA IF NOT EXISTS " + propertySchema + ";");
            bw.writeln();
        } catch (IOException ex) {
            Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        File[] files = (new File(propertyDataPath)).listFiles();

        Set<String> createdTables = new HashSet<String>();

        for (File file : files) {
            BufferedFileReader br = null;
            try {
                br = new BufferedFileReader(file);
                br.readLine();
                String line = br.readLine();
                while (line != null && line.equals("")) {
                }
                List<String> csvLine = CSV.parseLine(line);
                boolean firstIsReference;
                boolean secondIsReference;
                String token = csvLine.get(0);
                if (token.charAt(0) == '<'
                        && token.charAt(token.length() - 1) == '>') {
                    firstIsReference = true;
                } else {
                    firstIsReference = false;
                }
                token = csvLine.get(1);
                if (token.charAt(0) == '<'
                        && token.charAt(token.length() - 1) == '>') {
                    secondIsReference = true;
                } else {
                    secondIsReference = false;
                }

                br.close();
                br = new BufferedFileReader(file);

                String path = file.getPath();
                String classLabel = path.split("/")[1].split("$", 2)[0];
                String propertyLabel = path.split("/")[1].split("$", 2)[1];
                propertyLabel = propertyLabel.substring(0,
                        propertyLabel.lastIndexOf("."));
                String tableName = propertySchema + "." + classLabel
                        + firstToUpperCase(propertyLabel);
                line = br.readLine();
                if (line != null && !createdTables.contains(tableName)) {
                    List<String> labels = CSV.parseLine(line);
                    bw.writeln("CREATE TABLE " + tableName + "(");
                    if (firstIsReference) {
                        bw.writeln("\tid" + " INTEGER,");
                    } else {
                        bw.writeln("\t" + labels.get(0)
                                + //" VARCHAR(255),");
                                " text,");
                    }
                    if (secondIsReference) {
                        bw.writeln("\t" + labels.get(1)
                                + " INTEGER");
                    } else {
                        bw.writeln("\t" + labels.get(1)
                                + " text");
                    }
                    tableAttrs.put(tableName, labels);
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

                List<String> attrs = tableAttrs.get(tableName);
                bw.writeln("COPY " + tableName + " (id, "
                        + attrs.get(1) + ") FROM stdin;");
                while ((line = br.readLine()) != null) {
                    List<String> list = CSV.parseLine(line);
                    //bw.writeln("INSERT INTO " + tableName + " VALUES");
                    //bw.write("\t(");
                    if (firstIsReference) {
                        bw.write(getReferenceFromToken(list.get(0)));
                    } else {
                        bw.write(getStringFromToken(list.get(0)));
                    }
                    //bw.write(", ");
                    bw.write("\t");
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
                    //bw.writeln(");");
                    bw.writeln();
                }
                bw.writeln("\\.");
                bw.writeln();
                br.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(OntologyGraph.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(OntologyGraph.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
        try {
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    private List<Pair<String, OntologyGraphEdge>>
            getAttrFromDB(OntologyGraphNode node,
                    Set<OntologyGraphEdge> properties,
                    Set<String> propertiesInOBDA,
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
            br = new BufferedFileReader(propertyQueriesPath
                    + clssName + queryFileExtension);
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
                            + clssName + "_" + propertyName
                            + queryFileExtension;
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
            if (bw != null) {
                bw.close();
            }
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
    public void getAllData(String obdaPath) {
        int index = 0;
        Set<String> entitiesInOBDA = getEntitiesInOBDA(obdaPath);
        for (OntologyGraphNode clss : classes) {
            //System.out.println((index++) + ": " + clss);
            getData(clss.getURIAsStr(), entitiesInOBDA);
            Map<String, String> prefixes = getPrefixes(obdaPath);
            generateClassSparqlQueries(prefixes, entitiesInOBDA);
        }
    }

    public void getData(String uri, Set<String> entitiesInOBDA) {
        OntologyGraphNode node = (OntologyGraphNode) uriToGraphObjectMap.get(uri);
        String queries = "";
        Map<String, String> prefixes = getPrefixes(obdaPath);

        Set<OntologyGraphEdge> properties = node.getEdges(EdgeType.PROPERTY);
        generatePropertySparqlQueries(node, properties, prefixes,
                entitiesInOBDA);

        List<Pair<String, OntologyGraphEdge>> propertyFiles
                = getAttrFromDB(node, properties, entitiesInOBDA, prefixes);

        /*for (Pair<String, OntologyGraphEdge> pair : propertyFiles) {
         String filePath = pair.getFirst();
         OntologyGraphEdge property = pair.getSecond();
         try {
         ontop(obdaPath, owlPath, filePath, propertyDataPath
         + property.getPreviousNode().getLabel() + "_"
         + property.getLabel());
         } catch (IOException ex) {
         Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE, null,
         ex);
         }
         }*/
    }

    private boolean generateClassSparqlQueries(Map<String, String> prefixes,
            Set<String> entitiesInOBDA) {
        String queries = "";
        for (OntologyGraphNode clss : classes) {
            String label = clss.getLabel().replaceAll("[-.]", "");
            String classURI = clss.getAsClass().getURIAsStr();
            String attr = getAttrStr(prefixes, classURI, label);
            if (!entitiesInOBDA.contains(attr)) {
                continue;
            }

            queries += "[QueryItem=\"" + label + "\"]\n";
            for (String key : prefixes.keySet()) {
                queries += "PREFIX " + prefixes.get(key) + ": <" + key + ">\n";
            }

            String classId = getAttrStr(prefixes, clss.getURIAsStr(),
                    label);

            queries += "select ?x\n"
                    + "where {\n"
                    + "  ?x a " + classId + ".\n"
                    + "}\n\n";

        }

        try {
            if (!queries.equals("")) {
                BufferedFileWriter bw
                        = new BufferedFileWriter(classQueriesPath
                                + "all-classes"
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
    
    public boolean generateClassSparqlQueries(String obdaPath) {
        Map<String, String> prefixes = getPrefixes(obdaPath);
        Set<String> entitiesInOBDA = getEntitiesInOBDA(obdaPath);

        return generateClassSparqlQueries(prefixes, entitiesInOBDA);
    }

    private boolean generatePropertySparqlQueries(OntologyGraphNode node,
            Set<OntologyGraphEdge> properties, Map<String, String> prefixes,
            Set<String> entitiesInOBDA) {
        String queries = "";

        for (OntologyGraphEdge property : properties) {
            String label = property.getLabel();
            String propertyURI = property.getProperty().getURIAsStr();
            String attr = getAttrStr(prefixes, propertyURI, label);
            if (!entitiesInOBDA.contains(attr)) {
                continue;
            }

            queries += "[QueryItem=\"" + label + "\"]\n";
            for (String key : prefixes.keySet()) {
                queries += "PREFIX " + prefixes.get(key) + ": <" + key + ">\n";
            }

            String clss = getAttrStr(prefixes, node.getURIAsStr(),
                    node.getLabel().replaceAll("[-.]", ""));
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

    public boolean generatePropertySparqlQueries(OntologyGraphNode node,
            String obdaPath) {
        Map<String, String> prefixes = getPrefixes(obdaPath);
        Set<String> propertiesInOBDA = getEntitiesInOBDA(obdaPath);

        Set<OntologyGraphEdge> properties = node.getEdges(EdgeType.PROPERTY);
        return generatePropertySparqlQueries(node, properties, prefixes,
                propertiesInOBDA);
    }

    public boolean generatePropertySparqlQueries(String obdaPath) {
        Map<String, String> prefixes = getPrefixes(obdaPath);
        Set<String> propertiesInOBDA = getEntitiesInOBDA(obdaPath);

        boolean succeeded = true;
        for (OntologyGraphNode clss : classes) {
            Set<OntologyGraphEdge> properties
                    = clss.getEdges(EdgeType.PROPERTY);
            boolean success = generatePropertySparqlQueries(clss, properties,
                    prefixes, propertiesInOBDA);
            if (!success) {
                succeeded = false;
            }
        }

        return succeeded;
    }
    
    public boolean generateSparqlQueries(String obdaPath) {
        boolean success = generateClassSparqlQueries(obdaPath);
        return success & generatePropertySparqlQueries(obdaPath);
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

    private Set<String> getEntitiesInOBDA(String path) {
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
            Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE,
                    null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OntologyGraph.class.getName()).log(Level.SEVERE,
                    null, ex);
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

                //w.write(" -- REFERENCES " + sprclssName + "(id)");
                foreignKeys.add(getForeignKey(schemaName, tableName,
                        attrName, sprclssName, "id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean skipProperty(OntologyProperty property,
            OntologyClass subject, OntologyObject object) {
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

    private Set<String> omitPrefixes(Set<String> ids) {
        Set<String> set = new HashSet<String>(ids.size());
        for (String id : ids) {
            set.add(id.split(":")[1]);
        }
        return set;
    }

    public void toSchema(String fileName, String obdaPath) {
        initializeTableMaps();
        Set<Pair<OntologyProperty, OntologyObject>> usedProperties
                = new HashSet();
        Set<String> propertiesInOBDA
                = omitPrefixes(getEntitiesInOBDA(obdaPath)); // Should be handled as null/empty

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
                if (tableName.equals("")) { // double check - Why????!!!!!
                    continue;
                }
                w.writeln();
                w.writeln("CREATE TABLE " + tableFullName + "(");
                addTable(clss, schemaName, tableName);
                w.write("\tid SERIAL PRIMARY KEY");

                writeSuperClassReferences(w, clss, foreignKeys, schemaName,
                        tableName);

                Set<OntologyGraphEdge> binary = new HashSet();
                Set<OntologyGraphEdge> properties = new HashSet();
                Set<String> binaryPropertyNames = new HashSet();
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
                        binaryPropertyNames.add(edge.getLabel());
                        continue;
                    }

                    properties.add(edge);

                    addFieldToTable(w, object, edge, it, tableName,
                            foreignTableName, foreignKeys, propertiesInOBDA);
                }
                w.writeln();
                w.writeln(");");

                createBinaryTables(w, binary, clss, tableName, foreignKeys);

                insertValues(w, tableName, properties, binary,
                        propertiesInOBDA);
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

    // Test method
    public String replicateAllAttributesInSubclass() {
        OntologyGraphNode subclass = null;
        for (OntologyGraphNode clss : classes) {
            if (clss.getLabel().equals("Editor")) {
                subclass = clss;
                break;
            }
        }
        OntologyGraphNode superclass = subclass.getEdges(EdgeType.SUPERCLASS).
                iterator().next().getNextNode();
        return replicateAllAttributesInSubclass(superclass, subclass);
    }

    public String replicateAllAttributesInSubclass(OntologyGraphNode superclass,
            OntologyGraphNode subclass) {
        Set<OntologyGraphEdge> properties
                = superclass.getEdges(EdgeType.PROPERTY);
        String table1 = superclass.getLabel().replaceAll("[-.]", "");
        String table2 = subclass.getLabel().replaceAll("[-.]", "");
        String sql = "";
        for (OntologyGraphEdge property : properties) {
            OntologyGraphNode objectNode = property.getNextNode();
            OntologyClass superclassObject = superclass.getAsClass();
            Cardinality card = property.getProperty().getCardinality(ontology,
                    superclassObject);
            if (card.getMin() > 1 || card.getMax() > 1) {
                continue;
            }

            sql += replicateAttributeInTable(schemaName, table1, table2, "id",
                    "id", property.getLabel(), property.getLabel(),
                    objectNode.getSQLDatatype()) + "\n\n\n";
        }

        return sql;
    }

    public static String replicateAttributeInTable(String schema1, String table1,
            String schema2, String table2, String id1, String id2,
            String column1, String column2, String datatype) {
        String table1FullName = schema1 + "." + table1; // Super class
        String table2FullName = schema2 + "." + table2; // Sub class
        String sql = replicateAttributeInTable(table1FullName, table2FullName,
                id1, id2, column1, column2, datatype);
        return sql;
    }

    public static String replicateAttributeInTable(String schema, String table1,
            String table2, String id1, String id2, String column1,
            String column2, String datatype) {
        String sql = replicateAttributeInTable(schema, table1, schema,
                table2, id1, id2, column1, column2, datatype);
        return sql;
    }

    public static String replicateAttributeInTable(String table1, String table2,
            String id1, String id2, String column1, String column2,
            String datatype) {
        String sql = "ALTER TABLE " + table2 + " ADD COLUMN " + column2 + " "
                + datatype + ";\n\n";
        sql += "UPDATE TABLE " + table2
                + " SET " + column2 + "=sq." + column1 + "\n"
                + "FROM  (\n"
                + "   SELECT " + table1 + "." + id1 + ", " + table1 + "."
                + column1 + "\n"
                + "   FROM   TABLE " + table1 + "\n"
                + "   ) AS sq\n"
                + "WHERE  " + table2 + "." + id2 + "=sq." + id1 + ";";

        return sql;
    }

    private void insertValues(BufferedFileWriter w, String tableName,
            Set<OntologyGraphEdge> properties, Set<OntologyGraphEdge> binary,
            Set<String> propertiesInOBDA) throws IOException {
        /*if (!(properties.size() + binary.size() > 0)) {
         return;
         }*/

        Set<OntologyGraphEdge> allProperties = new HashSet(properties.size()
                + binary.size());
        allProperties.addAll(properties);
        allProperties.addAll(binary);

        Set<OntologyGraphEdge> localProperties = new HashSet();
        for (OntologyGraphEdge property : properties) {
            if (propertiesInOBDA.contains(property.getLabel())) {
                localProperties.add(property);
            }
        }

        Set<OntologyGraphEdge> allLocalProperties = new HashSet();
        for (OntologyGraphEdge property : allProperties) {
            if (propertiesInOBDA.contains(property.getLabel())) {
                allLocalProperties.add(property);
            }
        }

        /*if (!(propertyNames.size() > 0))
         return;
         int propertyCount = 0;
         for (String propertyName : propertyNames) {
         if (propertiesInOBDA.contains(propertyName))
         propertyCount++;
         }
         if (!(propertyCount > 0))
         return;*/
        w.writeln();
        w.writeln("CREATE MATERIALIZED VIEW ids AS");
        boolean first = true;
        for (OntologyGraphEdge property : allLocalProperties) {
            w.writeln();
            if (!first) {
                w.writeln("UNION");
            }
            first = false;
            w.writeln("(");
            w.writeln("    SELECT x FROM");
            String propertyLabel = property.getLabel();
            String fullAttributeName = fullAttributeName(propertySchema,
                    tableName, propertyLabel) + "data";
            w.writeln("    " + fullAttributeName);
            w.write(")");
        }
        w.writeln(";");

        w.writeln();
        w.write("INSERT INTO " + schemaName + "." + tableName
                + "(id");
        for (OntologyGraphEdge property : localProperties) {

            //if (propertiesInOBDA.contains(propertyName))
            w.write(", " + property.getLabel());
        }
        w.writeln(")");
        w.write("SELECT ");
        w.write("ids.x AS id");

        for (OntologyGraphEdge property : localProperties) {
            String propertyLabel = property.getLabel();
            String fullAttributeName = fullAttributeName(propertySchema,
                    tableName, propertyLabel) + "data";

            w.write(", " + fullAttributeName + "." + propertyLabel + " AS "
                    + propertyLabel);
        }
        w.writeln();
        w.write("FROM ids");
        first = true;
        for (OntologyGraphEdge property : localProperties) {
            String propertyLabel = property.getLabel();
            String fullTableName = fullTableName(propertySchema, tableName)
                    + firstToUpperCase(propertyLabel) + "data";
            w.writeln(" LEFT OUTER JOIN");

            w.write(fullTableName);
            w.write(" ON " + "ids.x = " + fullTableName + ".x");
            first = false;
        }
        w.writeln(";");
        w.write();

        w.writeln("DROP MATERIALIZED VIEW ids;");
        w.write();

    }

    private String fullAttributeName(String schema, String tableName,
            String propertyLabel) {
        return propertySchema + "." + tableName + propertyLabel;
    }

    private String fullTableName(String schema, String tableName) {
        return propertySchema + "." + tableName;
    }

    private void addFieldToTable(BufferedFileWriter w, OntologyGraphNode object,
            OntologyGraphEdge edge, Iterator<OntologyGraphEdge> it,
            String tableName, String foreignTableName, List<String> foreignKeys,
            Set<String> propertiesInOBDA)
            throws IOException {
        String attrName = edge.getLabel();
        if (!propertiesInOBDA.contains(attrName)) {
            return;
        }
        w.write(",");
        if (object.isClass()) {
            w.writeln();
            w.write("\t" + attrName + " INTEGER");

            //w.write(" -- REFERENCES " + foreignTableName + "(id)");
            foreignKeys.add(getForeignKey(schemaName, tableName,
                    attrName, foreignTableName, "id"));
            addAttrToSchema(schemaName, tableName,
                    attrName, edge);
            addTable(edge, schemaName, tableName, attrName);

        } else if (object.isDataType()) {
            w.writeln();
            w.write("\t" + attrName + " "
                    + edge.getNextNode().getSQLDatatype());
            addAttrToSchema(schemaName, tableName,
                    attrName, edge);
            addTable(edge, schemaName, tableName, attrName);
        }
    }

    private void createBinaryTables(BufferedFileWriter w,
            Set<OntologyGraphEdge> binary, OntologyGraphNode clss,
            String tableName, List<String> foreignKeys) throws IOException {
        for (OntologyGraphEdge edge : binary) {
            OntologyGraphNode object = edge.getNextNode();
            String subjectName = clss.getLabel().replaceAll("[-.]", "");
            String objectName
                    = object.getLabel().replaceAll("[-.]", "");
            String propertyLabel = edge.getLabel();
            //objectName = objectName;
            String propertyName = firstToUpperCase(propertyLabel);
            String binaryTableName = subjectName + propertyName;
            String binaryTableFullName = schemaName + "."
                    + binaryTableName;
            w.writeln();
            w.writeln("CREATE TABLE " + binaryTableFullName + "(");
            addTable(edge, schemaName, binaryTableName, propertyLabel);

            if (object.isClass()) {
                w.writeln("\tid" + " INTEGER, -- REFERENCES "
                        + subjectName + "(id)" + ",");
                w.writeln("\t" + propertyLabel
                        + " INTEGER -- REFERENCES "
                        + objectName + "(id)");
                w.writeln(");");

                OntologyGraphEdge inverse = edge.getInverseEdge();
                if (inverse != null) {
                    String inversePropertyName
                            = firstToUpperCase(inverse.getLabel());
                    addAttrToSchema(schemaName, binaryTableName,
                            inversePropertyName, inverse);
                    addTable(inverse, schemaName, binaryTableFullName,
                            subjectName);
                }
                addAttrToSchema(schemaName, binaryTableName,
                        propertyName, edge);

                foreignKeys.add(getForeignKey(schemaName, binaryTableName,
                        propertyName, objectName, "id"));
            } else if (object.isDataType()) {
                //w.writeln("\t" + subjectName + " "
                //        + clss.getSQLDatatype() + ",");
                w.writeln("\tid" + " INTEGER, -- REFERENCES " + subjectName
                        + "(id)" + ",");
                w.writeln("\t" + propertyLabel + " "
                        + object.getSQLDatatype());
                w.writeln(");");
            }

            foreignKeys.add(getForeignKey(schemaName,
                    binaryTableName, subjectName,
                    subjectName, "id"));

            w.writeln();
            w.writeln("ALTER TABLE ONLY " + binaryTableFullName);
            w.writeln("\tADD CONSTRAINT " + subjectName.toLowerCase()
                    + "_" + propertyName.toLowerCase() + "_pkey"
                    + " PRIMARY KEY (id, "
                    + propertyLabel + ");");

            w.writeln();
            w.writeln("INSERT INTO " + binaryTableFullName + "(id, "
                    + propertyLabel + ")");
            //w.writeln("SELECT x, " + propertySchema + "." + binaryTableName +
            //        "." + propertyLabel + " AS " + propertyLabel);
            w.writeln("SELECT DISTINCT ON (x, " + propertyLabel + ") *");
            w.writeln("FROM " + propertySchema + "." + binaryTableName
                    + "data" + ";");
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
                + attr.toLowerCase() + "_reference"
                + " FOREIGN KEY (" + attr + ") REFERENCES " + schemaName
                + "." + foreignTable + "(" + foreignAttr + ");";
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
        uriToGraphObjectMap.put(uri, node);

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
    
    public void addTriple(IRI subject, IRI predicate, IRI object) {
        //OWLRDFConsumer consumer = new OWLRDFConsumer(ontology, ontology.getOWLOntology().getC)
        OWLRDFConsumer consumer = new OWLRDFConsumer(ontology.getOWLOntology(),
                (AnonymousNodeChecker) new TurtleParser((Reader) null),
                new OWLOntologyLoaderConfiguration() );
        consumer.addTriple(subject, predicate, object);
    }
    
    public void addTriple(IRI subject, IRI predicate, OWLLiteral con) {
        
    }

    public void build(Ontology ontology) {
        System.out.println("Building...");
        this.ontology = ontology;
        noOfedges = 0;
        Set<OntologyClass> classesSet = ontology.getClasses();
        Set<OntologyDatatype> datatypeSet = ontology.getDatatypes();
        Set<OntologyProperty> properties = ontology.getProperties();
        Set<OntologyIndividual> individualsSet
                = new HashSet<OntologyIndividual>();

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

        uriToGraphObjectMap
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
            uriToGraphObjectMap.put(uri, node);
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
                    if (clss.edgeExists(edge)
                            || edge.getNextNode().equals(clss)) {
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
        List<OntologyGraphEdge> edgesToRemove
                = new ArrayList<OntologyGraphEdge>();
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
            uriToGraphObjectMap.put(uri, edge);
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
                    clss.addConnection(property,
                            objectClasses.iterator().next());
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

            int numberOfNodes = classes.size() + datatypes.size()
                    + edges.size();

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
                    adjacencyArray[predicateNumber].put(objectNumber,
                            newWeight);
                    adjacencyArray[objectNumber].put(predicateNumber,
                            newWeight);
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

    private List<Pair<String, String>>
            labelsAndDatatypes(OntologyGraphNode node, Set<OntologyGraphObject> set) {
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

                OntologyGraphObject node = uriToGraphObjectMap.get(uri);
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
