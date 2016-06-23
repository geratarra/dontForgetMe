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
    MessageTemplate requestTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

    protected void setup() {
        System.out.println("Agente >> " + getLocalName() + " iniciado.");
        // Se le anade un comportamiento ciclico al agente para que siempre este en espera de mensaje
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                // Se recibe el mensaje
                ACLMessage tempResp = blockingReceive(requestTemplate);
                // Los mensajes recibidos siempre son de CoorAgent, aun asi, se hace la validacion.
                if (tempResp != null && tempResp.getSender().getLocalName().equals("CoorAgent")) {
                    // A partir de 30 grados de temperatura es cuando se alerta al agente InterfazAgent
                    temperature = new Random().nextInt((35 - 25) + 1) + 25;
                    ACLMessage temp = new ACLMessage(ACLMessage.INFORM);
                    temp.addReceiver(new AID("CoorAgent", false));
                    temp.setContent(Integer.toString(temperature));
                    send(temp);
                }
            }
        });
    }
}
