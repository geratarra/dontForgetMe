package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Random;

/**
 * Created by gerardo on 24/05/16.
 */
public class TempAgent extends Agent {

    private static int temperature;
    MessageTemplate generalTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

    protected void setup() {
        System.out.println("Agente >> " + getLocalName() + " iniciado.");
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage tempResp = blockingReceive(generalTemplate);
                if (tempResp != null && tempResp.getSender().getLocalName().equals("CoorAgent")) {
                    temperature = new Random().nextInt((35 - 30) + 1) + 30;
                    ACLMessage temp = new ACLMessage(ACLMessage.INFORM);
                    temp.addReceiver(new AID("CoorAgent", false));
                    temp.setContent(Integer.toString(temperature));
                    send(temp);
                }
            }
        });
    }
}
