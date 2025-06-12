package org;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class EmergencyAgent extends Agent {
    protected void setup() {
        System.out.println("Emergency Agent: " + getLocalName() + " in the intersection!");

        // Trimite cerere de urgență la semafor
        ACLMessage request = new ACLMessage(ACLMessage.INFORM);
        request.addReceiver(getDefaultTrafficLight());
        request.setContent("Emergency: Change to Green");
        send(request);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("emergency");  // Tipul specific vehiculelor de urgență
        sd.setName("EmergencyAgentService");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        ACLMessage statusEntering = new ACLMessage(ACLMessage.REQUEST);
        statusEntering.addReceiver(new jade.core.AID("MonitoringAgent", jade.core.AID.ISLOCALNAME));
        statusEntering.setContent(getLocalName() + ": EmergencyEntering");
        send(statusEntering);

        // După traversare:
        ACLMessage statusUpdate = new ACLMessage(ACLMessage.INFORM);
        statusUpdate.addReceiver(new jade.core.AID("MonitoringAgent", jade.core.AID.ISLOCALNAME));
        statusUpdate.setContent(getLocalName() + ": Crossed");
        send(statusUpdate);

        System.out.println("EmergencyAgent Answer: " + getLocalName() + " has crossed.");
        doDelete();
    }

    private jade.core.AID getDefaultTrafficLight() {
        return new jade.core.AID("TrafficLightAgent", jade.core.AID.ISLOCALNAME);
    }
}
