package eg.edu.alexu.ehr.ontology;

import java.io.IOException;
import eg.edu.alexu.ehr.ontology.api.wrapper.Ontology;
import eg.edu.alexu.ehr.ontology.graph.OntologyGraph;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import static eg.edu.alexu.ehr.ontology.Axiom.*;
import eg.edu.alexu.ehr.query.Plan;
import eg.edu.alexu.ehr.util.db.Database;
import eg.edu.alexu.ehr.util.io.BufferedFileReader;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;

public class Main {

    public static void main(String[] args) throws OWLOntologyCreationException, IOException, URISyntaxException {
        //String inputfile = "movieontology-instances.owl";
        //String inputfile = args[0];
        List<String> inputfiles = Arrays.asList(args);
        //String inputfile = "wine.rdf";
        //String inputfile = "FlyAtlas-Vocabulary.owl";
        //String inputfile = "countries.owl";
	//Ontology ontology = new Ontology("FlyAtlas-Vocabulary.owl");
        //Ontology ontology = new Ontology("pc.rdf");
        //Ontology ontology = new Ontology("test.rdf");
        //Ontology ontology = new Ontology("test.rdf");
        //Ontology ontology = new Ontology("dbpedia_2015-04.owl");

        //Ontology ontology = new Ontology("movieontology-instances (small).owl");
        // temp comment: Ontology ontology = new Ontology(inputfile);
        
        //Ontology ontology = new Ontology("wine.rdf");
        //Ontology ontology = new Ontology("countries.owl");
        //Ontology ontology = new Ontology("countries2.owl");

        
        /*for (OWLOntology o :ontology.getOWLOntology().getDirectImports()) {
            System.out.println(o);
        }*/
        
        //Ontology ontology = new Ontology(inputfile);
        Ontology ontology = new Ontology(inputfiles);
        OntologyGraph graph = new OntologyGraph(ontology);
//        graph.addTriple(IRI.create(new URI("http://www.movieontology.org/2009/11/09/movieontology.owl#movie1")),
//                IRI.create(new URI("http://www.movieontology.org/2009/10/01/movieontology.owl#hasEditor")),
//                IRI.create(new URI("http://www.movieontology.org/2009/10/01/movieontology.owl#editor1")));
//        graph.build(ontology);
        //graph.toTable("test/graph.sql");
        //graph.save("test/Edges.txt");
        graph.toSchema("graph.sql", "movieontology.obda");
        graph.save("Edges.txt");
        String sql = graph.populate("populate.sql");
        //System.out.println("INSERT SQL:");
        System.out.println(sql);
        //graph.generateOBDAFile(inputfile);
        //System.out.println(graph.replicateAllAttributesInSubclass());
        
        /*String sql = OntologyGraph.replicateAttributeInTable("ontology",
                "PersonBirthName", "Editor", "id", "id", "birthName",
                "birthName", "text");
        System.out.println(sql);*/
        
        // Database
        /*String url = "jdbc:postgresql://localhost:5432/postgres";
        String username = "sameh";
        Database database = new Database(url, username);
        //database.connect(url, username);
        database.executeQuery("select * " +
                "from title where title = 'Braveheart';");
        database.executeQuery("select * " +
                "from title where title = 'X-Men';");
        
        //database.getTables("public");
        database.getAndSaveStats("public");*/
        
        //graph.getData("http://www.movieontology.org/" +
                //"2009/10/01/movieontology.owl#Movie", "movieontology.obda",
                //inputfile);
        /*long start = System.currentTimeMillis();
        graph.getAllData("movieontology.obda");//, inputfile);
        long end = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (end - start));*/
        //graph.insertIntoPropertyTables("property-data.sql");
        
        
        //graph.generateSparqlQueries("movieontology.obda");
        
        //System.out.println("\n Query:");
        //System.out.println(query);
        
        //Plan plan = new Plan("query-plans/get-all-movie-info.json");
        //plan.build("query-plans/get-all-movie-info.json");
        //int x = 0;
        
    }

    public static void gpmetis(String path, int noOfPartitions) throws IOException {
        Runtime.getRuntime().exec("gpmetis " + path + " " + noOfPartitions);
    }

    public static void gpmetis(String path) throws IOException {
        gpmetis(path, 10);
    }
    
    public static void ontop(String obdaPath, String owlPath, String queryPath,
            String outPath)
            throws IOException {
        Runtime.getRuntime().exec("./ontop/ontop -m " + obdaPath + " -t " +
                owlPath + " -q " + queryPath + " -o " + outPath);
    }
}
