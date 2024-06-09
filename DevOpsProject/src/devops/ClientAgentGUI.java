package devops;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ClientAgentGUI extends JFrame {

	private ClientAgent agent;
	private JTextField toolTF;
	private JButton searchB;

	public ClientAgentGUI(ClientAgent agent) {
		this.agent = agent;
		init();
	}

	private void init() {
		toolTF = new JTextField(20);
		toolTF.setToolTipText("Enter the name of the tool, technology, or ideology you want to learn about.");

		searchB = new JButton("I want to learn this!");
		searchB.addActionListener(clickListener);
		searchB.setToolTipText("Click to search for learning materials.");

		JLabel toolLabel = new JLabel("Tool/Technology/Ideology:");

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		inputPanel.add(toolLabel);
		inputPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		inputPanel.add(toolTF);
		inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		inputPanel.add(searchB);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		contentPanel.add(inputPanel);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

		add(contentPanel);

		setTitle("Client Agent - Learning Assistance");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private ActionListener clickListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String toolName = toolTF.getText();

			if (toolName.length() > 0) {
				agent.setSearchedTool(toolName);
				toolTF.setText("");
			}
		}
	};
}
