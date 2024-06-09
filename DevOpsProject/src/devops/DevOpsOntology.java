package devops;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

public class DevOpsOntology {

	private OWLOntologyManager ontoManager;
	private OWLOntology devOpsOntology;
	private OWLDataFactory dataFactory;
	private OWLReasoner reasoner;

	private String ontologyIRIStr;
	private boolean contains = false;

	public DevOpsOntology() throws OWLOntologyCreationException {
		ontoManager = OWLManager.createOWLOntologyManager();
		dataFactory = ontoManager.getOWLDataFactory();

		loadOntologyFromFile();

		if (devOpsOntology == null) {
			throw new OWLOntologyCreationException("Ontology could not be loaded.");
		}

		ontologyIRIStr = devOpsOntology.getOntologyID()
				.getOntologyIRI().toString() + "#";

	}

	private void loadOntologyFromFile() {
		File ontoFile = new File("/Users/danielkoklev/IdeaProjects/DevOpsOntology/DevOpsProject/src/devops/devops-onto.owl");
		System.out.println("Loading ontology from file: " + ontoFile.getAbsolutePath());

		if (!ontoFile.exists()) {
			System.err.println("Ontology file does not exist: " + ontoFile.getAbsolutePath());
			return;
		}

		try {
			devOpsOntology = ontoManager.loadOntologyFromOntologyDocument(ontoFile);
			System.out.println("Ontology loaded successfully.");
		} catch (OWLOntologyCreationException e) {
			System.err.println("Error loading ontology from file: " + ontoFile.getAbsolutePath());
			e.printStackTrace();
		}
	}
	
	private String getClassFriendlyName(OWLClass cls) {
		
		String label = cls.getIRI().toString();
		label = label.substring(label.indexOf('#') + 1);
		
		return label;	
		
	}
	
	private boolean containsSuperClass(Set<OWLClassExpression> setOfClasses, OWLClass superClass) {
		
		for(OWLClassExpression clsExps : setOfClasses) {
			
			for(OWLClass cls : clsExps.getClassesInSignature()) {
				if(cls.getIRI().equals(superClass.getIRI())) {
					contains = true;
				}else {
					if(cls.getSubClasses(devOpsOntology).size() > 0) {
						containsSuperClass(
								cls.getSuperClasses(devOpsOntology),
								superClass);
					}
				}
			}
			
		}	
		return contains;
	}
	
	private List<String> getAllTools(OWLClass practiceClass, OWLObjectProperty hasTool) {

		List<String> tools = new ArrayList<>();
		
		for(OWLAxiom axiom : 
			practiceClass.getReferencingAxioms(devOpsOntology)) {
						
			if(axiom.getAxiomType() == AxiomType.SUBCLASS_OF) {
				
				for(OWLObjectProperty op : 
					axiom.getObjectPropertiesInSignature()) {
					
					if(op.getIRI().equals(hasTool.getIRI())) {
						
						for(OWLClass classInAxiom: 
							axiom.getClassesInSignature()) {
							
							if(!classInAxiom.getIRI().equals(practiceClass.getIRI())) {
								tools.add(getClassFriendlyName(classInAxiom));
							}
							
						}
					}
				}
			}			
		}
		
		
		return tools;
	}
	
	public ArrayList<Practice> getPracticeByTool(String tool){
		
		ArrayList<Practice> result = new ArrayList<>();
		
		OWLObjectProperty hasTool = dataFactory
				.getOWLObjectProperty(
						IRI.create(ontologyIRIStr + "hasTool"));
		
		OWLClass toolClass = dataFactory.getOWLClass(
				IRI.create(ontologyIRIStr + tool));
		

		for(OWLAxiom axiom : 
			toolClass.getReferencingAxioms(devOpsOntology)) {
			

			if(axiom.getAxiomType() == AxiomType.SUBCLASS_OF) {


				for(OWLObjectProperty op: 
					axiom.getObjectPropertiesInSignature()) {
					

					if(op.getIRI().equals(hasTool.getIRI())) {
						

						for(OWLClass classInAxiom: 
							axiom.getClassesInSignature()) {
							
							if(containsSuperClass(
									classInAxiom.getSuperClasses(devOpsOntology),
									dataFactory.getOWLClass(
											IRI.create(ontologyIRIStr + "Practice")))) {
								
								contains = false;
								
								Practice p = new Practice();
								p.setName(getClassFriendlyName(classInAxiom));
								p.setId(classInAxiom.getIRI().toQuotedString());
								
								p.setTool(getAllTools(classInAxiom
										, hasTool));
								
								result.add(p);							
							}
							
						}
					}
					
				}
				
			}
			
		}
		
		return result;
	}
	
