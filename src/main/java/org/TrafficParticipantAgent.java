package org;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.CyclicBehaviour;

public abstract class TrafficParticipantAgent extends Agent {
    protected boolean crossed = false;
    protected abstract ParticipantType getParticipantType(); // Vehicul sau Pieton

    protected void setup() {
        System.out.println(getParticipantType() + " Agent: " + getLocalName() + " started.");

        ACLMessage initial = new ACLMessage(ACLMessage.REQUEST);
        initial.addReceiver(new jade.core.AID("MonitoringAgent", jade.core.AID.ISLOCALNAME));
        initial.setContent(getLocalName() + ": New");
        send(initial);

        registerInDF();

        addBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                if (!crossed) {
                    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                    request.addReceiver(getDefaultTrafficLight());
                    request.setContent(getParticipantType() + " Request: cross intersection");
                    send(request);

                    ACLMessage monitorUpdate = new ACLMessage(ACLMessage.REQUEST);
                    monitorUpdate.addReceiver(new jade.core.AID("MonitoringAgent", jade.core.AID.ISLOCALNAME));
                    monitorUpdate.setContent(getLocalName() + ": Waiting");
                    send(monitorUpdate);
                }
            }
        });

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if (msg.getContent().equals(getParticipantType().getCrossSignal())) {
                        System.out.println(getParticipantType() + " Agent Answer: " + getLocalName() + " can cross.");
                        crossed = true;

                        ACLMessage statusUpdate = new ACLMessage(ACLMessage.INFORM);
                        statusUpdate.addReceiver(new jade.core.AID("MonitoringAgent", jade.core.AID.ISLOCALNAME));
                        statusUpdate.setContent(getLocalName() + ": Crossed");
                        send(statusUpdate);
                    } else {
                        System.out.println(getParticipantType() + " Agent Answer: " + getLocalName() + " must wait.");
                    }
                } else {
                    block();
                }
            }
        });
    }

    private void registerInDF() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(getParticipantType().name());
        sd.setName(getParticipantType() + "AgentService");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private jade.core.AID getDefaultTrafficLight() {
        return new jade.core.AID("TrafficLightAgent", jade.core.AID.ISLOCALNAME);
    }
}
