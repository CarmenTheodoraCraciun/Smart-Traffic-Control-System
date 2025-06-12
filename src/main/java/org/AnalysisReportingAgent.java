package org;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

public class AnalysisReportingAgent extends Agent {
    private int currentVehiclesInIntersection = 0;
    private int currentPedestriansInIntersection = 0;
    private int currentEmergencyVehicles = 0;
    private long currentAverageWaitingTime = 0;
    private double currentLightChangeFrequency = 0.0;
    private int totalLightChanges = 0;
    private long totalWaitingTimeOverall = 0;
    private int entitiesProcessedOverall = 0;

    private static final String REPORT_FILENAME = "report.txt";

    // Variables to track changes
    private int prevVehiclesInIntersection = -1;
    private int prevPedestriansInIntersection = -1;
    private int prevEmergencyVehicles = -1;
    private long prevAverageWaitingTime = -1;
    private double prevLightChangeFrequency = -1;
    private int prevTotalLightChanges = -1;

    protected void setup() {
        System.out.println("Analysis Reporting Agent: " + getLocalName() + " started.");

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("traffic-analysis");
        sd.setName("TrafficReportingService");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();
                    if (content.startsWith("REPORT_DATA:")) {
                        processReportData(content.substring("REPORT_DATA:".length()));
                        generateReport();
                    }
                } else {
                    block();
                }
            }
        });
    }

    private void processReportData(String dataString) {
        String[] pairs = dataString.split(";");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                switch (key) {
                    case "vehiculeInIntersection":
                        currentVehiclesInIntersection = Integer.parseInt(value);
                        break;
                    case "pedestrianInIntersection":
                        currentPedestriansInIntersection = Integer.parseInt(value);
                        break;
                    case "emergencyVehicles":
                        currentEmergencyVehicles = Integer.parseInt(value);
                        break;
                    case "averageWaitingTime":
                        currentAverageWaitingTime = Long.parseLong(value);
                        break;
                    case "lightChangeFrequency":
                        currentLightChangeFrequency = Double.parseDouble(value);
                        break;
                    case "totalLightChanges":
                        totalLightChanges = Integer.parseInt(value);
                        break;
                    case "totalWaitingTimeOverall":
                        totalWaitingTimeOverall = Long.parseLong(value);
                        break;
                    case "entitiesProcessedOverall":
                        entitiesProcessedOverall = Integer.parseInt(value);
                        break;
                }
            }
        }
    }

    private void generateReport() {
        // Check if data has changed
        if (currentVehiclesInIntersection == prevVehiclesInIntersection &&
                currentPedestriansInIntersection == prevPedestriansInIntersection &&
                currentEmergencyVehicles == prevEmergencyVehicles &&
                currentAverageWaitingTime == prevAverageWaitingTime &&
                currentLightChangeFrequency == prevLightChangeFrequency &&
                totalLightChanges == prevTotalLightChanges) {

            return;
        }

        // Update previous values
        prevVehiclesInIntersection = currentVehiclesInIntersection;
        prevPedestriansInIntersection = currentPedestriansInIntersection;
        prevEmergencyVehicles = currentEmergencyVehicles;
        prevAverageWaitingTime = currentAverageWaitingTime;
        prevLightChangeFrequency = currentLightChangeFrequency;
        prevTotalLightChanges = totalLightChanges;

        // Generate report only if there are changes
        try (FileWriter fw = new FileWriter(REPORT_FILENAME, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println("\n--- Traffic System Report ---");
            pw.println("Timestamp: " + new java.util.Date());
            pw.println("-----------------------------");
            pw.println("Current Data:");
            pw.println("  Vehicles in intersection: " + currentVehiclesInIntersection);
            pw.println("  Pedestrians in intersection: " + currentPedestriansInIntersection);
            pw.println("  Emergency vehicles: " + currentEmergencyVehicles);
            pw.println("  Average waiting time: " + currentAverageWaitingTime + " ms");
            pw.printf("  Traffic light change frequency: %.2f changes per minute\n", currentLightChangeFrequency);
            pw.println("\nGeneral Statistics:");
            pw.println("  Total traffic light changes: " + totalLightChanges);
            pw.println("-----------------------------\n");

            System.out.println("AnalysisReportingAgent: Report generated successfully!");

        } catch (IOException e) {
            System.err.println("AnalysisReportingAgent: Error writing report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        File reportFile = new File(REPORT_FILENAME);
        System.out.println("Analysis Reporting Agent: " + getLocalName() + " terminated.");
        System.out.println("The full report has been saved to: " + reportFile.getAbsolutePath());
    }
}