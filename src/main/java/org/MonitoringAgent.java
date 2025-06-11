package org;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MonitoringAgent extends Agent {
    private TrafficControlGUI gui;
    private HashMap<String, String> vehicleStatus = new HashMap<>();
    private HashMap<String, Long> vehicleEntryTimes = new HashMap<>();
    private int totalLightChanges;
    private long lastProcessedChangeTime = 0;
    private long lastLightCycleDuration = 0;
    private long totalWaitingTime = 0;
    private int vehiclesProcessed = 0;

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

                    if (content.startsWith("LightChange")) {
                        String[] parts = content.split(":");
                        if (parts.length == 3) {
                            totalLightChanges = Integer.parseInt(parts[1].trim());
                            long currentChangeTime = Long.parseLong(parts[2].trim());

                            if (lastProcessedChangeTime != 0) {
                                lastLightCycleDuration = currentChangeTime - lastProcessedChangeTime;
                            }
                            lastProcessedChangeTime = currentChangeTime;

                            System.out.println("MonitoringAgent received: Total Changes=" + totalLightChanges + ", Current Change Time=" + currentChangeTime + ", Last Cycle Duration=" + lastLightCycleDuration + "ms");
                        }
                    }
                    else if (content.contains(":")) {
                        String[] parts = content.split(":");
                        if (parts.length == 2) {
                            String name = parts[0];
                            String status = parts[1];

                            if (!vehicleEntryTimes.containsKey(name)) {
                                vehicleEntryTimes.put(name, System.currentTimeMillis());
                            }

                            if (status.equals("passed") && vehicleEntryTimes.containsKey(name)) {
                                long entryTime = vehicleEntryTimes.get(name);
                                long waitingTime = System.currentTimeMillis() - entryTime;

                                totalWaitingTime += waitingTime;
                                vehiclesProcessed++;
                                vehicleEntryTimes.remove(name);
                            }

                            vehicleStatus.put(name, status);

                            List<AID> emergencyAgents = getEmergencyAgents();
                            if (emergencyAgents.contains(new AID(name, AID.ISLOCALNAME))) {
                                gui.updateCarStatus("Emergency", name, status);
                            } else {
                                gui.updateCarStatus("Vehicule", name, status);
                            }
                        }
                    }

                    long emergencyVehiclesInIntersection = vehicleStatus.entrySet().stream()
                            .filter(entry -> {
                                try {
                                    return getEmergencyAgents().contains(new AID(entry.getKey(), AID.ISLOCALNAME)) && !entry.getValue().equals("passed");
                                } catch (Exception e) {
                                    return false;
                                }
                            })
                            .count();

                    long currentAverageWaitingTime = vehiclesProcessed > 0 ? totalWaitingTime / vehiclesProcessed : 0;
                    double frequency = (lastLightCycleDuration > 0) ? (60000.0 / lastLightCycleDuration) : 0.0;

                    // Aici era problema în String.format. O să îl las la fel pentru debug.
                    // Dar pentru GUI, trimite direct valoarea double.
                    System.out.println("MonitoringAgent data: Vehicles Processed=" + vehiclesProcessed + ", Total Waiting Time=" + totalWaitingTime + ", Avg Waiting Time=" + currentAverageWaitingTime + "ms, Frequency=" + String.format("%.2f", frequency) + " changes/minute");

                    // Trimiți direct valoarea double pentru frequency. GUI-ul ar trebui să o formateze.
                    gui.updateMonitoring(vehicleStatus.size(), (int) emergencyVehiclesInIntersection, currentAverageWaitingTime, frequency);
                }
                else {
                    block();
                }
            }
        });
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