package org;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.TickerBehaviour;

public class VehicleAgent extends Agent {
    private boolean crossed = false;

    protected void setup() {
        System.out.println("Vehicle Agent: " + getLocalName() + " started.");

        addBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                if (!crossed) {
                    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                    request.addReceiver(getDefaultTrafficLight());
                    request.setContent("VehicleAgent Request: cross intersection");
                    send(request);
                }
            }
        });

        addBehaviour(new jade.core.behaviours.CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if (msg.getContent().equals("Green")) {
                        System.out.println("VehicleAgent Answer: " + getLocalName() + " can cross.");
                        crossed = true;

                        // Trimite actualizarea statusului către MonitoringAgent
                        ACLMessage statusUpdate = new ACLMessage(ACLMessage.INFORM);
                        statusUpdate.addReceiver(new jade.core.AID("MonitoringAgent", jade.core.AID.ISLOCALNAME));
                        statusUpdate.setContent(getLocalName() + ": Crossed");
                        send(statusUpdate);

                    } else {
                        System.out.println("VehicleAgent Answer: " + getLocalName() + " must wait.");
                    }
                } else {
                    block();
                }
            }
        });
    }

    private jade.core.AID getDefaultTrafficLight() {
        return new jade.core.AID("TrafficLightAgent", jade.core.AID.ISLOCALNAME);
    }
}
