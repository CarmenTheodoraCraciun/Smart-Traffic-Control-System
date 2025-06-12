package org;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class MonitoringAgent extends Agent {
    private TrafficControlGUI gui;
    private HashMap<String, String> vehicleStatus = new HashMap<>();
    private HashMap<String, String> pedestrianStatus = new HashMap<>();
    private HashMap<String, Long> entryTimes = new HashMap<>();
    private int totalLightChanges = 0;
    private long lastProcessedChangeTime = 0;
    private long lastLightCycleDuration = 0;
    private long totalWaitingTime = 0;
    private int entitiesProcessed = 0;

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

                    // Procesare schimbare semafor
                    if (content.startsWith("LightChange")) {
                        String[] parts = content.split(":");
                        if (parts.length == 3) {
                            totalLightChanges = Integer.parseInt(parts[1].trim());
                            long currentChangeTime = Long.parseLong(parts[2].trim());

                            if (lastProcessedChangeTime != 0) {
                                lastLightCycleDuration = currentChangeTime - lastProcessedChangeTime;
                            }
                            lastProcessedChangeTime = currentChangeTime;

                            System.out.println("MonitoringAgent received: Light changes=" + totalLightChanges);
                        }
                    }

                    // Procesare vehicule și pietoni
                    else if (content.contains(":")) {
                        String[] parts = content.split(":");
                        if (parts.length == 2) {
                            String name = parts[0];
                            String status = parts[1];

                            boolean isPedestrian = isPedestrian(name);
                            HashMap<String, String> targetStatus = isPedestrian ? pedestrianStatus : vehicleStatus;

                            if (!entryTimes.containsKey(name)) {
                                entryTimes.put(name, System.currentTimeMillis());
                            }

                            if (status.equals("passed") && entryTimes.containsKey(name)) {
                                long entryTime = entryTimes.get(name);
                                long waitingTime = System.currentTimeMillis() - entryTime;

                                totalWaitingTime += waitingTime;
                                entitiesProcessed++;
                                entryTimes.remove(name);
                            }

                            targetStatus.put(name, status);

                            String type = isPedestrian ? "Pedestrian" : (getEmergencyAgents().contains(new AID(name, AID.ISLOCALNAME)) ? "Emergency" : "Vehicule");
                            gui.updateAgentStatus(type, name, status);
                        }
                    }

                    // Procesare verificare pietoni vs. urgențe
                    else if (content.equals("CheckEmergency")) {
                        boolean emergencyPresent = vehicleStatus.entrySet().stream()
                                .anyMatch(entry -> getEmergencyAgents().contains(new AID(entry.getKey(), AID.ISLOCALNAME)) && !entry.getValue().equals("passed"));

                        ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                        response.addReceiver(msg.getSender());
                        response.setContent(emergencyPresent ? "EmergencyActive" : "NoEmergency");
                        send(response);
                        System.out.println("MonitoringAgent - Emergency check result: " + response.getContent());
                    }

                    long emergencyVehiclesInIntersection = vehicleStatus.entrySet().stream()
                            .filter(entry -> getEmergencyAgents().contains(new AID(entry.getKey(), AID.ISLOCALNAME)) && !entry.getValue().equals("passed"))
                            .count();

                    long averageWaitingTime = entitiesProcessed > 0 ? totalWaitingTime / entitiesProcessed : 0;
                    double frequency = (lastLightCycleDuration > 0) ? (60000.0 / lastLightCycleDuration) : 0.0;

                    gui.updateMonitoring(vehicleStatus.size(), pedestrianStatus.size(), (int) emergencyVehiclesInIntersection, averageWaitingTime, frequency);
                } else {
                    block();
                }
            }
        });
    }

    private boolean isPedestrian(String agentName) {
        try {
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("pedestrian"); // Tipul pietonilor înregistrat în DFService
            template.addServices(sd);

            DFAgentDescription[] result = DFService.search(this, template);
            for (DFAgentDescription dfd : result) {
                if (dfd.getName().getLocalName().equals(agentName)) {
                    return true;
                }
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        return false;
    }


    private List<AID> getEmergencyAgents() {
        List<AID> emergencyAgents = new ArrayList<>();
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("emergency");
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
