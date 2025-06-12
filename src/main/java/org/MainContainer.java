package org;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainContainer {
    public static void main(String[] args) {
        try {
            // Inițializare platformă JADE
            Runtime runtime = Runtime.instance();
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN, "true");
            profile.setParameter(Profile.GUI, "true");
            AgentContainer mainContainer = runtime.createMainContainer(profile);

            System.out.println("JADE platform started.");

            // Golirea fișierului report.txt dacă există
            String reportFilePath = "report.txt";
            File reportFile = new File(reportFilePath);
            if (reportFile.exists()) {
                try (FileWriter fw = new FileWriter(reportFilePath, false)) {
                    System.out.println("MainContainer: Fișierul report.txt există. Îl golesc...");
                } catch (IOException e) {
                    System.err.println("MainContainer: Eroare la golirea fișierului report.txt: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Creăm GUI-ul o singură dată
            TrafficControlGUI gui = new TrafficControlGUI();

            // Lansăm agenții și le transmitem GUI-ul
            Object[] guiParam = new Object[]{gui};
            AgentController trafficLightAgent = mainContainer.createNewAgent("TrafficLightAgent", "org.TrafficLightAgent", guiParam);
            trafficLightAgent.start();

            AgentController monitoringAgent = mainContainer.createNewAgent("MonitoringAgent", "org.MonitoringAgent", guiParam);
            monitoringAgent.start();

            AgentController reportingAgent = mainContainer.createNewAgent("AnalysisReportingAgent", "org.AnalysisReportingAgent", guiParam);
            reportingAgent.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
