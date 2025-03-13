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
import java.util.List;
import java.util.Set;
import java.net.UnknownHostException;

public class Server {
    public static final String serverIP = "127.0.0.1";
    public static final String serverMsg = "hello serveur RX302";
    public static final String serverResponse = "Serveur RX302 ready";
    
    private ArrayList<Client> clients = new ArrayList<>();
    
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
                for (Client clientInfo : clients) {
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
                String message = receiveStream.readUTF();
                InetAddress clientAddress = udpio.getReceivePacket().getAddress();
                int clientPort = udpio.getReceivePacket().getPort();
            	if (message.startsWith(serverMsg)) {
                    String name = receiveStream.readUTF();
                    int foundPort = -1;
                    scanPortLoop:
                    for (int port : UDPSocketScanner.availablePorts) {
                    	if (port == serverPort) continue;
                    	for (Client client: clients) {
                    		if (client.getPort() == port) continue scanPortLoop;
                    	}
                    	foundPort = port;
                        break;
                    }
                    if (foundPort == -1) {
                    	userPrint("pas de port disponible pour la connection du client.");
                    }
                    
                    Client client = new Client(clientAddress, foundPort, clientPort, name, this);
                    userPrint(name + " s'est connecté sur le port : " + foundPort);
                    sendStream.writeUTF(serverResponse);
                    sendStream.writeShort(foundPort);
                    
                    udpio.sendData(clientAddress, clientPort);
                    userPrint("Réponse envoyée au client.");
                    clients.add(client);
                    new Thread(client).start();
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
     * Méthode qui relaie un message à tous les clients connectés
     * @param socket
     * @param message
     * @param senderAddress
     * @param senderPort
     * @throws IOException
     */
    private void relayMessageToClients(String message, Client client)throws IOException {

        for (Client checkClient : clients) {
            if (checkClient != client) {
            	checkClient.sendStream.writeByte(1);
            	checkClient.sendStream.writeUTF(client.name + " : " + message);
                checkClient.udpio.sendData(checkClient.getAddress(), checkClient.clientPort);
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
    public List<Client> getClients() {
    	return clients;
    }

    /**
     * Méthode qui retourne un client en fonction de son adresse et de son port
     * @param address
     * @param port
     * @return
     */
    public Client getUser(InetAddress address, int port){
        for(Client client : clients){
            if(client.getAddress().equals(address) && client.getPort() == port){
                return client;
            }
        }
        return null;
    }

    /**
     * Classe qui permet de scanner les ports UDP
     */
    private static class Client implements Runnable {
        private final InetAddress address;
        private final int port;
        private int clientPort;
        private final String name;
        private final UDPIO udpio = new UDPIO();
        private DataOutputStream sendStream = udpio.getOutput();
        private DataInputStream receiveStream = udpio.getInput();
        private Server server;

        /**
         * Constructeur de la classe Client
         * @param address
         * @param port
         * @param name
         * @param server
         */
        public Client(InetAddress address, int port, int clientPort, String name, Server server) {
            this.address = address;
            this.port = port;
            this.clientPort = clientPort;
            this.name = name;
            this.server = server;
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
            Client that = (Client) o;
            return port == that.port && address.equals(that.address);
        }

        @Override
        public int hashCode() {
            return address.hashCode() + port;
        }
        public void run() {
        	try {
        		DatagramSocket socket = udpio.getSocket();
                socket.bind(new InetSocketAddress("0.0.0.0", port));
            	while (true) {
                    udpio.receiveData();
                    switch (receiveStream.readByte()) {
                        case 1:
                            treatMessage();
                        break;

                        case 0x7F:
                          Server.userPrint("Le client " + name + " s'est déconnecté.");
                          socket.close();
                          return;
                    
                        default:
                            break;
                    }
                }
        	}
        	catch(IOException e) {
        		
        	}
        }
        /**
         * Méthode qui traite un message reçu par le serveur
         */
        public void treatMessage() {
            try {
                String message = receiveStream.readUTF();
                if (message.charAt(0) != '/') {
                    userPrint(name + " : " + message);
                }
                if (message.charAt(0) == '/') {
                    switch (message.substring(1, message.indexOf(" "))) {
                        case "msg":
                            String[] parts = message.split(" ", 3);
                            String targetName = parts[1];
                            String msg = parts[2];
                            for (Client client : server.getClients()) {
                                if (client.name.equals(targetName)) {
                                    sendStream.writeByte(1);
                                    sendStream.writeUTF("Message reçu de " + this.name + " : " + msg + "\n");
                                    udpio.sendData(client.getAddress(), client.clientPort);
                                    break;
                                }
                            }

                        case "all":
                        String[] partsclient = message.split(" ", 2);
                        String allclients = partsclient[1];
                        if (allclients.equals("clients")) {
                            for (Client client : server.getClients()) {
                                    sendStream.writeByte(1);
                                    sendStream.writeUTF(server.getUser(client.getAddress(), client.getPort()).name);
                                    udpio.sendData(address, clientPort);
                             }

                            }                               
                break;
                        default:
                            break;
                    }
                } else {
                    server.relayMessageToClients(message, this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}