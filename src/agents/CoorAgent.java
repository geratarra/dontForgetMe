package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.leap.Iterator;

/**
 * Created by gerardo on 24/05/16.
 */
public class CoorAgent extends Agent {

    MessageTemplate generalTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

    protected void setup() {
        System.out.println("Agente >> " + getLocalName() + " iniciado.");

        addBehaviour(new TickerBehaviour(this, 2000) {
            @Override
            protected void onTick() {

                // Sending request to TempAgent
                ACLMessage tempReq = new ACLMessage(ACLMessage.REQUEST);
                tempReq.addReceiver(new AID("TempAgent", false));
                tempReq.setContent("Give me the temperature");
                send(tempReq);

                // Sending request to WPAgent
                ACLMessage wpReq = new ACLMessage(ACLMessage.REQUEST);
                wpReq.addReceiver(new AID("WPagent", false));
                wpReq.setContent("Give me weight and preasure");
                send(wpReq);

                // Receiving messages from TempAgent and WPAgent
                ACLMessage resp = blockingReceive(generalTemplate);
                if (resp != null && resp.getSender().getLocalName().equals("TempAgent")) {
                    try {
                        System.out.println("Coordinador recibio de >> " + tempReq.getSender().getName() +
                            " temperatura >> " + resp.getContentObject() + "\n");
                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }
                } else if (resp != null && resp.getSender().getLocalName().equals("WPAgent")) {
                    try {
                        float[] aux = (float[])resp.getContentObject();
                        System.out.println("Coordinador recibio de >> " + resp.getSender().getName() +
                                " peso >> " + aux[0] + "Kg, presion >> " + aux[1] + "N/m^2");
                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

}
