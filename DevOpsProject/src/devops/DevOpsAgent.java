package devops;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DevOpsAgent extends Agent {

	private static class ToolInfo {
		String type;
		String url;

		ToolInfo(String type, String url) {
			this.type = type;
			this.url = url;
		}

		@Override
		public String toString() {
			return "Type: " + type + ", URL: " + url;
		}
	}

	private Map<String, ToolInfo> tools;

	@Override
	protected void setup() {
		System.out.println("Agent " + getLocalName() + " started.");

		tools = new HashMap<>();
		loadOntology();

		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null) {
					String toolName = msg.getContent();
					String result = queryTool(toolName);

					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent(result);
					myAgent.send(reply);
				} else {
					block();
				}
			}
		});
	}

	private void loadOntology() {
		try {
			File file = new File("/Users/dkoklev/uni/DevOpsOntology/DevOpsProject/src/devops/devops_ontology_test.owl");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();

			// Parse NamedIndividuals
			NodeList declarationNodes = doc.getElementsByTagName("Declaration");
			for (int i = 0; i < declarationNodes.getLength(); i++) {
				Element declarationElement = (Element) declarationNodes.item(i);
				NodeList namedIndividualNodes = declarationElement.getElementsByTagName("NamedIndividual");
				if (namedIndividualNodes.getLength() > 0) {
					String toolName = namedIndividualNodes.item(0).getAttributes().getNamedItem("IRI").getTextContent().replace("#", "");
					tools.put(toolName, new ToolInfo("Unknown", "Unknown"));
				}
			}

			NodeList classAssertionNodes = doc.getElementsByTagName("ClassAssertion");
			for (int i = 0; i < classAssertionNodes.getLength(); i++) {
				Element classAssertionElement = (Element) classAssertionNodes.item(i);
				String toolName = null;
				String toolType = null;
				NodeList childNodes = classAssertionElement.getChildNodes();
				for (int j = 0; j < childNodes.getLength(); j++) {
					Node childNode = childNodes.item(j);
					if (childNode.getNodeType() == Node.ELEMENT_NODE) {
						Element childElement = (Element) childNode;
						if (childElement.getTagName().equals("NamedIndividual")) {
							toolName = childElement.getAttribute("IRI").replace("#", "");
						} else if (childElement.getTagName().equals("Class")) {
							toolType = childElement.getAttribute("IRI").replace("#", "");
						}
					}
				}
				if (toolName != null && toolType != null) {
					ToolInfo toolInfo = tools.get(toolName);
					if (toolInfo != null) {
						toolInfo.type = toolType;
					}
				}
			}

			NodeList annotationAssertionNodes = doc.getElementsByTagName("AnnotationAssertion");
			for (int i = 0; i < annotationAssertionNodes.getLength(); i++) {
				Element annotationAssertionElement = (Element) annotationAssertionNodes.item(i);
				String toolName = null;
				String url = null;
				NodeList childNodes = annotationAssertionElement.getChildNodes();
				for (int j = 0; j < childNodes.getLength(); j++) {
					Node childNode = childNodes.item(j);
					if (childNode.getNodeType() == Node.ELEMENT_NODE) {
						Element childElement = (Element) childNode;
						if (childElement.getTagName().equals("IRI")) {
							toolName = childElement.getTextContent().replace("#", "");
						} else if (childElement.getTagName().equals("Literal")) {
							url = childElement.getTextContent();
						}
					}
				}
				if (toolName != null && url != null) {
					ToolInfo toolInfo = tools.get(toolName);
					if (toolInfo != null) {
						toolInfo.url = url;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String queryTool(String toolName) {
		ToolInfo toolInfo = tools.get(toolName);
		return (toolInfo != null) ? toolInfo.toString() : "Tool not found";
	}
}
