import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {

    public static void main(String args[]) throws SocketException,UnknownHostException {
        UDPSocketScanner.scanUDPPorts(1,200,"127.0.0.1");

    }
}
