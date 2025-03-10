import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.net.DatagramPacket;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private String nom;
    private String serverIP;
    private int serverPort;
    private InetAddress servInetAddress;
    private UDPIO udpio = new UDPIO();
    private DataOutputStream sendStream = udpio.getOutput();
    private DataInputStream receiveStream = udpio.getInput();



    
    public static void main(String[] args) throws SocketException, UnknownHostException {
        Scanner scanner = new Scanner(System.in);
        


        System.out.print("Entrez votre nom : ");
        String nom = scanner.nextLine();
        
        System.out.print("Entrez l'adresse IP du serveur : ");
        String serverIP = scanner.nextLine();
        
        System.out.print("Entrez le port du serveur : ");
        int serverPort = scanner.nextInt();
        scanner.nextLine(); 
        
        Client client = new Client(nom, serverIP, serverPort);
        client.run(scanner);
        
        scanner.close();
    }

    public Client(String nom, String serverIP, int serverPort){
        this.nom = nom;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        
        try{
            servInetAddress = InetAddress.getByName(serverIP);  
        } catch (Exception e) {
            e.printStackTrace();
        }

       
    }

    public void run(Scanner scanner){
        try{   
            sendStream.writeUTF(Server.serverMsg);
            sendStream.writeUTF(nom);

            udpio.sendData(this.servInetAddress,this.serverPort);

            System.out.println("Message envoyé au serveur.");

           

            udpio.receiveData();
            String response = receiveStream.readUTF();

            System.out.println("Réponse du serveur : " + response);

            new Thread(() -> {
                try {
                    while (true) {
                        udpio.receiveData();
                        String message = receiveStream.readUTF();
                        System.out.println("Message reçu : " + message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            while (true) {
                
                System.out.print("Entrez un message à envoyer : ");
                String message = scanner.nextLine();
                sendStream.writeUTF(message);
                udpio.sendData(this.servInetAddress,this.serverPort);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

   
}