import agents.*;

/**
 * Created by gerardo on 24/05/16.
 */
public class Main {

    public static void main(String[] args) {

        jade.Boot.main(new String[] {
                "-gui",
//                "CarAgent:" + CarAgent.class.getName() + ";CoorAgent:" + CoorAgent.class.getName() +
//                        ";PPAgent:" + PPAgent.class.getName() + ";TempAgent:" + TempAgent.class.getName()
                "TempAgent:" + TempAgent.class.getName() + ";CoorAgent:" + CoorAgent.class.getName()
        });

    }

}
