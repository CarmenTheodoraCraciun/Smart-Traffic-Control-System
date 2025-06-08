package org;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

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

            // Creăm GUI-ul o singură dată
            TrafficControlGUI gui = new TrafficControlGUI();

            // Lansăm agenții și le transmitem GUI-ul
            Object[] guiParam = new Object[]{gui};
            AgentController trafficLightAgent = mainContainer.createNewAgent("TrafficLightAgent", "org.TrafficLightAgent", guiParam);
            trafficLightAgent.start();

            AgentController monitoringAgent = mainContainer.createNewAgent("MonitoringAgent", "org.MonitoringAgent", guiParam);
            monitoringAgent.start();

            System.out.println("Agents started.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
