package devops;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ClientAgent extends Agent{

	private ClientAgentGUI gui;
	private String searchedTool;
	private List<AID> devopsService;
	
	@Override
	protected void setup() {

		gui = new ClientAgentGUI(this);
		gui.setTitle(this.getLocalName());
		
	}
	
	private OneShotBehaviour theDevOpsService = new OneShotBehaviour() {
		
		@Override
		public void action() {
			System.out.println("Welcome to our service! Do you need materials for " +
					searchedTool + "?");
			
			DFAgentDescription agentDesc = new DFAgentDescription();
			ServiceDescription servD = new ServiceDescription();
			
			servD.setType("DevOps Service");
			
			agentDesc.addServices(servD);
			
			try {
				DFAgentDescription[] descriptions = DFService.search(myAgent, agentDesc);
				devopsService = new ArrayList<AID>();
				
				for(int i = 0; i < descriptions.length; i++) {
					devopsService.add(descriptions[i].getName());
				}
			
			} catch (FIPAException e) {
				System.out.println("Error: " + e.getMessage());
			}
			
			if(devopsService.size() > 0) {
				addBehaviour(new HungryBehaviour());
			}else {
				System.out.println("Sorry we do not provide learning materials for the requested tool!");
			}
		}
	};
	

	public String getSearchedTool() {
		return searchedTool;
	}

	public void setSearchedTool(String searchedTool) {
		this.searchedTool = searchedTool;
		
		if(searchedTool != "") {
			addBehaviour(theDevOpsService);
		}
	}
	
	
	private class HungryBehaviour extends Behaviour{

		int step = 0;
		MessageTemplate mt;
		HashMap<AID, Practice[]> practices = new HashMap<AID, Practice[]>();
		int replyCounter = 0;
		
		@Override
		public void action() {
			
			switch(step) {
				case 0:
					
					ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
					
					for(int i = 0; i < devopsService.size(); i++) {
						cfp.addReceiver(devopsService.get(i));
					}
					
					cfp.setContent(searchedTool);
					cfp.setConversationId("DevOps");
					cfp.setReplyWith("cfp" + System.currentTimeMillis());
					
					mt = MessageTemplate.and(
							MessageTemplate.MatchConversationId("DevOps"),
							MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
					
					send(cfp);
					step++;
					
				break;
				case 1:
					
					ACLMessage reply = receive(mt);
					
					if(reply != null) {
						
						if(reply.getPerformative() == ACLMessage.PROPOSE) {
							
							ObjectMapper mapper = new ObjectMapper();
							
							try {
								practices.put(reply.getSender(),
										mapper.readValue(reply.getContent(), Practice[].class));
								
								for(int i = 0; i < practices.get(reply.getSender()).length; i++) {
									System.out.println(practices.get(reply.getSender())[i]);
								}
								
							} catch (IOException e) {
								e.printStackTrace();
							}							
							
						}else {
							System.out.println(reply.getSender() + ": We do not provide such practices!");
						}
						
						replyCounter++;
						
						if(replyCounter == devopsService.size()) {
							step++;
						}
					}
					
				break;
				
				case 2:
					step++;
				break;

			}
			
		}

		@Override
		public boolean done() {
			if(step == 3) {
				doDelete();
				return true;
			}else if(practices.keySet().size() == 0 && replyCounter == devopsService.size()) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				addBehaviour(theDevOpsService);
				return true;
			}			
			
			return false;
		}
		
	}
	
	
}