	private void saveOntology() {
		try {
			ontoManager.saveOntology(devOpsOntology);

		} catch (OWLOntologyStorageException e) {
			System.out.println("Cannot edit the file at that moment");
			e.printStackTrace();
		}
	}

	public void createPracticeType(String practiceName) {
		OWLClass practiceClass = dataFactory.getOWLClass(
				IRI.create(ontologyIRIStr + practiceName));

		OWLClass parentClass = dataFactory.getOWLClass(
				IRI.create(ontologyIRIStr + "NamedPractice"));

		OWLSubClassOfAxiom subClassOf = dataFactory.getOWLSubClassOfAxiom(
				practiceClass, parentClass);

		AddAxiom axiom = new AddAxiom(devOpsOntology, subClassOf);
		ontoManager.applyChange(axiom);
		saveOntology();
	}

	public void addToolToPractice(String practiceName, String toolName) {

		OWLClass practiceClass = dataFactory.getOWLClass(
				IRI.create(ontologyIRIStr + practiceName));

		OWLClass toolClass = dataFactory.getOWLClass(
				IRI.create(ontologyIRIStr + toolName));

		System.out.println("Practice: " + practiceName + " Tool: " + toolName);

		OWLObjectProperty hasTool = dataFactory.getOWLObjectProperty(
				IRI.create(ontologyIRIStr + "hasTool")
				);

		OWLClassExpression classExpression = dataFactory.getOWLObjectSomeValuesFrom(
				hasTool, toolClass);

		OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(
				practiceClass, classExpression);


		AddAxiom addAxiom = new AddAxiom(devOpsOntology, axiom);

		ontoManager.applyChange(addAxiom);

		saveOntology();

	}

	public void deleteTool(String name) {
		OWLClass classToRemove = dataFactory.getOWLClass(
				IRI.create(ontologyIRIStr + name));

		OWLEntityRemover remover = new OWLEntityRemover(ontoManager,
				devOpsOntology.getImportsClosure());

		classToRemove.accept(remover);

		ontoManager.applyChanges(remover.getChanges());
		saveOntology();

	}

	public void removeToolFromPractice(String practiceName, String toolName) {

		OWLClass practiceClass = dataFactory.getOWLClass(
				IRI.create(ontologyIRIStr + practiceName));

		OWLClass toolClass = dataFactory.getOWLClass(
				IRI.create(ontologyIRIStr + toolName));

		System.out.println("Practice: " + practiceName + " Tool: " + toolName);

		OWLObjectProperty hasTool = dataFactory.getOWLObjectProperty(
				IRI.create(ontologyIRIStr + "hasTool")
				);

		OWLClassExpression classExpression = dataFactory.getOWLObjectSomeValuesFrom(
				hasTool, toolClass);

		OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(
				practiceClass, classExpression);


		RemoveAxiom removeAxiom = new RemoveAxiom(devOpsOntology, axiom);

		ontoManager.applyChange(removeAxiom);

		saveOntology();

	}

	public void renameTool(String oldName, String newName) {

		OWLEntityRenamer renamer = new OWLEntityRenamer(ontoManager,
				devOpsOntology.getImportsClosure());

		IRI newIRI = IRI.create(ontologyIRIStr + newName);
		IRI oldIRI = IRI.create(ontologyIRIStr + oldName);

		ontoManager.applyChanges(renamer.changeIRI(oldIRI, newIRI));

		saveOntology();

	}


	
	


	
	
	
	
}
