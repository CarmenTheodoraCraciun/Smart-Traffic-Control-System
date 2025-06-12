package org;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.CyclicBehaviour;

import javax.swing.*;

public class PedestrianAgent extends Agent {
    private boolean crossed = false;
//    private DefaultListModel<String> vehicleListModel, emergencyListModel, pedestrianListModel;

    protected void setup() {
        System.out.println("Pedestrian Agent: " + getLocalName() + " started.");

        ACLMessage initial = new ACLMessage(ACLMessage.REQUEST);
        initial.addReceiver(new jade.core.AID("MonitoringAgent", jade.core.AID.ISLOCALNAME));
        initial.setContent(getLocalName() + ": New");
        send(initial);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("pedestrian");
        sd.setName("PedestrianAgentService");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                if (!crossed) {
                    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                    request.addReceiver(getDefaultTrafficLight());
                    request.setContent("Pedestian Request: cross intersection");
                    send(request);

                    ACLMessage monitorUpdate = new ACLMessage(ACLMessage.REQUEST);
                    monitorUpdate.addReceiver(new jade.core.AID("MonitoringAgent", jade.core.AID.ISLOCALNAME));
                    monitorUpdate.setContent(getLocalName() + ": Waiting");
                    send(monitorUpdate);
                }
            }
        });

        addBehaviour(new jade.core.behaviours.CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if (msg.getContent().equals("Red")) {
                        System.out.println("PedestrianAgent Answer: " + getLocalName() + " can cross.");
                        crossed = true;

                        ACLMessage statusUpdate = new ACLMessage(ACLMessage.INFORM);
                        statusUpdate.addReceiver(new jade.core.AID("MonitoringAgent", jade.core.AID.ISLOCALNAME));
                        statusUpdate.setContent(getLocalName() + ": Crossed");
                        send(statusUpdate);
                    } else {
                        System.out.println("PedestrianAgent Answer: " + getLocalName() + " must wait.");
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
