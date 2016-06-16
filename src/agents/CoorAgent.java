package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;

/**
 * Created by gerardo on 24/05/16.
 */
public class CoorAgent extends Agent {

    MessageTemplate generalTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

    private static boolean doorState;
    private static boolean engineState;
    private static boolean internAlarmState;
    private static int temperature;
    private static float weight;
    private static String presure;

    private void weightReq() {
        // Sending request to WPAgent
        ACLMessage wReq = new ACLMessage(ACLMessage.REQUEST);
        wReq.addReceiver(new AID("WPAgent", false));
        wReq.setContent("Give me the weight");
        send(wReq);
    }

    private void lockMsg() {
        ACLMessage lockCar = new ACLMessage(ACLMessage.INFORM);
        lockCar.addReceiver(new AID("CarAgent", false));
        lockCar.setContent("Lock the car");
        send(lockCar);
    }

    private void level1Alert() {
        ACLMessage phoneAlert = new ACLMessage(ACLMessage.INFORM);
        phoneAlert.addReceiver(new AID("InterfazAgent", false));
        phoneAlert.setContent("Alert level 1");
        send(phoneAlert);
    }

    protected void setup() {
        doWait(3000);
        System.out.println("Agente >> " + getLocalName() + " iniciado.");
        FSMBehaviour fsm = new FSMBehaviour(this) {
            public int onEnd() {
                System.out.println("FSM behaviour completed.");
                return super.onEnd();
            }
        };

        // CoorAgent pide el estado del motor
        fsm.registerFirstState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
//                Sending request to CarAgent
                ACLMessage engineStateReq = new ACLMessage(ACLMessage.REQUEST);
                engineStateReq.addReceiver(new AID("CarAgent", false));
                engineStateReq.setContent("Give me the engine state");
                send(engineStateReq);
            }
        }, "A");

        // CoorAgent recibe el estado del motor
        fsm.registerState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                ACLMessage engineStateResp = blockingReceive(generalTemplate);
                if (engineStateResp != null && engineStateResp.getSender().getLocalName().equals("CarAgent")) {
                    System.out.println("Coordinador recibio de >> " + engineStateResp.getSender().getName() +
                            " estado de motor >> " + engineStateResp.getContent() + "\n");
                }

            }
        }, "B");

        // CoorAgent pide el estado del peso
        fsm.registerState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
//                 Sending request to WPAgent
                ACLMessage wReq = new ACLMessage(ACLMessage.REQUEST);
                wReq.addReceiver(new AID("WPAgent", false));
                wReq.setContent("Give me the weight 1");
                send(wReq);
            }
        }, "C");


        // CoorAgent recibe el estado del peso
        fsm.registerState(new OneShotBehaviour() {
            private int exitValue;
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                ACLMessage wResp = blockingReceive(generalTemplate);
                if (wResp != null && wResp.getSender().getLocalName().equals("WPAgent")) {
                    System.out.println("Coordinador recibio de >> " + wResp.getSender().getName() +
                            " peso >> " + wResp.getContent() + "\n");
                    if (Float.parseFloat(wResp.getContent()) >= 10) {
                        ACLMessage activeInternAlarm = new ACLMessage(ACLMessage.INFORM);
                        activeInternAlarm.addReceiver(new AID("CarAgent", false));
                        activeInternAlarm.setContent("Active intern alarm");
                        send(activeInternAlarm);
                        System.out.println("Coordinador ordeno activar alarma");
                    } else {
                        exitValue = 3;
                    }
                }
            }
            public int onEnd() {
                return exitValue;
            }
        }, "D");

        // CoorAgent recibe estado de puerta del conductor
        fsm.registerState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
//                 Receiving doorState's message from CarAgent
                ACLMessage doorStateMsg = blockingReceive(generalTemplate);
                if (doorStateMsg != null && doorStateMsg.getSender().getLocalName().equals("CarAgent")) {
                    System.out.println("Coordinador recibio de >> " + doorStateMsg.getSender().getName() +
                            " puerta del conductor >> " + doorStateMsg.getContent() + "\n");
                }
            }
        }, "E");

        // CoorAgent ordena desactivar seguros
        fsm.registerState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
