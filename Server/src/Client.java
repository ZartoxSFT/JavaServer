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
        scanner.nextLine(); // Consommer la nouvelle ligne
        
        // Initialiser le client avec les informations saisies
        Client client = new Client(nom, serverIP, serverPort);
        client.run(scanner);
        
        scanner.close();
    }

    public Client(String nom, String serverIP, int serverPort){
        this.nom = nom;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public void run(Scanner scanner){
        final DatagramSocket socket;
        try{   
            socket = new DatagramSocket();
            InetAddress servInetAddress = InetAddress.getByName(serverIP);

            byte[] receivedData = new byte[1024];
            byte[] sendData = Server.serverMsg.getBytes(); // Envoi du message exact attendu par le serveur

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, servInetAddress, serverPort);
            socket.send(sendPacket);
            System.out.println("Message envoyé au serveur.");

            DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Réponse du serveur : " + response);

            // Démarrer un thread pour recevoir les messages
            new Thread(() -> {
                try {
                    while (true) {
                        socket.receive(receivePacket);
                        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        System.out.println("Message reçu : " + message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            // Boucle pour envoyer des messages
            while (true) {
                System.out.print("Entrez un message à envoyer : ");
                String message = scanner.nextLine();
                sendData = (nom + ": " + message).getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, servInetAddress, serverPort);
                socket.send(sendPacket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}