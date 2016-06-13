package agents;

import com.sun.org.apache.xpath.internal.operations.Bool;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.Random;


/**
 * Created by gerardo on 24/05/16.
 */
public class CarAgent extends Agent {

    private static boolean engineState;
    private static boolean doorsState;
    private static boolean internAlarm;
    private static boolean locks;

    public static boolean getEngineState() {
        return engineState;
    }
    public static boolean getDoorsSate() {
        return doorsState;
    }
    public static boolean getInternAlarmState;
    public static void setDoorState() { doorsState = !doorsState; }
    public static void setInterAlarmState() { internAlarm = !internAlarm; }

    MessageTemplate requestTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

    public void setup() {
        System.out.println("Agente >> " + getLocalName() + " iniciado.");
        addBehaviour(new CyclicBehaviour(this) {

            public void action() {

                ACLMessage coorMsg = receive();
                if (coorMsg != null && coorMsg.getSender().getLocalName().equals("CoorAgent") &&
                        coorMsg.getPerformative() == ACLMessage.REQUEST) {
                    // Sending info to CoorAgent
                    ACLMessage infoResp = new ACLMessage(ACLMessage.INFORM);
                    infoResp.addReceiver(new AID("CoorAgent", false));

                    engineState = new Random().nextBoolean();
                    doorsState = new Random().nextBoolean();

                    infoResp.setContent(Boolean.toString(engineState) + " " + Boolean.toString(doorsState));
                    send(infoResp);
                } else if (coorMsg != null && coorMsg.getSender().getLocalName().equals("CoorAgent") &&
                        coorMsg.getPerformative() == ACLMessage.INFORM) {
                    internAlarm = true;
                    System.out.println("CarAgent encencio alarma interna!");
                    setDoorState();
                    
                }
//                System.out.println("Motor de carro >> " + engineState);
//                System.out.println("Puertas de carro >> " + doorsState + "\n");
            }
        });

    }
}
