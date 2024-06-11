package devops;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InputAgent extends Agent {
    private JTextField toolTextField;
    private JButton searchButton;

    @Override
    protected void setup() {
        JFrame frame = new JFrame("Input Agent");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        toolTextField = new JTextField(20);
        searchButton = new JButton("Search");

        panel.add(new JLabel("Tool Name:"));
        panel.add(toolTextField);
        panel.add(searchButton);

        frame.add(panel);
        frame.setVisible(true);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String toolName = toolTextField.getText().trim();
                addBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.addReceiver(new AID("OutputAgent", AID.ISLOCALNAME));
                        msg.setContent(toolName);
                        send(msg);
                        System.out.println("InputAgent sent request for tool: " + toolName);
                    }
                });
            }
        });
    }
}
