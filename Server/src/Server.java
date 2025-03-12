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

    public Server() {}

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
        

            serverPort = 12345; // Pour tester

            userPrint("Serveur en écoute sur le port : " + serverPort);

            InetSocketAddress address = new InetSocketAddress( "0.0.0.0", serverPort);
            socket.bind(address);
            udpio.getReceivePacket().setPort(serverPort);
            userPrint("Java_Server en écoute...");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                for (ClientInfo clientInfo : clients){
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

                String message, name;
                int clientPort;
                InetAddress clientAddress;
                try{
                     message = receiveStream.readUTF();
                     clientAddress = udpio.getReceivePacket().getAddress();
                     clientPort = udpio.getReceivePacket().getPort();
                }catch(Exception e){
                    e.printStackTrace();
                    continue;
                }
               
                
                try{
                    
                 ClientInfo clientinfo = getUser(clientAddress, clientPort);
                 if(clientinfo != null && message.charAt(0) !=  '/'){
                    userPrint(clientinfo.name + " : "  + message);
                 }
                    if (message.startsWith(serverMsg)) {
                        name = receiveStream.readUTF();
                        clients.add(new ClientInfo(clientAddress, clientPort, name));
                        userPrint(name + " s'est connecté sur le port : " + clientPort);
                        sendStream.writeUTF(serverResponse);
                        udpio.sendData(clientAddress, clientPort);
                        userPrint("Réponse envoyée au client.");
                        
                    } else if(message.charAt(0) ==  '/'){
                        switch (message.substring(1,message.indexOf(" "))) {
                            case "msg":
                                String[] parts = message.split(" ", 3);
                                String targetName = parts[1];
                                String msg = parts[2];
                                for (ClientInfo client : clients) {
                                    if (client.name.equals(targetName)) {
                                        sendStream.writeByte(1);
                                        sendStream.writeUTF(msg);
                                        udpio.sendData(client.getAddress(), client.getPort());
                                        break;
                                    }
                                }
                                
                                break;
                        
                            default:
                                break;
                        }
                    } else {
                        relayMessageToClients(socket, message, clientAddress, clientPort);
                    } 
                } catch (Exception e) {
                    continue;
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

    private void relayMessageToClients(DatagramSocket socket, String message, InetAddress senderAddress, int senderPort)throws IOException {

        for (ClientInfo client : clients) {
            if (!client.getAddress().equals(senderAddress) && client.getPort() != senderPort) {
                    sendStream.writeByte(1);
                    sendStream.writeUTF(message);
                    udpio.sendData(client.getAddress(), client.getPort());

            }
        }
    }

    public static void userPrint(String text) {
        System.out.println(text);
    }

    
    public ClientInfo getUser(InetAddress address, int port){
        for(ClientInfo client : clients){
            if(client.getAddress().equals(address) && client.getPort() == port){
                return client;
            }
        }
        return null;
    }

    private static class ClientInfo {
        private final InetAddress address;
        private final int port;
        private final String name;

        public ClientInfo(InetAddress address, int port, String name) {
            this.address = address;
            this.port = port;
            this.name = name;
        }

        public InetAddress getAddress() {
            return address;
        }

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