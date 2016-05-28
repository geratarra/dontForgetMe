package agents;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Random;

/**
 * Created by gerardo on 24/05/16.
 */
public class PPAgent extends Agent {

    public static float weight;
    public static float presure;

    protected void setup(){

        System.out.println("Agente >> " + getLocalName() + " iniciado.");
        addBehaviour(new TickerBehaviour(this, 2000) {
            @Override
            protected void onTick() {

                weight = new Random().nextFloat()*(15 - 5) + 5;
//                System.out.println("Peso en el auto >> " + weight + " Kg");

                presure = new Random().nextInt(150 - 100) + 100;
//                System.out.println("Presion en el auto >> " + presure + " N/m^2");
            }
        });

    }

}
