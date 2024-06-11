package devops;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class OutputAgent extends Agent {
	private JTextArea resultTextArea;
	private Map<String, ToolInfo> toolInfoMap;

	private static class ToolInfo {
		String type;
		String url;

		ToolInfo(String type, String url) {
			this.type = type;
			this.url = url;
		}

		@Override
		public String toString() {
			return "Type: " + type + "\nURL: " + url;
		}
	}

	@Override
	protected void setup() {
		JFrame frame = new JFrame("Output Agent");
		frame.setSize(400, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		resultTextArea = new JTextArea(10, 30);
		resultTextArea.setEditable(false);

		panel.add(new JScrollPane(resultTextArea));
		frame.add(panel);
		frame.setVisible(true);

		toolInfoMap = new HashMap<>();
		loadOntology();

		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
				ACLMessage msg = receive(mt);
				if (msg != null) {
					String toolName = msg.getContent();
					System.out.println("OutputAgent received request for tool: " + toolName);

					ToolInfo toolInfo = toolInfoMap.get(toolName);
					if (toolInfo != null) {
						resultTextArea.setText("Tool: " + toolName + "\n" + toolInfo);
						System.out.println("OutputAgent displayed info for tool: " + toolName);
					} else {
						resultTextArea.setText("Tool: " + toolName + " not found in the ontology.");
						System.out.println("OutputAgent: Tool not found in the ontology.");
					}
				} else {
					block();
				}
			}
		});
	}

	private void loadOntology() {
		try {
			File file = new File("/Users/dkoklev/uni/DevOpsOntology/DevOpsProject/src/devops/devops_ontology_test.owl"); // Update this path to your ontology file
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
			System.out.println("Loaded ontology: " + ontology);

			for (OWLNamedIndividual individual : ontology.getIndividualsInSignature()) {
				String toolName = individual.getIRI().getFragment();
				String toolType = null;
				String toolUrl = null;

				// Extracting the type from class assertions
				for (OWLClassExpression classExpression : individual.getTypes(ontology)) {
					if (!classExpression.isAnonymous()) {
						toolType = classExpression.asOWLClass().getIRI().getFragment();
					}
				}

				// Extracting URL from annotations
				for (OWLAnnotationAssertionAxiom annotation : ontology.getAnnotationAssertionAxioms(individual.getIRI())) {
					OWLAnnotationProperty property = annotation.getProperty();
					if (property.getIRI().getFragment().equalsIgnoreCase("hasURL")) {
						toolUrl = ((OWLLiteral) annotation.getValue()).getLiteral();
					}
				}

				if (toolType != null && toolUrl != null) {
					toolInfoMap.put(toolName, new ToolInfo(toolType, toolUrl));
					System.out.println("Loaded tool: " + toolName + " with type: " + toolType + " and URL: " + toolUrl);
				} else {
					System.out.println("Loaded tool: " + toolName + " with missing type or URL");
				}
			}
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
	}
}
