import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
    
    public static void main(String args[]) throws SocketException,UnknownHostException {
        UDPSocketScanner.scanUDPPorts(1,200 , Server.serverIP);
        Client client = new Client();


    }

    public Client(){

    }


    public void run(){
        
    }
}
