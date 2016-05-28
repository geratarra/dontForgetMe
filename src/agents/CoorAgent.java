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

                // Sending request to TempAgent
                ACLMessage tempReq = new ACLMessage(ACLMessage.REQUEST);
                tempReq.addReceiver(new AID("TempAgent", false));
                tempReq.setContent("Give me the temperature");
                send(tempReq);

                ACLMessage tempResp = blockingReceive(generalTemplate);
                if (tempResp != null && tempResp.getSender().getLocalName().equals("TempAgent")) {
                    System.out.println("Coordinador recibio de >> " + tempReq.getSender().getName() +
                        " temperatura >> " + tempResp.getContent() + "\n");
                }

            }
        });
    }

}
