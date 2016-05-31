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

    MessageTemplate generalTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

    protected void setup(){

        System.out.println("Agente >> " + getLocalName() + " iniciado.");
        addBehaviour(new TickerBehaviour(this, 2000) {
            @Override
            protected void onTick() {

                try {
                    // Weight limit: 10 Kg
                    weight = (float) (Math.floor((new Random().nextFloat()*(15 - 5) + 5)*100)/100);
                    System.out.println("Peso en el auto >> " + weight + " Kg");

                    // Presure limit: 125 N/m^2
                    presure = new Random().nextInt(150 - 100) + 100;
                    System.out.println("Presion en el auto >> " + presure + " N/m^2");
                } catch (Exception e) {
                    System.out.println(e);
                }

                ACLMessage wpReq = blockingReceive(generalTemplate);
                if (wpReq != null && wpReq.getSender().getLocalName().equals("CoorAgent")) {
                    ACLMessage wp = new ACLMessage(ACLMessage.INFORM);
                    wp.addReceiver(new AID("CoorAgent", false));
                    wp.setContent(Float.toString(weight) + " " + Float.toString(presure));
                    send(wp);
                }
            }
        });

    }

}
