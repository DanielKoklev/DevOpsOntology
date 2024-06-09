package devops;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class DevOpsServiceBehaviour extends CyclicBehaviour {
	
	DevOpsOntology devOpsOntology;
	
	
	public DevOpsServiceBehaviour(DevOpsOntology devOpsOntology, Agent agent) {
		this.devOpsOntology = devOpsOntology;
		myAgent = agent;
	}

	@Override
	public void action() {
		
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
		
		ACLMessage msg = myAgent.receive(mt);
		
		if(msg != null) {
			
			String tool = msg.getContent();
			
			ACLMessage reply = msg.createReply();
			
			System.out.println("We are searching for " + tool);
			ArrayList<Practice> result = devOpsOntology.getPracticeByTool(tool);
			
			System.out.println(result.size() + "practices found containing this tool.");
			if(result.size() > 0) {
				System.out.println("We provide service for this tool!");
				
				reply.setPerformative(ACLMessage.PROPOSE);
				
				ObjectMapper mapper = new ObjectMapper();
				
				try {
					reply.setContent(mapper.writeValueAsString(result));
					
					reply.setLanguage("JSON");
					
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}				
			}else {
				reply.setPerformative(ACLMessage.INFORM_REF);
				reply.setContent("We are sorry to inform you, that we do not provide service for this tool.");
			}
			
			myAgent.send(reply);
			
		}
		

	}

}
