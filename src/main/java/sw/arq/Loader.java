package sw.arq;

import sw.arq.component.Delivery;
import sw.arq.component.Server;

/**
 * Hello world!
 *
 */
public class Loader {
    public static void main( String[] args ) {
        //receiver
        String message = "i'm a message.";
        new Server(6888, message.getBytes().length).start();
        //sender
        new Delivery(6888, message.getBytes()).start();
    }
}
