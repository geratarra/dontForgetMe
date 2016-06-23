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

import java.util.Random;

/**
 * Created by gerardo on 24/05/16.
 */
public class CoorAgent extends Agent {

    MessageTemplate informTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

    private static boolean doorState;
    private static boolean engineState;
    private static boolean timer;
    private static int temperature;
    private static float weight;

    /**
     * Envia mensaje de peticion de peso al agente CarAgent
     * @return void
     */
    private void weightReq() {
        // El mensaje sera tipo REQUEST
        ACLMessage wReq = new ACLMessage(ACLMessage.REQUEST);
        // Se anade el receptor (usando un identificador AID)
        wReq.addReceiver(new AID("WPAgent", false));
        // Se agrega el contenido
        wReq.setContent("Give me the weight");
        // Se envia el mensaje
        send(wReq);
    }

    /**
     * Envia mensaje al agente CarAgent informando que debe bloquear los seguros del auto
     * @return void
     */
    private void lockMsg() {
        // El mensaje sera tipo INFORM
        ACLMessage lockCar = new ACLMessage(ACLMessage.INFORM);
        lockCar.addReceiver(new AID("CarAgent", false));
        lockCar.setContent("Lock the car");
        send(lockCar);
    }

    /**
     * Envia mensaje al agente CarAgent informando que debe desbloquear los seguros del auto
     * @return void
     */
    private void unlockMsg() {
        // El mensaje sera tipo INFORM
        ACLMessage unlockMsg = new ACLMessage(ACLMessage.INFORM);
        unlockMsg.addReceiver(new AID("CarAgent", false));
        unlockMsg.setContent("Unlock the doors");
        send(unlockMsg);
    }

    /**
     * Envia mensaje al agente InterfazAgent, informando que debe activar la alerta nivel 1
     * @return void
     */
    private void level1Alert() {
        // El mensaje sera tipo INFORM
        ACLMessage phoneAlert = new ACLMessage(ACLMessage.INFORM);
        phoneAlert.addReceiver(new AID("InterfazAgent", false));
        phoneAlert.setContent("Alert level 1");
        send(phoneAlert);
    }

    /**
     * Envia mensaje al agente InterfazAgent, informando que debe activar la alerta nivel 2
     * @return void
     */
    private void level2Alert() {
        // El mensaje es tipo INFORM
        ACLMessage phoneAlert = new ACLMessage(ACLMessage.INFORM);
        phoneAlert.addReceiver(new AID("InterfazAgent", false));
        phoneAlert.setContent("Alert level 2");
        send(phoneAlert);
    }

    /**
     * Envia mensaje al agente CoorAgent (a si mismo), indicando que debe detener el timer
     * @return void
     */
    private void stopTimerMsg() {
        // El atributo timer en false indica que el timer esta detendio
        timer = false;
        ACLMessage stopTimer = new ACLMessage(ACLMessage.INFORM);
        stopTimer.addReceiver(new AID("CoorAgent", false));
        stopTimer.setContent("Stop timer");
        send(stopTimer);
        // Aqui mismo se recibe el mensaje y se imprime
        ACLMessage resp = blockingReceive(informTemplate);
        if (resp != null && resp.getSender().getLocalName().equals("CoorAgent")) {
            System.out.println("Coordinador detuvo el timer");
        }
    }

    /**
     * Envia mensaje al agente CarAgent haciendo peticion del estado del motor
     * @return void
     */
    private void engineStateMsg() {
        // El mensaje es tipo REQUEST
        ACLMessage engineStateReq = new ACLMessage(ACLMessage.REQUEST);
        engineStateReq.addReceiver(new AID("CarAgent", false));
        engineStateReq.setContent("Give me the engine state");
        send(engineStateReq);
    }

