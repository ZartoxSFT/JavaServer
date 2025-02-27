import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class UDPSocketScanner {
    private static final byte[] EMPTY_PACKET = new byte[0];
    public static List<Integer> availablePorts = new ArrayList<>();

    /**
     * Méthode qui va itérer sur les ports de startPort à endPort et appeler scanUDPPort pour chaque port
     * @param startPort
     * @param endPort
     * @param hostIP
     * @throws SocketException
     */
    public static void scanUDPPorts(int startPort, int endPort, String hostIP) throws SocketException, UnknownHostException {
        for (int port = startPort; port < endPort; port++) {
            scanUDPPort(port, hostIP);
        }
    }

    /**
     * Méthode qui va scanner un port UDP
     * @param port
     * @param host
     * @throws SocketException
     * @throws UnknownHostException
     * @throws SocketTimeoutException
     * @throws IOException
     */
    private static void scanUDPPort(int port, String host) throws SocketException, UnknownHostException {
        DatagramSocket socket = null;
        try{
            
            socket = new DatagramSocket(port);
            InetAddress address = InetAddress.getByName(host);
            socket.setSoTimeout(2000);

            DatagramPacket packet = new DatagramPacket(EMPTY_PACKET, EMPTY_PACKET.length, address, port);
            socket.send(packet);

            System.out.println("Port disponible: " + port + " sur " + host);
            availablePorts.add(port);
        } catch (UnknownHostException e) {

            System.out.println("Host inconnu : " + host);
            throw new UnknownHostException();
        } catch (SocketTimeoutException e) {
            System.out.println("Port indisponible: " + port + " sur " + host);
        } catch (IOException e) {
            System.out.println("Port indisponible: " + port + " sur " + host);
        }finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
