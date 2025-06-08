package org;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class EmergencyAgent extends Agent {
    protected void setup() {
        System.out.println("Emergency Agent: " + getLocalName() + " in the intersection!");

        // Trimite cerere de urgență la semafor
        ACLMessage request = new ACLMessage(ACLMessage.INFORM);
        request.addReceiver(getDefaultTrafficLight());
        request.setContent("Emergency: Change to Green");
        send(request);

        // După ce trece intersecția, își schimbă statusul în "Crossed"
        ACLMessage statusUpdate = new ACLMessage(ACLMessage.INFORM);
        statusUpdate.addReceiver(new jade.core.AID("MonitoringAgent", jade.core.AID.ISLOCALNAME));
        statusUpdate.setContent(getLocalName() + ": Crossed");
        send(statusUpdate);

        System.out.println("EmergencyAgent Answer: " + getLocalName() + " has crossed and will remain in the list.");
        doDelete();
    }

    private jade.core.AID getDefaultTrafficLight() {
        return new jade.core.AID("TrafficLightAgent", jade.core.AID.ISLOCALNAME);
    }
}