//              Unlocking doors
                ACLMessage unlockMsg = new ACLMessage(ACLMessage.INFORM);
                unlockMsg.addReceiver(new AID("CarAgent", false));
                unlockMsg.setContent("Unlock the doors");
                send(unlockMsg);
            }
        }, "F");

        // CoorAgent solicita y recibe estado de puerta
        fsm.registerState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                ACLMessage doorStateReq = new ACLMessage(ACLMessage.REQUEST);
                doorStateReq.addReceiver(new AID("CarAgent", false));
                doorStateReq.setContent("Give me the door's state 1");
                send(doorStateReq);
                ACLMessage doorStateResp = blockingReceive(generalTemplate);
                if (doorStateResp != null && doorStateResp.getSender().getLocalName().equals("CarAgent")) {
                    System.out.println("Coordinador recibio de >> " + doorStateResp.getSender().getName() +
                            " puerta del conductor >> " + doorStateResp.getContent() + "\n");
                }
            }
        }, "G");

        // CoorAgent inicia timer y solicita estado de puerta
        fsm.registerState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                ACLMessage startTimerMsg = new ACLMessage(ACLMessage.INFORM);
                startTimerMsg.addReceiver(new AID("CoorAgent", false));
                startTimerMsg.setContent("Start timer");
                send(startTimerMsg);

                ACLMessage doorStateReq = new ACLMessage(ACLMessage.REQUEST);
                doorStateReq.addReceiver(new AID("CarAgent", false));
                doorStateReq.setContent("Give me the door's state");
                send(doorStateReq);

                ACLMessage doorStateResp = blockingReceive(generalTemplate);
                if (doorStateResp != null && doorStateResp.getSender().getLocalName().equals("CarAgent")) {
                    System.out.println("Coordinador recibio de >> " + doorStateResp.getSender().getName() +
                            " puerta del conductor >> " + doorStateResp.getContent() + "\n");
                }
            }
        }, "H");

        // CoorAgent recibe estado de puerta y solicita peso
        fsm.registerState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                ACLMessage doorStateResp = blockingReceive(generalTemplate);
                if (doorStateResp != null && doorStateResp.getSender().getLocalName().equals("CarAgent")) {
                    System.out.println("Coordinador recibio de >> " + doorStateResp.getSender().getName() +
                            " puerta del conductor >> " + doorStateResp.getContent() + "\n");
                    doorState = Boolean.parseBoolean(doorStateResp.getContent());
                    weightReq();
                }
            }
        }, "I");

        // CoorAgent recibe estado de peso
        fsm.registerState(new OneShotBehaviour() {
            public int exitValue;
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                ACLMessage wResp = blockingReceive(generalTemplate);
                if (wResp != null && wResp.getSender().getLocalName().equals("WPAgent")) {
                    System.out.println("Coordinador recibio de >> " + wResp.getSender().getName() +
                            " peso >> " + wResp.getContent() + "\n");
                    weight = Float.parseFloat(wResp.getContent());
                    ACLMessage stopTimer = new ACLMessage(ACLMessage.INFORM);
                    stopTimer.addReceiver(new AID("CoorAgent", false));
                    stopTimer.setContent("Stop timer");
                    send(stopTimer);
                    ACLMessage resp = blockingReceive(generalTemplate);
                    if (resp != null && resp.getSender().getLocalName().equals("CoorAgent")) {
                        System.out.println("Coordinador detuvo el timer");
                    }

                    ACLMessage stopInternAlarm = new ACLMessage(ACLMessage.INFORM);
                    stopInternAlarm.addReceiver(new AID("CarAgent", false));
                    stopInternAlarm.setContent("Stop intern alarm");
                    send(stopInternAlarm);

                    if (weight < 10) {
                        lockMsg();
                        System.out.println("Caso 1 terminado: No se olvido al bebe");
                        exitValue = 3;
                    } else if (weight >= 10 && doorState == true) {
                        exitValue = 0;
                    } else if (weight >= 10 && doorState == false) {
                        exitValue = 1;
                    }
                }
            }

            public int onEnd() {
                return exitValue;
            }

        }, "J");

        fsm.registerState(new OneShotBehaviour() {
            // De J con 0
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                level1Alert();
                // Sending request to WPAgent
                ACLMessage wReq = new ACLMessage(ACLMessage.REQUEST);
                wReq.addReceiver(new AID("WPAgent", false));
                wReq.setContent("Give me the weight 2");
                send(wReq);
            }
        }, "K");

        fsm.registerState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                ACLMessage wResp = blockingReceive(generalTemplate);
                if (wResp != null && wResp.getSender().getLocalName().equals("WPAgent")) {
                    if (Float.parseFloat(wResp.getContent()) < 10) {
                        lockMsg();
                        System.out.println("Caso 2 terminado: alerta nivel 1");
                    }
                } else {
                    System.out.println("Algo paso, estado L");
                    System.out.println(wResp);
                }
            }
        }, "L");

        fsm.registerState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                level1Alert();
                ACLMessage tempReq = new ACLMessage(ACLMessage.REQUEST);
                tempReq.addReceiver(new AID("TempAgent", false));
                tempReq.setContent("Give me the temperature");
                send(tempReq);
            }
        }, "M");

        fsm.registerState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                ACLMessage tempResp = blockingReceive(generalTemplate);
                if (tempResp != null && tempResp.getSender().getLocalName().equals("TempAgent") &&
                        tempResp.getPerformative() == ACLMessage.INFORM) {
                    System.out.println("CoorAgent recibio temperatura >> " + tempResp.getContent());
                    if (Integer.parseInt(tempResp.getContent()) >= 30) {
                        ACLMessage phoneAlert = new ACLMessage(ACLMessage.INFORM);
                        phoneAlert.addReceiver(new AID("InterfazAgent", false));
                        phoneAlert.setContent("Alert level 2");
                        send(phoneAlert);

                        ACLMessage startAlarm = new ACLMessage(ACLMessage.INFORM);
                        startAlarm.addReceiver(new AID("CarAgent", false));
                        startAlarm.setContent("Start alarm");
                        send(startAlarm);

                        ACLMessage wReq = new ACLMessage(ACLMessage.REQUEST);
                        wReq.addReceiver(new AID("WPAgent", false));
                        wReq.setContent("Give me the weight 2");
                        send(wReq);
                    }
                } else {
                    System.out.println("Algo paso, estado N");
                    System.out.println(tempResp);
                }
            }
        }, "N");

        fsm.registerState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                ACLMessage wResp = blockingReceive(generalTemplate);
                if (wResp != null && wResp.getSender().getLocalName().equals("WPAgent")) {
                    if (Float.parseFloat(wResp.getContent()) < 10) {
                        lockMsg();
                        System.out.println("Caso 3 terminado: alerta nivel 2");
                    }
                }
            }
        }, "O");

        fsm.registerLastState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                System.out.println("Simulacion terminada");
            }
        }, "Z");

        fsm.registerDefaultTransition("A", "B");
        fsm.registerDefaultTransition("B", "C");
        fsm.registerDefaultTransition("C", "D");
        fsm.registerDefaultTransition("D", "E");
        fsm.registerTransition("D", "Z", 3);
        fsm.registerDefaultTransition("E", "F");
        fsm.registerDefaultTransition("F", "G");
        fsm.registerDefaultTransition("G", "H");
        fsm.registerDefaultTransition("H", "I");
        fsm.registerDefaultTransition("I", "J");
        fsm.registerTransition("J", "K", 0);
        fsm.registerTransition("J", "Z", 3);
        fsm.registerTransition("J", "M", 1);
        fsm.registerDefaultTransition("K", "L");
        fsm.registerDefaultTransition("L", "Z");
        fsm.registerDefaultTransition("M", "N");
        fsm.registerDefaultTransition("N", "O");
        fsm.registerDefaultTransition("O", "Z");
        addBehaviour(fsm);

    }

}
