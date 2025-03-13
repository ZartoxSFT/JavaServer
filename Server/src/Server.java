import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.net.UnknownHostException;

public class Server {
    public static final String serverIP = "127.0.0.1";
    public static final String serverMsg = "hello serveur RX302";
    public static final String serverResponse = "Serveur RX302 ready";
    
    private ArrayList<ClientInfo> clients = new ArrayList<>();
    
    private UDPIO udpio = new UDPIO();
    private DataOutputStream sendStream = udpio.getOutput();
    private DataInputStream receiveStream = udpio.getInput();

    

    public static void main(String args[]) {
        try {
            UDPSocketScanner.scanUDPPorts(1, 200, serverIP);
            Server server = new Server();
            server.run();
        } catch (SocketException e) {
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Constructeur de la classe Server
     */
    public Server() {}

    /**
     * Méthode qui exécute le serveur Java
     */
    private void run() {
        DatagramSocket socket = udpio.getSocket();
        try {
            int serverPort = -1;
            for (int port : UDPSocketScanner.availablePorts) {
                serverPort = port;
                break;
            }
    
            if (serverPort == -1) {
                userPrint("Aucun port disponible dans la plage spécifiée.");
                return;
            }
    
            userPrint("Serveur en écoute sur le port : " + serverPort);
    
            InetSocketAddress address = new InetSocketAddress("0.0.0.0", serverPort);
            socket.bind(address);
            udpio.getReceivePacket().setPort(serverPort);
            userPrint("Java_Server en écoute...");
    
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                for (ClientInfo clientInfo : clients) {
                    try {
                        sendStream.writeByte(0x7F);
                        udpio.sendData(clientInfo.getAddress(), clientInfo.getPort());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }));
    
            while (true) {
                udpio.receiveData();
                InetAddress clientAddress = udpio.getReceivePacket().getAddress();
                int clientPort = udpio.getReceivePacket().getPort();
                switch (receiveStream.readByte()) {
                    case 1:
                        treatMessage(socket, clientAddress, clientPort);
                    break;

                    case 0x7F:
                      ClientInfo leaveClient = getUser(clientAddress, clientPort);
                      Server.userPrint("Le client " + leaveClient.name + " s'est déconnecté.");
                      clients.remove(leaveClient);
                    break;
                
                    default:
                        break;
                }
            }

        } catch (BindException e) {
            userPrint("Port du socket déjà attribué, un serveur tourne probablement en arrière-plan");
            System.exit(-1);
        } catch (Exception e) {
            userPrint("Impossible de créer le socket");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Méthode qui traite un message reçu par le serveur
     * @param socket
     * @param clientAddress
     * @param clientPort
     */
    public void treatMessage(DatagramSocket socket, InetAddress clientAddress, int clientPort) {
        try {
            String message = receiveStream.readUTF();
            ClientInfo clientInfo = getUser(clientAddress, clientPort);
            if (clientInfo != null && message.charAt(0) != '/') {
                userPrint(clientInfo.name + " : " + message);
            }
            if (message.startsWith(serverMsg)) {
                receiveStream.readByte();
                String name = receiveStream.readUTF();
                clients.add(new ClientInfo(clientAddress, clientPort, name));
                userPrint(name + " s'est connecté sur le port : " + clientPort);
                sendStream.writeUTF(serverResponse);
                udpio.sendData(clientAddress, clientPort);
                userPrint("Réponse envoyée au client.");
            } else if (message.charAt(0) == '/') {
                switch (message.substring(1, message.indexOf(" "))) {
                    case "msg":
                        String[] parts = message.split(" ", 3);
                        String targetName = parts[1];
                        String msg = parts[2];
                        for (ClientInfo client : clients) {
                            if (client.name.equals(targetName)) {
                                sendStream.writeByte(1);
                                sendStream.writeUTF("Message reçu de " + getUser(clientAddress, clientPort).name + " : " + msg + "\n" + "Entrez un message à envoyer :");
                                udpio.sendData(client.getAddress(), client.getPort());
                                break;
                            }
                        }

                    case "all":
                    String[] partsclient = message.split(" ", 2);
                    String allclients = partsclient[1];
                    if (allclients.equals("clients")) {
                        for (ClientInfo client : clients) {
                                sendStream.writeByte(1);
                                sendStream.writeUTF(getUser(client.getAddress(), client.getPort()).name);
                                udpio.sendData(clientAddress, clientPort);
                         }
                        sendStream.writeByte(1);
                        sendStream.writeUTF("Entrez un message à envoyer :");
                        udpio.sendData(clientAddress, clientPort);

                        }                               
            break;
                    default:
                        break;
                }
            } else {
                relayMessageToClients(socket, message, clientAddress, clientPort);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui relaie un message à tous les clients connectés
     * @param socket
     * @param message
     * @param senderAddress
     * @param senderPort
     * @throws IOException
     */
    private void relayMessageToClients(DatagramSocket socket, String message, InetAddress senderAddress, int senderPort)throws IOException {

        for (ClientInfo client : clients) {
            if (!client.getAddress().equals(senderAddress) || client.getPort() != senderPort) {
                    sendStream.writeByte(1);
                    sendStream.writeUTF(getUser(senderAddress, senderPort).name + " : " + message);
                    udpio.sendData(client.getAddress(), client.getPort());

            }
        }
    }

    /**
     * Méthode qui affiche un message à l'utilisateur 
     * @param text
     */
    public static void userPrint(String text) {
        System.out.println(text);
    }

    /**
     * Méthode qui retourne un client en fonction de son adresse et de son port
     * @param address
     * @param port
     * @return
     */
    public ClientInfo getUser(InetAddress address, int port){
        for(ClientInfo client : clients){
            if(client.getAddress().equals(address) && client.getPort() == port){
                return client;
            }
        }
        return null;
    }

    /**
     * Classe qui permet de scanner les ports UDP
     */
    private static class ClientInfo {
        private final InetAddress address;
        private final int port;
        private final String name;

        /**
         * Constructeur de la classe ClientInfo
         * @param address
         * @param port
         * @param name
         */
        public ClientInfo(InetAddress address, int port, String name) {
            this.address = address;
            this.port = port;
            this.name = name;
        }

        /**
         * Méthode qui retourne le nom du client
         * @return
         */
        public InetAddress getAddress() {
            return address;
        }

        /**
         * Méthode qui retourne le port du client
         * @return
         */
        public int getPort() {
            return port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClientInfo that = (ClientInfo) o;
            return port == that.port && address.equals(that.address);
        }

        @Override
        public int hashCode() {
            return address.hashCode() + port;
        }
    }
}