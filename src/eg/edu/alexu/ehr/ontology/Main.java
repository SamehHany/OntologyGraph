package eg.edu.alexu.ehr.ontology;

import java.io.IOException;
import eg.edu.alexu.ehr.ontology.api.wrapper.Ontology;
import eg.edu.alexu.ehr.ontology.graph.OntologyGraph;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import static eg.edu.alexu.ehr.ontology.Axiom.*;
import eg.edu.alexu.ehr.query.Plan;

public class Main {

    public static void main(String[] args) throws OWLOntologyCreationException, IOException {
	//Ontology ontology = new Ontology("FlyAtlas-Vocabulary.owl");
        //Ontology ontology = new Ontology("pc.rdf");
        //Ontology ontology = new Ontology("test.rdf");
        //Ontology ontology = new Ontology("test.rdf");
        //Ontology ontology = new Ontology("dbpedia_2015-04.owl");

        //Ontology ontology = new Ontology("movieontology-instances (small).owl");
        //Ontology ontology = new Ontology("movieontology-instances.owl");
        /*Ontology ontology = new Ontology("wine.rdf");
        //Ontology ontology = new Ontology("countries.owl");
        //Ontology ontology = new Ontology("countries2.owl");

        OntologyGraph graph = new OntologyGraph(ontology);
        //graph.toTable("test/graph.sql");
        //graph.save("test/Edges.txt");
        graph.toTable("graph.sql");
        graph.save("Edges.txt");*/
        
        Plan plan = new Plan("query-plans/get-all-movie-info.json");
        //plan.build("query-plans/get-all-movie-info.json");
        int x = 0;
        
    }

    public static void gpmetis(String path, int noOfPartitions) throws IOException {
        Runtime.getRuntime().exec("gpmetis " + path + " " + noOfPartitions);
    }

    public static void gpmetis(String path) throws IOException {
        gpmetis(path, 10);
    }
}
