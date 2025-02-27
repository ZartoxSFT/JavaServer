import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static int freeport = 1;
    public static final String serverIP = "127.0.0.1";
    public static final String serverMsg = "hello serveur RX302";
    public static final String serverResponse = "Serveur RX302 ready";
    public static List<Integer> availablePorts = new ArrayList<>();

    public static void main(String args[]) throws SocketException, UnknownHostException {
        UDPSocketScanner.scanUDPPorts(1, 200, serverIP);
        Server server = new Server();
        server.run();
    }

    public Server(){}

    private void run(){
        DatagramSocket broadCastSock = null;
        try{
            int serverPort = -1;
            for (int result : UDPSocketScanner.availablePorts) {
                serverPort = result;
                freeport = result;
                break;
            }

            if (serverPort == -1) {
                System.out.println("Aucun port disponible dans la plage spécifiée.");
                return;
            }

            System.out.println("Serveur en écoute sur le port : " + serverPort);

            broadCastSock = new DatagramSocket(null);
            InetSocketAddress address = new InetSocketAddress(serverIP, serverPort);
            broadCastSock.bind(address);
            byte[] receivedData = new byte[1024];
            userPrint("Java_Server...");

            while(true){
                DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
                broadCastSock.receive(receivePacket);
                
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                userPrint("Message reçu : " + message + " de " + clientAddress.getHostAddress() + ":" + clientPort);

                if(message.equals(serverMsg)){
                    userPrint("Nouveau client : " + clientAddress.getHostAddress() + " : " + clientPort);
                    
                    byte[] sendData = serverResponse.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                    broadCastSock.send(sendPacket);
                    userPrint("Réponse envoyée au client.");
                } else {
                    userPrint("Message inattendu reçu : " + message);
                }
            }
        } catch(BindException e) {
            userPrint("Port du socket déjà attribué, un serveur tourne probablement en arrière-plan");
            System.exit(-1);
        } catch(Exception e) {
            userPrint("Impossible de créer le socket");
            e.printStackTrace();
            System.exit(-1);
        } finally{
            if (broadCastSock != null && !broadCastSock.isClosed()) {
                broadCastSock.close();
            }
        }
    }

    public static void userPrint(String text) {
        System.out.println(text);
    }
}