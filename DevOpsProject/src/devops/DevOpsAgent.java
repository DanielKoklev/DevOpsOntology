package devops;

import java.util.List;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class DevOpsAgent extends Agent{
	
	private DevOpsOntology devOpsOntology;
	private DevOpsAgentGUI gui;

	public void addToolToPractice(String practiceName, String toolName) {
		devOpsOntology.addToolToPractice(practiceName, toolName);
	}

	public void removeToolFromPractice(String practiceName, String toolName) {
		devOpsOntology.removeToolFromPractice(practiceName, toolName);
		
	}

	public void changeToolName(String oldName, String newName) {
		devOpsOntology.renameTool(oldName, newName);
	}

	public void deleteTool(String name) {
		devOpsOntology.deleteTool(name);
	}
	
	@Override
	protected void setup() {
		try {
			devOpsOntology = new DevOpsOntology();

			List<Practice> specificPractice = devOpsOntology.getPracticeByTool("Docker");

			gui = new DevOpsAgentGUI(this, specificPractice);

			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();

			dfd.setName(getAID());
			sd.setType("DevOps Service");

			dfd.addServices(sd);

			try {
				DFService.register(this, dfd);
			} catch (FIPAException e) {
				e.printStackTrace();
			}

			addBehaviour(new DevOpsServiceBehaviour(devOpsOntology, this));

		} catch (OWLOntologyCreationException e) {
			System.err.println("Failed to initialize DevOpsOntology.");
			e.printStackTrace();
			doDelete();
		}
	}

	

}
