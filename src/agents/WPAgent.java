package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.Random;

/**
 * Created by gerardo on 24/05/16.
 */
public class WPAgent extends Agent {

    public static float weight;
    public static float presure;
    private static float[] info = new float[2];

    MessageTemplate generalTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

    protected void setup(){

        System.out.println("Agente >> " + getLocalName() + " iniciado.");
        addBehaviour(new TickerBehaviour(this, 2000) {
            @Override
            protected void onTick() {

                try {
                    info[0] = (float) (Math.floor((new Random().nextFloat()*(15 - 5) + 5)*100)/100);
                    System.out.println("Peso en el auto >> " + info[0] + " Kg");

                    info[1] = new Random().nextInt(150 - 100) + 100;
                    System.out.println("Presion en el auto >> " + info[1] + " N/m^2");
                } catch (Exception e) {
                    System.out.println(e);
                }


                ACLMessage wpReq = blockingReceive(generalTemplate);
                if (wpReq != null && wpReq.getSender().getLocalName().equals("CoorAgent")) {
                    ACLMessage wp = new ACLMessage(ACLMessage.INFORM);
                    wp.addReceiver(new AID("CoorAgent", false));
                    try {
                        wp.setContentObject(info);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    send(wp);
                }
            }
        });

    }

}
