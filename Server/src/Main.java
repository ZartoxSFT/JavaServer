import java.net.SocketException;

public class Main {

    public static void main(String args[]) throws SocketException {
        UDPSocketScanner.scanUDPPort(1,100,"localhost");

    }
}
