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
public class WPAgent extends Agent {

    public static float weight;
    public static float presure;

    MessageTemplate generalTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

    protected void setup(){

        doWait(3000);
        System.out.println("Agente >> " + getLocalName() + " iniciado.");
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage wpReq = blockingReceive(generalTemplate);
                if (wpReq != null && wpReq.getSender().getLocalName().equals("CoorAgent") &&
                        wpReq.getPerformative() == ACLMessage.REQUEST) {
                    if (wpReq.getContent().equals("Give me the weight")) {
                        ACLMessage w = new ACLMessage(ACLMessage.INFORM);
                        w.addReceiver(new AID("CoorAgent", false));
                        w.setContent(Float.toString(weight));
                        send(w);
                    } else if (wpReq.getContent().equals("Give me the weight 1")) {
                        ACLMessage w = new ACLMessage(ACLMessage.INFORM);
                        w.addReceiver(new AID("CoorAgent", false));
                        // Weight limit: 10 Kg
                        weight = (float) (Math.floor((new Random().nextFloat()*(15 - 10) + 10)*100)/100);
                        w.setContent(Float.toString(weight));
                        send(w);
                        weight = (float) (Math.floor((new Random().nextFloat()*(15 - 7) + 7)*100)/100);
                    } else if (wpReq.getContent().equals("Give me the weight 2")) {
                        ACLMessage w = new ACLMessage(ACLMessage.INFORM);
                        w.addReceiver(new AID("CoorAgent", false));
                        // Weight limit: 10 Kg
                        weight = (float) (Math.floor((new Random().nextFloat()*(9.99 - 5) + 5)*100)/100);
                        w.setContent(Float.toString(weight));
                        send(w);
                    }
                }
            }
        });

    }

}