    /**
     * Todo agente debe tener su metodo 'setup'. En este se indica todo su comportamiento con el entorno
     * @return void
     */
    protected void setup() {
        // Cada agente en cuanto inicia tiene un retraso de 3 segundos, esto con el fin de que
        // el sniffer se inicie de manera correcta y detecte a todos los agentes
        doWait(3000);
        System.out.println("Agente >> " + getLocalName() + " iniciado.");
        // Se crea el objeto maquina de estados finitos
        FSMBehaviour fsm = new FSMBehaviour(this) {

            // Esta funcion se ejecuta al finalizar el proceso de la maquina de estados finitos
            public int onEnd() {
                System.out.println("FSM behaviour completed.");
                return super.onEnd();
            }
        };

        // Se registra el primer estado. Todos los estados reciben un comportamiento
        // y el nombre del estado. En este caso todos los estados registrados
        // reciben como argumento un comportamiento de tipo OneShotBehaviour, este
        // comportamiento indica que se ejecutara una sola vez.
        // // Se pide el estado del motor del auto
        fsm.registerFirstState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                engineStateMsg();
            }
        }, "A");

        // CoorAgent recibe el estado del motor (el cual sera false)
        fsm.registerState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                ACLMessage engineStateResp = blockingReceive(informTemplate);
                if (engineStateResp != null && engineStateResp.getSender().getLocalName().equals("CarAgent")) {
                    System.out.println("Coordinador recibio de >> " + engineStateResp.getSender().getName() +
                            " estado de motor >> " + engineStateResp.getContent() + "\n");
                    engineState = Boolean.parseBoolean(engineStateResp.getContent());
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


        // CoorAgent recibe el estado del peso y pide el estado de la puerta
        fsm.registerState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                ACLMessage wResp = blockingReceive(informTemplate);
                if (wResp != null && wResp.getSender().getLocalName().equals("WPAgent")) {
                    System.out.println("Coordinador recibio de >> " + wResp.getSender().getName() +
                            " peso >> " + wResp.getContent() + "\n");
                    weight = Float.parseFloat(wResp.getContent());
                    ACLMessage doorStateReq = new ACLMessage(ACLMessage.REQUEST);
                    doorStateReq.addReceiver(new AID("CarAgent", false));
                    doorStateReq.setContent("Give me the door's state 1");
                    send(doorStateReq);
                }
            }
        }, "D");

        // CoorAgent recibe estado de puerta
        fsm.registerState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                ACLMessage doorStateMsg = blockingReceive(informTemplate);
                if (doorStateMsg != null && doorStateMsg.getSender().getLocalName().equals("CarAgent")) {
                    System.out.println("Coordinador recibio de >> " + doorStateMsg.getSender().getName() +
                            " puerta del conductor >> " + doorStateMsg.getContent() + "\n");
                    doorState = Boolean.parseBoolean(doorStateMsg.getContent());
                    // Si el peso que se recibio anteriormente es mayor o igual a 10, el motor esta
                    // apagado y la puerta abierta, se informa a CoorAgent que inicie el timer
                    if (weight >= 10 && engineState == false && doorState == true) {
                        ACLMessage startTimerMsg = new ACLMessage(ACLMessage.INFORM);
                        startTimerMsg.addReceiver(new AID("CoorAgent", false));
                        startTimerMsg.setContent("Start timer");
                        send(startTimerMsg);
                        // Se envia y recibe el mensaje de informe para iniciar el timer
                        ACLMessage resp = blockingReceive(informTemplate);
                        if (resp != null && resp.getSender().getLocalName().equals("CoorAgent")) {
                            System.out.println("Coordinador inicio el timer");
                            timer = true;
                        }

                        // Se envia mensaje a CarAgent para que bloquee los seguros del auto
                        lockMsg();

                        // Se envia mensaje a CarAgent para que inicie la alarma interna del auto
                        ACLMessage startInternAlarm = new ACLMessage(ACLMessage.INFORM);
                        startInternAlarm.setContent("Active intern alarm");
                        startInternAlarm.addReceiver(new AID("CarAgent", false));
                        send(startInternAlarm);
                    }
                }
            }
        }, "E");

        // CoorAgent hace peticion de peso a WPAgent
        fsm.registerState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                weightReq();
            }
        }, "F");

        // CoorAgent recibe estado de peso
        fsm.registerState(new OneShotBehaviour() {
            private int exitValue;
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                ACLMessage wResp = blockingReceive(informTemplate);
                if (wResp != null && wResp.getSender().getLocalName().equals("WPAgent")) {
                    System.out.println("Coordinador recibio de >> " + wResp.getSender().getName() +
                            " peso >> " + wResp.getContent() + "\n");
                    weight = Float.parseFloat(wResp.getContent());
                    // Aqui termina el caso 1 (no se olvida al bebe)
                    // Si el peso es menor a diez, se manda mensaje para que el timer se detenga
                    if (weight < 10) {
                        stopTimerMsg();

                        // Se envia mensaje a CarAgent para que la alarma interna se detenga
                        ACLMessage stopInternAlarm = new ACLMessage(ACLMessage.INFORM);
                        stopInternAlarm.addReceiver(new AID("CarAgent", false));
                        stopInternAlarm.setContent("Stop intern alarm");
                        send(stopInternAlarm);

                        // Se envia mensaje a CarAgent para que desbloquee los seguros
                        unlockMsg();
                        System.out.println("Caso 1 terminado: no se olvido al bebe");
                        exitValue = 1;
                    } else {
                        engineStateMsg();
                        exitValue = 2;
                    }
                }
            }

            //
            public int onEnd() {
                return exitValue;
            }
        }, "G");

        // CoorAgent inicia timer y solicita estado de puerta
        fsm.registerState(new OneShotBehaviour() {
            private int exitValue;
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                ACLMessage engineStateResp = blockingReceive(informTemplate);
                if (engineStateResp != null && engineStateResp.getSender().getLocalName().equals("CarAgent")) {
                    System.out.println("Coordinador recibio de >> " + engineStateResp.getSender().getName() +
                            " estado del motor >> " + engineStateResp.getContent() + "\n");
                    engineState = Boolean.parseBoolean(engineStateResp.getContent());
                    if (engineState == false) {
                        stopTimerMsg();

                        ACLMessage stopInternAlarm = new ACLMessage(ACLMessage.INFORM);
                        stopInternAlarm.addReceiver(new AID("CarAgent", false));
                        stopInternAlarm.setContent("Stop intern alarm");
                        send(stopInternAlarm);

                        level1Alert();

                        weightReq();
                        exitValue = 2;
                    } else {
                        exitValue = 1;
                    }
                }
            }

            // Todos los estados llevan la funcion onEnd, la cual regresa un valor de salida.
            // Las funciones de transicion de cada estado dependen de este valor de salida
            public int onEnd() {
                return exitValue;
            }
        }, "H");

        // CoorAgent recibe estado de peso
        fsm.registerState(new OneShotBehaviour() {
            private int exitValue;
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                ACLMessage wResp = blockingReceive(informTemplate);
                if (wResp != null && wResp.getSender().getLocalName().equals("WPAgent")) {
                    System.out.println("Coordinador recibio de >> " + wResp.getSender().getName() +
                            " peso >> " + wResp.getContent() + "\n");
                    weight = Float.parseFloat(wResp.getContent());
                    // Si el peso es menor a 10, entonces termina el caso 2
                    if (weight < 10) {
                        // Se envia mensaje a CarAgent para que desbloquee los seguros
                        unlockMsg();
                        System.out.println("Caso 2 terminado: alerta nivel 1");
                        exitValue = 1;
                    } else {
                        exitValue = 2;
                    }
                }
            }
            public int onEnd() { return exitValue; }
        }, "I");


        // CoorAgent pide a TempAgent la temperatura
        fsm.registerState(new OneShotBehaviour() {
            public void action() {
                System.out.println("Executing behaviour " + getBehaviourName());
                ACLMessage tempReq = new ACLMessage(ACLMessage.REQUEST);
                tempReq.addReceiver(new AID("TempAgent", false));
                tempReq.setContent("Give me the temperature");
                send(tempReq);
            }
        }, "J");

        // Se recibe de TempAgent la temperatura
        fsm.registerState(new OneShotBehaviour() {
            private int exitValue;
            public void action() {
                ACLMessage tempResp = blockingReceive(informTemplate);
                if (tempResp != null && tempResp.getSender().getLocalName().equals("TempAgent")) {
                    temperature = Integer.parseInt(tempResp.getContent());
                    // Si es mayor a 30 grados se envia una alerta nivel 2
                    if (temperature >= 30) {
                        level2Alert();
                        System.out.println("Alertando a proteccion civil..");
                        // Se envia mensaje a CarAgent para que inicie la alarma del auto
                        ACLMessage alarmMsg = new ACLMessage(ACLMessage.INFORM);
                        alarmMsg.addReceiver(new AID("CarAgent", false));
                        alarmMsg.setContent("Start alarm");
                        send(alarmMsg);
                        System.out.println("Caso 3 terminado: alerta nivel 2");
                        exitValue = 2;
                    } else {
                        exitValue = 1;
                    }
                }
            }

            public int onEnd() { return exitValue; }
        }, "K");
