package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Random;

/**
 * Created by gerardo on 24/05/16.
 */
public class WPAgent extends Agent {

    public static float weight;

    MessageTemplate requestTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

    protected void setup(){

        doWait(3000);
        System.out.println("Agente >> " + getLocalName() + " iniciado.");
        // Se le anade un comportamiento ciclico al agente para que siempre este en espera de mensaje
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage wpReq = blockingReceive(requestTemplate);
                // Los mensajes recibidos siempre son de CoorAgent, aun asi, se hace la validacion.
                if (wpReq != null && wpReq.getSender().getLocalName().equals("CoorAgent") &&
                        wpReq.getPerformative() == ACLMessage.REQUEST) {
                    // Mensaje de respuesta
                    ACLMessage w = new ACLMessage(ACLMessage.INFORM);
                    w.addReceiver(new AID("CoorAgent", false));
                    if (wpReq.getContent().equals("Give me the weight")) {
                        weight = (float) (Math.floor((new Random().nextFloat()*(15 - 5) + 5)*100)/100);
                        w.setContent(Float.toString(weight));
                    // Este mensaje es para el caso o casos en los que se requiera que haya peso
                        // (por efecto de la simulacion)
                    } else if (wpReq.getContent().equals("Give me the weight 1")) {
                        weight = (float) (Math.floor((new Random().nextFloat()*(15 - 10) + 10)*100)/100);
                        w.setContent(Float.toString(weight));
                    }
                    send(w);
                }
            }
        });

    }

}
