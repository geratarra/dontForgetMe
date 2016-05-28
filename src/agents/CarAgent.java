package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Random;


/**
 * Created by gerardo on 24/05/16.
 */
public class CarAgent extends Agent {

    private static boolean engineState;
    private static boolean doorsState;
    private static boolean internAlarm;

    public static boolean getEngineState() {
        return engineState;
    }
    public static boolean getDoorsSate() {
        return doorsState;
    }
    public static boolean getInternAlarmState;

    MessageTemplate generalTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

    public void setup() {
        System.out.println("Agente >> " + getLocalName() + " iniciado.");
        addBehaviour(new CyclicBehaviour(this) {

            public void action() {

                ACLMessage coorMsg = receive(generalTemplate);
                if (coorMsg != null && coorMsg.getContent().equals("Encender alarma interna")) {
                    System.out.println("Alarma interna de auto ENCENDIDA");
                }

                engineState = new Random().nextBoolean();
//                System.out.println("Motor de carro >> " + engineState);

                doorsState = new Random().nextBoolean();
//                System.out.println("Puertas de carro >> " + doorsState + "\n");
            }
        });

    }
}
