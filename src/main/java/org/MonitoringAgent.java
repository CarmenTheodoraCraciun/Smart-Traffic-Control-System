package org;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MonitoringAgent extends Agent {
    private TrafficControlGUI gui;
    private HashMap<String, String> vehicleStatus = new HashMap<>();
    private HashMap<String, Long> waitingTimes = new HashMap<>();
    private int totalLightChanges = 0;
    private long lastChangeDuration = 0;

    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            gui = (TrafficControlGUI) args[0];
        } else {
            System.out.println("MonitoringAgent: No GUI received!");
        }

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();

                    // Procesare semnal de schimbare a semaforului
                    if (content.startsWith("LightChange")) {
                        String[] parts = content.split(": ");
                        if (parts.length == 3) {
                            totalLightChanges = Integer.parseInt(parts[1]);
                            lastChangeDuration = Long.parseLong(parts[2]);
                        }
                    }
                    // Procesare vehicule și urgențe
                    else if (content.contains(":")) {
                        String[] parts = content.split(": ");
                        if (parts.length == 2) {
                            String name = parts[0];
                            String status = parts[1];

                            vehicleStatus.put(name, status);

                            List<AID> emergencyAgents = getEmergencyAgents();
                            if (emergencyAgents.contains(new AID(name, AID.ISLOCALNAME))) {
                                gui.updateCarStatus("Emergency", name, status);
                            } else {
                                gui.updateCarStatus("Vehicule", name, status);
                            }
                        }
                    }

                    // Actualizare interfață monitorizare
                    gui.updateMonitoring("Schimbări semafor: " + totalLightChanges +
                            " | Ultima schimbare: " + lastChangeDuration + " ms" +
                            " | Vehicule în intersecție: " + vehicleStatus.size());
                } else {
                    block(); // Așteaptă noi mesaje
                }
            }
        });
    }

    private List<AID> getEmergencyAgents() {
        List<AID> emergencyAgents = new ArrayList<>();
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("emergency"); // Același tip folosit la înregistrare
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            for (DFAgentDescription dfd : result) {
                emergencyAgents.add(dfd.getName());
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        return emergencyAgents;
    }
}
