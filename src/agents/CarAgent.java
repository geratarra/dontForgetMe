package agents;

import com.sun.org.apache.xpath.internal.operations.Bool;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;

import java.io.IOException;
import java.util.Random;


/**
 * Created by gerardo on 24/05/16.
 */
public class CarAgent extends Agent {

    private static boolean engineState;
    private static boolean doorState;
    private static boolean internAlarm;
    private static boolean locks;
    private static boolean alarm;

    public static boolean getEngineState() {
        return engineState;
    }
    public static boolean getDoorsSate() {
        return doorState;
    }
    public static boolean getInternAlarmState;
    public static void setDoorState(boolean aux) { doorState = aux; }
    public static void setInterAlarmState(boolean aux) { internAlarm = aux; }
    public static void unlock() { locks = false; }
    public static void lock() { locks = true; }

    MessageTemplate requestTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

    private void sendDoorStateMsg() {
        ACLMessage doorStateMsg = new ACLMessage(ACLMessage.INFORM);
        doorStateMsg.addReceiver(new AID("CoorAgent", false));
        doorStateMsg.setContent(String.valueOf(doorState));
        send(doorStateMsg);
    }

    public void sendEngineStateMsg() {
        // Sending engine state to CoorAgent
        ACLMessage infoResp = new ACLMessage(ACLMessage.INFORM);
        infoResp.addReceiver(new AID("CoorAgent", false));
        infoResp.setContent(Boolean.toString(engineState));
        send(infoResp);
    }

    public void setup() {
        doWait(3000);
        System.out.println("Agente >> " + getLocalName() + " iniciado.");
        addBehaviour(new CyclicBehaviour(this) {

            public void action() {

                ACLMessage coorMsg = receive();
                if (coorMsg != null && coorMsg.getSender().getLocalName().equals("CoorAgent") &&
                        coorMsg.getPerformative() == ACLMessage.REQUEST) {
                    if (coorMsg.getContent().equals("Give me the engine state")) {
                        sendEngineStateMsg();
//                    engineState = new Random().nextBoolean();
//                    doorState = new Random().nextBoolean();
                    } else if (coorMsg.getContent().equals("Give me the door's state 1")) {
                        setDoorState(false);
                        sendDoorStateMsg();
                    } else if (coorMsg.getContent().equals("Give me the door's state")) {
                        System.out.println("CarAgent recibio peticion de estado de puerta");
                        setDoorState(new Random().nextBoolean());
                        sendDoorStateMsg();
                    }
                } else if (coorMsg != null && coorMsg.getSender().getLocalName().equals("CoorAgent") &&
                            coorMsg.getPerformative() == ACLMessage.INFORM) {
                    if (coorMsg.getContent().equals("Active intern alarm")) {
                        setInterAlarmState(true);
                        System.out.println("CarAgent activo alarma interna");
                        setDoorState(true);
                        sendDoorStateMsg();
                    } else if (coorMsg.getContent().equals("Unlock the doors")) {
                        unlock();
                        System.out.println("CarAgent desactivo los seguros del auto");
                    } else if (coorMsg.getContent().equals("Stop intern alarm")) {
                        setInterAlarmState(false);
                        System.out.println("CarAgent desactivo la alarma interna");
                    } else if (coorMsg.getContent().equals("Lock the car")) {
                        lock();
                        System.out.println("CarAgent aseguro el auto");
                    } else if (coorMsg.getContent().equals("Start alarm")) {
                        alarm = true;
                        System.out.println("Alarma del carro activada!");
                    }
                }
            }
        });
    }
}
