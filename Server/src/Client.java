import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private String nom;
    private String serverIP;
    private int serverPort;
    
    public static void main(String[] args) throws SocketException, UnknownHostException {
        Scanner scanner = new Scanner(System.in);

        // Demander les informations à l'utilisateur
        System.out.print("Entrez votre nom: ");
        String nom = scanner.nextLine();
        
        System.out.print("Entrez l'adresse IP du serveur: ");
        String serverIP = scanner.nextLine();
        
        System.out.print("Entrez le port du serveur: ");
        int serverPort = scanner.nextInt();
        
        // Initialiser le client avec les informations saisies
        Client client = new Client(nom, serverIP, serverPort);
        client.run();
        
        scanner.close();
    }

    public Client(String nom, String serverIP, int serverPort){
        this.nom = nom;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public void run(){
        DatagramSocket broadCastSock = null;
        try{   
            broadCastSock = new DatagramSocket();
            InetAddress servInetAddress = InetAddress.getByName(serverIP);

            byte[] receivedData = new byte[1024];
            byte[] sendData = ("hello serveur RX302").getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, servInetAddress, serverPort);
            broadCastSock.send(sendPacket);
            System.out.println("Message envoyé au serveur.");

            DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
            broadCastSock.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            InetAddress serverIP = receivePacket.getAddress();
            int serverPort = receivePacket.getPort();

            System.out.println("Réponse du serveur : " + response + " de @" + serverIP.getHostAddress() + " : " + serverPort);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (broadCastSock != null && !broadCastSock.isClosed()) {
                broadCastSock.close();
            }
        }
    }
}