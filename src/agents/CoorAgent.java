package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * Created by gerardo on 24/05/16.
 */
public class CoorAgent extends Agent {

    MessageTemplate generalTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

    private static boolean doorState;
    private static boolean engineState;
    private static boolean internAlarmState;
    private static int temperature;
    private static String weight;
    private static String presure;

    protected void setup() {
        System.out.println("Agente >> " + getLocalName() + " iniciado.");

        addBehaviour(new TickerBehaviour(this, 2000) {
            protected void onTick() {

                // Sending request to TempAgent
                ACLMessage tempReq = new ACLMessage(ACLMessage.REQUEST);
                tempReq.addReceiver(new AID("TempAgent", false));
                tempReq.setContent("Give me the temperature");
                send(tempReq);

                // Sending request to WPAgent
                ACLMessage wpReq = new ACLMessage(ACLMessage.REQUEST);
                wpReq.addReceiver(new AID("WPagent", false));
                wpReq.setContent("Give me weight and preasure");
                send(wpReq);

                // Sending request to CarAgent
                ACLMessage engineStateReq = new ACLMessage(ACLMessage.REQUEST);
                engineStateReq.addReceiver(new AID("CarAgent", false));
                engineStateReq.setContent("Give me the engine state");
                send(engineStateReq);

                // Receiving messages from TempAgent and WPAgent
                ACLMessage resp = blockingReceive(generalTemplate);
                if (resp != null && resp.getSender().getLocalName().equals("TempAgent")) {
                    System.out.println("Coordinador recibio de >> " + tempReq.getSender().getName() +
                        " temperatura >> " + resp.getContent() + "\n");
                    temperature = Integer.parseInt(resp.getContent());
                } else if (resp != null && resp.getSender().getLocalName().equals("WPAgent")) {
                    weight = resp.getContent().split(" ")[0];
                    presure = resp.getContent().split(" ")[1];
                    System.out.println("Coordinador recibio de >> " + resp.getSender().getName() +
                                " peso >> " + weight + "Kg, presion >> " + presure + "N/m^2");
                } else if (resp != null && resp.getSender().getLocalName().equals("CarAgent")) {
                    System.out.println("Coordinador recibio de >> " + resp.getSender().getName() +
                            " estado de puertas >> " + resp.getContent().split(" ")[1] +
                            " estado de motor >> " + resp.getContent().split(" ")[0]);
                    doorState = Boolean.parseBoolean(resp.getContent().split(" ")[1]);
                    engineState = Boolean.parseBoolean(resp.getContent().split(" ")[0]);;
                }

                if (weight != null) {
                    if (engineState == false && doorState == false && Float.parseFloat(weight) >= 10) {
                        ACLMessage alert = new ACLMessage(ACLMessage.INFORM);
                        alert.addReceiver(new AID("CarAgent", false));
                        alert.setContent("Turn on the intern alarm");
                        send(alert);
                        internAlarmState = true;
                        System.out.println("CoorAgent envio alerta a CarAgent");
                    }
                }

            }
        });
    }

}
