package org;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class PlanningAgent extends Agent {
    protected void setup() {
        System.out.println("Planning Agent started.");

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();

                    try {
                        int trafficLevel = Integer.parseInt(content); // Conversie sigură
                        System.out.println("PlanningAgent: Received traffic data: " + trafficLevel);
                        adjustTrafficLights(trafficLevel);
                    } catch (NumberFormatException e) {
                        System.out.println("PlanningAgent: Invalid data received (" + content + "). Ignoring.");
                    }
                } else {
                    block();
                }
            }
        });
    }

    private void adjustTrafficLights(int trafficLevel) {
        ACLMessage adjustment = new ACLMessage(ACLMessage.INFORM);
        adjustment.addReceiver(new jade.core.AID("TrafficLightAgent", jade.core.AID.ISLOCALNAME));

        if (trafficLevel > 5) { // Dacă sunt multe vehicule, prelungim timpul de verde
            System.out.println("PlanningAgent: High traffic detected! Increasing GREEN time.");
            adjustment.setContent("Increase GREEN");
        } else {
            System.out.println("PlanningAgent: Low traffic detected! Keeping default timing.");
            adjustment.setContent("Default Timing");
        }
        send(adjustment);
    }
}
