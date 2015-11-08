package eg.edu.alexu.ehr.ontology;
import java.io.IOException;
import eg.edu.alexu.ehr.ontology.api.wrapper.Ontology;
import eg.edu.alexu.ehr.ontology.graph.OntologyGraph;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class Main {
	public static void main(String []args) throws OWLOntologyCreationException, IOException {
		//File file = new File("pc.rdf");
		/*File file = new File("movieontology-instances.owl");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);*/
		/*Set<OWLClass> classSet = ontology.getClassesInSignature();
		OWLClass []owlclasses = new OWLClass[classSet.size()];
		int index = 0;
		for (OWLClass c : classSet) {
			owlclasses[index++] = c;
			//Set<OWLIndividual> instances = ((OWLClass)c).getIndividuals(ontology);
			//for (OWLIndividual instance : instances)
				//System.out.println(c + " --> " + instance);
			System.out.println(c);
		}
		
		System.out.println();
		
		Set<OWLDataProperty> dataPropertySet = ontology.getDataPropertiesInSignature();//owlclasses[55].getDataPropertiesInSignature();
		Set<OWLObjectProperty> objectPropertySet = ontology.getObjectPropertiesInSignature();//owlclasses[55].getObjectPropertiesInSignature();
		
		System.out.println(dataPropertySet.size());
		System.out.println(objectPropertySet.size());
		for (OWLDataProperty p : dataPropertySet) {
			System.out.println(p + ": " + p.getDomains(ontology).size() + " - " + p.getRanges(ontology).size());
		}
		for (OWLObjectProperty p : objectPropertySet) {
			System.out.println(p + ": " + p.getDomains(ontology).size() + " - " + p.getRanges(ontology).size());
		}*/
		
		//Ontology ontology = new Ontology("FlyAtlas-Vocabulary.owl");
		//Ontology ontology = new Ontology("pc.rdf");
                Ontology ontology = new Ontology("test.rdf");

		//Ontology ontology = new Ontology("movieontology-instances (small).owl");
                //Ontology ontology = new Ontology("countries.owl");
                //Ontology ontology = new Ontology("countries2.owl");
             //Axiom.processAxioms(ontology.getOWLOntology());
             // Axiom.processAnnotation(ontology.getOWLOntology());
                //Axiom.processProperties(ontology.getOWLOntology());
                OntologyGraph graph = new OntologyGraph(ontology);

           //     System.err.println("LLLLLLLLLLLLLLLLLL");
           //     Axiom.processClasses(ontology.getOWLOntology(),graph);
                // OntologyGraph graph = new OntologyGraph(ontology);
               // graph.iterate();
             //   graph.save("Edges.txt");
             //   graph.saveAsGraph();
               // int noOfPartitions = 5;
                //Process p = Runtime.getRuntime().exec("gpmetis graph.txt " + noOfPartitions);
              //  gpmetis("graph.txt", noOfPartitions);
              //  graph.generateSchema(noOfPartitions);
		//System.out.println();
		//System.out.println(graph);
		//graph.save("Graph Edges.txt");
		
	}

        public static void gpmetis(String path, int noOfPartitions) throws IOException {
            Runtime.getRuntime().exec("gpmetis " + path + " " + noOfPartitions);
        }

        public static void gpmetis(String path) throws IOException {
            gpmetis(path, 10);
        }
}