package devops;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Main {
    public static void main(String[] args) {
        jade.core.Runtime rt = jade.core.Runtime.instance();
        Profile p = new ProfileImpl();
        AgentContainer mainContainer = rt.createMainContainer(p);
        try {
            AgentController inputAgentController = mainContainer.createNewAgent("InputAgent", "devops.InputAgent", null);
            inputAgentController.start();

            AgentController outputAgentController = mainContainer.createNewAgent("OutputAgent", "devops.OutputAgent", null);
            outputAgentController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
