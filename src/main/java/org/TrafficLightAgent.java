package org;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.ArrayList;
import java.util.List;

public class TrafficLightAgent extends Agent {
    private String state = "RED";
    private boolean emergencyActive = false;
    private TrafficControlGUI gui;
    private int lightChangeCount = 0;

    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            gui = (TrafficControlGUI) args[0];
        } else {
            System.out.println("TrafficLightAgent: No GUI received!");
        }

        System.out.println("Traffic Light Agent started.");
        System.out.println("Traffic Light changed to: " + state);
        gui.setTrafficLightState(state);

        addBehaviour(new TickerBehaviour(this, 10000) {
            protected void onTick() {
                if (!emergencyActive) {
                    long currentTimeForChange = System.currentTimeMillis(); // Capture current time before state change

                    switch (state) {
                        case "RED" -> state = "GREEN";
                        case "GREEN" -> state = "YELLOW";
                        case "YELLOW" -> state = "RED";
                    }
                    if (state.equals("RED") && !emergencyActive) {
                        ACLMessage pedestrianMessage = new ACLMessage(ACLMessage.INFORM);
                        pedestrianMessage.setContent("Red");

                        List<AID> pedestrianAgents = getPedestrianAgents();
                        for (AID pedestrian : pedestrianAgents) {
                            pedestrianMessage.addReceiver(pedestrian);
                        }
                        send(pedestrianMessage);
                        System.out.println("TrafficLightAgent - Sent Red signal to pedestrians");
                    }

                    if (state.equals("GREEN") && !emergencyActive) {
                        ACLMessage greenMessage = new ACLMessage(ACLMessage.INFORM);
                        greenMessage.setContent("Green");

                        List<AID> vehicleAgents = getVehicleAgents();
                        for (AID vehicle : vehicleAgents) {
                            greenMessage.addReceiver(vehicle);
                        }
                        send(greenMessage);
                    }

                    gui.setTrafficLightState(state);
                    System.out.println("Traffic Light changed to: " + state);
                    lightChangeCount++;

                    ACLMessage monitorUpdate = new ACLMessage(ACLMessage.INFORM);
                    monitorUpdate.addReceiver(new jade.core.AID("MonitoringAgent", jade.core.AID.ISLOCALNAME));
                    monitorUpdate.setContent("LightChange:" + lightChangeCount + ":" + currentTimeForChange);
                    send(monitorUpdate);
                }
            }
        });
    }

    private List<AID> getVehicleAgents() {
        List<AID> vehicleAgents = new ArrayList<>();
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("vehicle");
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            for (DFAgentDescription dfd : result) {
                vehicleAgents.add(dfd.getName());
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        return vehicleAgents;
    }

    private List<AID> getPedestrianAgents() {
        List<AID> pedestrianAgents = new ArrayList<>();

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("pedestrian");
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            for (DFAgentDescription dfd : result) {
                pedestrianAgents.add(dfd.getName());
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("TrafficLightAgent - Found pedestrians: " + pedestrianAgents);
        return pedestrianAgents;
    }
}