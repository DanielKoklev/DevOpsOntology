package devops;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;

public class DevOpsAgentGUI extends JFrame {

	DevOpsAgent agent;

	List<Practice> services;
	JTextArea area;

	public DevOpsAgentGUI(DevOpsAgent agent, List<Practice> list) {
		this.agent = agent;
		services = list;
		init();

		addItemsForSell();
	}

	private void addItemsForSell() {
		StringBuilder sb = new StringBuilder();
		for(Practice currentPractice : services) {
			sb.append(currentPractice + "\n");
		}

		area.setText(sb.toString());
	}

	private void init() {

		JButton addToolButton = new JButton("Add Tool");
		JButton removeToolButton = new JButton("Remove Tool From Practice");
		JButton editToolButton = new JButton("Edit Tool");
		JButton deleteToolButton = new JButton("Delete existing tool");

		JTextField addToolPracticeNameTextField = new JTextField(15);
		JTextField addToolNameTextField = new JTextField(15);
		JTextField removeToolPracticeNameTextField = new JTextField(15);
		JTextField removeToolNameTextField = new JTextField(15);
		JTextField oldNameTool = new JTextField(15);
		JTextField newNameTool = new JTextField(15);
		JTextField deleteToolNameTextField = new JTextField(15);

		JPanel panel = new JPanel(new GridLayout(6, 1));

		JPanel row1 = new JPanel();
		row1.add(new JLabel("Practice:"));
		row1.add(addToolPracticeNameTextField);
		row1.add(new JLabel("Tool:"));
		row1.add(addToolNameTextField);
		row1.add(addToolButton);
		panel.add(row1);

		JPanel row2 = new JPanel();
		row2.add(new JLabel("Practice:"));
		row2.add(removeToolPracticeNameTextField);
		row2.add(new JLabel("Tool:"));
		row2.add(removeToolNameTextField);
		row2.add(removeToolButton);
		panel.add(row2);

		JPanel row3 = new JPanel();
		row3.add(new JLabel("Old Name:"));
		row3.add(oldNameTool);
		row3.add(new JLabel("New Name:"));
		row3.add(newNameTool);
		row3.add(editToolButton);
		panel.add(row3);

		JPanel row4 = new JPanel();
		row4.add(new JLabel("Tool Name:"));
		row4.add(deleteToolNameTextField);
		row4.add(deleteToolButton);
		panel.add(row4);

		add(panel);

		addToolButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String practiceName = addToolPracticeNameTextField.getText();
				String toolName = addToolNameTextField.getText();

				if (practiceName.length() > 0 && toolName.length() > 0) {
					agent.addToolToPractice(practiceName, toolName);
					addToolPracticeNameTextField.setText("");
					addToolNameTextField.setText("");
				}
			}
		});

		removeToolButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String practiceName = removeToolPracticeNameTextField.getText();
				String toolName = removeToolNameTextField.getText();

				if (practiceName.length() > 0 && toolName.length() > 0) {
					agent.removeToolFromPractice(practiceName, toolName);
					removeToolPracticeNameTextField.setText("");
					removeToolNameTextField.setText("");
				}
			}
		});

		editToolButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String oldName = oldNameTool.getText();
				String newName = newNameTool.getText();

				if (oldName.length() > 0 && newName.length() > 0) {
					agent.changeToolName(oldName, newName);
					oldNameTool.setText("");
					newNameTool.setText("");
				}
			}
		});

		deleteToolButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = deleteToolNameTextField.getText();
				if (name.length() > 0) {
					agent.deleteTool(name);
					deleteToolNameTextField.setText("");
				}
			}
		});

		setSize(800, 400);
		setVisible(true);
	}

}
