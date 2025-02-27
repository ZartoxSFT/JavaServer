import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.net.UnknownHostException;

public class Server {
    public static final String serverIP = "127.0.0.1";
    public static final String serverMsg = "hello serveur RX302";
    public static final String serverResponse = "Serveur RX302 ready";
    private Set<ClientInfo> clients = new HashSet<>();

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
        final DatagramSocket socket;
        try {
            int serverPort = -1;
            for (int port : UDPSocketScanner.availablePorts) {
                serverPort = port;
                break;
            }

            if (serverPort == -1) {
                System.out.println("Aucun port disponible dans la plage spécifiée.");
                return;
            }

            System.out.println("Serveur en écoute sur le port : " + serverPort);

            socket = new DatagramSocket(null);
            InetSocketAddress address = new InetSocketAddress(serverIP, serverPort);
            socket.bind(address);
            byte[] receivedData = new byte[1024];
            userPrint("Java_Server en écoute...");

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
                socket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                userPrint("Message reçu : " + message + " de " + clientAddress.getHostAddress() + ":" + clientPort);

                if (message.startsWith(serverMsg)) {
                    userPrint("Nouveau client : " + clientAddress.getHostAddress() + " : " + clientPort);
                    clients.add(new ClientInfo(clientAddress, clientPort));
                    byte[] sendData = serverResponse.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                    socket.send(sendPacket);
                    userPrint("Réponse envoyée au client.");
                } else {
                    relayMessageToClients(socket, message, clientAddress, clientPort);
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

    private void relayMessageToClients(DatagramSocket socket, String message, InetAddress senderAddress, int senderPort) {
        for (ClientInfo client : clients) {
            if (!client.getAddress().equals(senderAddress) || client.getPort() != senderPort) {
                try {
                    byte[] sendData = message.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, client.getAddress(), client.getPort());
                    socket.send(sendPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void userPrint(String text) {
        System.out.println(text);
    }

    private static class ClientInfo {
        private final InetAddress address;
        private final int port;

        public ClientInfo(InetAddress address, int port) {
            this.address = address;
            this.port = port;
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