import agents.*;

/**
 * Created by gerardo on 24/05/16.
 */
public class Main {

    public static void main(String[] args) {

        jade.Boot.main(new String[] {
                "-gui",
                "sniffer:jade.tools.sniffer.Sniffer;CarAgent:" + CarAgent.class.getName() +
                        ";CoorAgent:" + CoorAgent.class.getName() + ";WPAgent:" + WPAgent.class.getName() +
                        ";InterfazAgent:" + InterfazAgent.class.getName() + ";TempAgent:" +
                        TempAgent.class.getName()
        });
    }
}
