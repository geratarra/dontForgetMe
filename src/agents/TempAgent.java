package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.Random;

/**
 * Created by gerardo on 24/05/16.
 */
public class TempAgent extends Agent {

    private static int temperature;
    MessageTemplate generalTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

    public static int getTemperature() {
        return temperature;
    }

    protected void setup() {
        System.out.println("Agente >> " + getLocalName() + " iniciado.");
        addBehaviour(new TickerBehaviour(this, 2000) {
            @Override
            protected void onTick() {

                temperature = new Random().nextInt((35 - 25) + 1) + 25;
                System.out.println("TempAgent cambio temperatura >> " + temperature + " grados celsius");

                ACLMessage tempReq = blockingReceive(generalTemplate);
                if (tempReq.getSender().getLocalName().equals("CoorAgent")) {
                    ACLMessage temp = new ACLMessage(ACLMessage.INFORM);
                    temp.addReceiver(new AID("CoorAgent", false));
//                    try {
//                        temp.setContentObject(temperature);
//                        send(temp);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    temp.setContent(Integer.toString(temperature));
                }
            }
        });
    }

}