//
//        fsm.registerState(new OneShotBehaviour() {
//            // De J con 0
//            public void action() {
//                System.out.println("Executing behaviour " + getBehaviourName());
//                level1Alert();
//                // Sending request to WPAgent
//                ACLMessage wReq = new ACLMessage(ACLMessage.REQUEST);
//                wReq.addReceiver(new AID("WPAgent", false));
//                wReq.setContent("Give me the weight 2");
//                send(wReq);
//            }
//        }, "K");
//
//        fsm.registerState(new OneShotBehaviour() {
//            public void action() {
//                System.out.println("Executing behaviour " + getBehaviourName());
//                ACLMessage wResp = blockingReceive(generalTemplate);
//                if (wResp != null && wResp.getSender().getLocalName().equals("WPAgent")) {
//                    if (Float.parseFloat(wResp.getContent()) < 10) {
//                        lockMsg();
//                        System.out.println("Caso 2 terminado: alerta nivel 1");
//                    }
//                } else {
//                    System.out.println("Algo paso, estado L");
//                    System.out.println(wResp);
//                }
//            }
//        }, "L");
//
//        fsm.registerState(new OneShotBehaviour() {
//            public void action() {
//                System.out.println("Executing behaviour " + getBehaviourName());
//                level1Alert();
//                ACLMessage tempReq = new ACLMessage(ACLMessage.REQUEST);
//                tempReq.addReceiver(new AID("TempAgent", false));
//                tempReq.setContent("Give me the temperature");
//                send(tempReq);
//            }
//        }, "M");
//
//        fsm.registerState(new OneShotBehaviour() {
//            public void action() {
//                System.out.println("Executing behaviour " + getBehaviourName());
//                ACLMessage tempResp = blockingReceive(generalTemplate);
//                if (tempResp != null && tempResp.getSender().getLocalName().equals("TempAgent") &&
//                        tempResp.getPerformative() == ACLMessage.INFORM) {
//                    System.out.println("CoorAgent recibio temperatura >> " + tempResp.getContent());
//                    if (Integer.parseInt(tempResp.getContent()) >= 30) {
//                        ACLMessage phoneAlert = new ACLMessage(ACLMessage.INFORM);
//                        phoneAlert.addReceiver(new AID("InterfazAgent", false));
//                        phoneAlert.setContent("Alert level 2");
//                        send(phoneAlert);
//
//                        ACLMessage startAlarm = new ACLMessage(ACLMessage.INFORM);
//                        startAlarm.addReceiver(new AID("CarAgent", false));
//                        startAlarm.setContent("Start alarm");
//                        send(startAlarm);
//
//                        ACLMessage wReq = new ACLMessage(ACLMessage.REQUEST);
//                        wReq.addReceiver(new AID("WPAgent", false));
//                        wReq.setContent("Give me the weight 2");
//                        send(wReq);
//                    }
//                } else {
//                    System.out.println("Algo paso, estado N");
//                    System.out.println(tempResp);
//                }
//            }
//        }, "N");
//
//        fsm.registerState(new OneShotBehaviour() {
//            public void action() {
//                System.out.println("Executing behaviour " + getBehaviourName());
//                ACLMessage wResp = blockingReceive(generalTemplate);
//                if (wResp != null && wResp.getSender().getLocalName().equals("WPAgent")) {
//                    if (Float.parseFloat(wResp.getContent()) < 10) {
//                        lockMsg();
//                        System.out.println("Caso 3 terminado: alerta nivel 2");
//                    }
//                }
//            }
//        }, "O");

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
        fsm.registerDefaultTransition("E", "F");
        fsm.registerDefaultTransition("F", "G");
        fsm.registerTransition("G", "H", 2);
        fsm.registerTransition("G", "Z", 1);
        fsm.registerTransition("H", "Z", 1);
        fsm.registerTransition("H", "I", 2);
        fsm.registerTransition("I", "Z", 1);
        fsm.registerTransition("I", "J", 2);
        fsm.registerDefaultTransition("J", "K");
        fsm.registerTransition("K", "Z", 2);
        fsm.registerTransition("K", "J", 1);

        addBehaviour(fsm);

    }

}
