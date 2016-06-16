package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Created by gerardo on 24/05/16.
 */
public class InterfazAgent extends Agent {

    protected void setup() {
        System.out.println("Agente interfaz activado.");
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage alertMsg = blockingReceive();
                if (alertMsg != null && alertMsg.getSender().getLocalName().equals("CoorAgent")) {
                    if (alertMsg.getContent().equals("Alert level 1")) {
                        System.out.println("Alerta nivel 1 activa");
                    } else {
                        System.out.println("Alerta nivel 2 activa");
                    }
                }
            }
        });
    }

}
