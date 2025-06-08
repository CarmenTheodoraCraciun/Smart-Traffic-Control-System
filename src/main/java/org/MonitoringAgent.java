package org;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;
import java.util.HashMap;

public class MonitoringAgent extends Agent {
    private TrafficControlGUI gui;
    private HashMap<String, String> vehicleStatus = new HashMap<>();

    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            gui = (TrafficControlGUI) args[0];
        } else {
            System.out.println("MonitoringAgent: No GUI received!");
        }

        System.out.println("Monitoring Agent started.");

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();

                    if (msg.getPerformative() == ACLMessage.REQUEST) {
                        gui.addVehicle(msg.getSender().getLocalName());
                        vehicleStatus.put(msg.getSender().getLocalName(), "Waiting");
                        System.out.println("MonitoringAgent: Vehicle " + msg.getSender().getLocalName() + " requested access.");
                    } else if (content.contains(": Crossed")) {
                        String[] parts = content.split(": ");
                        if (parts.length == 2) {
                            vehicleStatus.put(parts[0], parts[1]);
                            gui.updateVehicleStatus(parts[0], parts[1]);
                            System.out.println("MonitoringAgent: Vehicle " + parts[0] + " updated to status: " + parts[1]);
                        }
                    }
                } else {
                    block();
                }
            }
        });
    }
}
