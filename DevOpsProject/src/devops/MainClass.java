package devops;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.core.Profile;

public class MainClass {

	public static void main(String[] args) {
		Runtime runtime = Runtime.instance();
		
		Profile profile = new ProfileImpl();
		
		profile.setParameter(Profile.MAIN_HOST, "localhost");
		profile.setParameter(Profile.MAIN_PORT, "1099");
		profile.setParameter(Profile.GUI, "true");
		
		AgentContainer mainContainer = runtime.createMainContainer(profile);
		
		try {
			System.out.println("Current working directory: " + System.getProperty("user.dir"));
			AgentController devopsService = mainContainer.createNewAgent(
					"DevOps Service", DevOpsAgent.class.getName(), null);
			AgentController client = mainContainer.createNewAgent("Client",
					ClientAgent.class.getName(), null);

			devopsService.start();
			client.start();
			
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
		
		
		
	}

}
