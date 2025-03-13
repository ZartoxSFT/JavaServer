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

    /**
     * Constructeur de la classe Client
     * @param nom
     * @param serverIP
     * @param serverPort
     */
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

    /**
     * Méthode qui permet de lancer le client et de communiquer avec le serveur
     * @param scanner
     */
    public void run(Scanner scanner){
        try{ 
            sendStream.writeByte(1);
            sendStream.writeUTF(Server.serverMsg);
            sendStream.writeByte(1);
            sendStream.writeUTF(nom);

            

            udpio.sendData(this.servInetAddress,this.serverPort);

            Server.userPrint("Connexion au serveur...");

           
            udpio.getSocket().setSoTimeout(2000);
            try{
                udpio.receiveData();
                String response = receiveStream.readUTF();
                Server.userPrint("Réponse du serveur : " + response);
            }
            catch(Exception e){
                Server.userPrint("Impossible de se connecter au serveur.");
                System.exit(0);
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                       sendStream.writeByte(0x7F);
                       udpio.sendData(this.servInetAddress,this.serverPort);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               
           }));

            udpio.getSocket().setSoTimeout(0);

            new Thread(() -> {
                try {
                    while (true) {
                        
                        udpio.receiveData();
                        switch (receiveStream.readByte()) {
                            case 1:
                            String message = receiveStream.readUTF();
                            Server.userPrint(message);
                                break;

                            case 0x7F:
                                Server.userPrint("Le serveur a fermé la connexion.");
                                System.exit(0);
                                break;
                        
                            default:
                                break;
                        }
                      
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();


            while (true) {
                
                System.out.print("Entrez un message à envoyer : \n" + //
                                        "");
                String message = scanner.nextLine();
                sendStream.writeByte(1);
                sendStream.writeUTF(message);
                udpio.sendData(this.servInetAddress,this.serverPort);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

   
}