import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Server {
    public int freeport;
    public static final String serverIP = "127.0.0.1";
    public static final String serverMsg = "hello serveur RX302";
    public static final String serverResponse = "Serveur RX302 ready";



    public static void main(String args[]) throws SocketException,UnknownHostException {
        int port = 0;
        Server server = new Server(port);
        server.run();

    } 

    public Server(int port){
        freeport = port;
    }


    private void run(){
        //setName("Server thread");
        DatagramSocket broadCastSock = null;
        try{
            broadCastSock = new DatagramSocket(null);
            InetSocketAddress address = new InetSocketAddress(serverIP, freeport);
            broadCastSock.bind(address);
            byte[] receivedData = new byte[1024];
            userPrint("Java_Server...");

            while(true){
                DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
                broadCastSock.receive(receivePacket);
                
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress clientAdress = receivePacket.getAddress();

                if(message.equals(serverMsg)){
                    userPrint("Nouveau client : " + clientAdress.getHostAddress() + " : " + freeport);
                    
                    byte[] sendData = serverResponse.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAdress, freeport);
                    broadCastSock.send(sendPacket);
                }

            }

            

        }catch(BindException e) {
			userPrint("Port du socket déja attribué, un serveur tourne probablement en arriére plan");
			System.exit(-1);;
		}
		catch(Exception e) {
			userPrint("Impossible de créer le socket");
			e.printStackTrace();
			System.exit(-1);;
		}
        finally{
            if (broadCastSock != null && !broadCastSock.isClosed()) {
                broadCastSock.close();
            }
        }
    }
    

    public static void userPrint(String text) {
		System.out.println(text);
	}
}
