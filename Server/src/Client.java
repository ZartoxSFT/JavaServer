import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javafx.scene.chart.PieChart.Data;

public class Client {
    public static String nom;
    
    public static void main(String args[]) throws SocketException,UnknownHostException {
        UDPSocketScanner.scanUDPPorts(1,200 , Server.serverIP);
        nom = "Client";
        Client client = new Client(nom);
        client.run();
        
    }

    public Client(String nom){
        this.nom = nom;
    }


    public void run(){
        DatagramSocket broadCastSock = null;
        try{   
            broadCastSock = new DatagramSocket();
            InetAddress currAdress = InetAddress.getByName("localhost");
            InetAddress servInetAddress = InetAddress.getByName(Server.serverIP);

            byte[] receivedData = new byte[1024];
            byte[] sendData = Server.serverMsg.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, servInetAddress, Server.freeport);
            broadCastSock.send(sendPacket);


            DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
            broadCastSock.receive(receivePacket);



            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            InetAddress serverIP = receivePacket.getAddress();
            int serverPort = receivePacket.getPort();

            Server.userPrint(response + " : @" + serverIP.getHostAddress() + " : " + serverPort);


        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (broadCastSock != null && !broadCastSock.isClosed()) {
                broadCastSock.close();
            }
        }
    }
}
