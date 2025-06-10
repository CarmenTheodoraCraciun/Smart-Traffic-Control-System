package org;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class TrafficLightAgent extends Agent {
    private String state = "RED";
    private boolean emergencyActive = false;
    private TrafficControlGUI gui;
    private long lastChangeTime = System.currentTimeMillis();
    private int lightChangeCount = 0;

    protected void setup() {
        // Primim GUI-ul ca parametru
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
                    switch (state) {
                        case "RED" -> state = "GREEN";
                        case "GREEN" -> state = "YELLOW";
                        case "YELLOW" -> state = "RED";
                    }

                    // Trimitere mesaj către vehicule dacă semaforul devine verde
                    if (state.equals("GREEN")) {
                        ACLMessage greenMessage = new ACLMessage(ACLMessage.INFORM);
                        greenMessage.setContent("Green");

                        List<AID> vehicleAgents = getVehicleAgents();
                        for (AID vehicle : vehicleAgents) {
                            greenMessage.addReceiver(vehicle);
                        }

                        send(greenMessage);
//                        System.out.println("TrafficLightAgent sent Green signal to vehicles: " + vehicleAgents);
                    }

                    gui.setTrafficLightState(state);
                    System.out.println("Traffic Light changed to: " + state);

                    long currentTime = System.currentTimeMillis();
                    lightChangeCount++;
                    long timeSinceLastChange = currentTime - lastChangeTime;
                    lastChangeTime = currentTime;

                    // Trimite datele de monitorizare
                    ACLMessage monitorUpdate = new ACLMessage(ACLMessage.INFORM);
                    monitorUpdate.addReceiver(new jade.core.AID("MonitoringAgent", jade.core.AID.ISLOCALNAME));
                    monitorUpdate.setContent("LightChange: " + lightChangeCount + ": " + timeSinceLastChange + " ms");
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

}
