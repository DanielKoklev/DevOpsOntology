package devops;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DevOpsGUI extends JFrame {
	private JTextField toolTextField;
	private JButton searchButton;
	private JTextArea resultTextArea;

	private AgentController agentController;
	private AgentContainer mainContainer;
	private DevOpsGUIAgent guiAgent;

	public DevOpsGUI() {
		setTitle("DevOps Tool Search");
		setSize(400, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		toolTextField = new JTextField(20);
		searchButton = new JButton("Search");
		resultTextArea = new JTextArea(10, 30);
		resultTextArea.setEditable(false);

		panel.add(new JLabel("Tool Name:"));
		panel.add(toolTextField);
		panel.add(searchButton);
		panel.add(new JScrollPane(resultTextArea));

		add(panel);

		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String toolName = toolTextField.getText().trim();
				searchTool(toolName);
			}
		});

		jade.core.Runtime rt = jade.core.Runtime.instance();
		jade.core.Profile p = new jade.core.ProfileImpl();
		mainContainer = rt.createMainContainer(p);
		try {
			agentController = mainContainer.createNewAgent("DevOpsAgent", "devops.DevOpsAgent", null);
			agentController.start();

			guiAgent = new DevOpsGUIAgent();
			mainContainer.acceptNewAgent("DevOpsGUIAgent", guiAgent).start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}

	private void searchTool(String toolName) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(new AID("DevOpsAgent", AID.ISLOCALNAME));
		msg.setContent(toolName);

		guiAgent.send(msg);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			DevOpsGUI gui = new DevOpsGUI();
			gui.setVisible(true);
		});
	}

	private class DevOpsGUIAgent extends Agent {
		@Override
		protected void setup() {
			addBehaviour(new CyclicBehaviour() {
				@Override
				public void action() {
					ACLMessage msg = receive();
					if (msg != null) {
						String result = msg.getContent();
						SwingUtilities.invokeLater(() -> resultTextArea.setText(result));
					} else {
						block();
					}
				}
			});
		}
	}
}
