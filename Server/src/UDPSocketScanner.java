import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPSocketScanner {
    public static byte[] testpacket = new byte[0];
    public static byte[] receivedtestpacket = new byte[1];

    public static void scanUDPPort(int StartPort, int EndPort, String hostIP)  throws SocketException {
        for (int i = StartPort; i < EndPort;i++){
            scanUDPPort(i, hostIP);
        }

    }

    private static void scanUDPPort(int port, String host)  throws SocketException {
        try{
            DatagramSocket socketClient = new DatagramSocket(port);
            InetAddress currAddress = InetAddress.getByName(host);
            socketClient.setSoTimeout(2000);

            DatagramPacket sendpacket = new DatagramPacket(testpacket, testpacket.length, currAddress, port);

            socketClient.send(sendpacket);

           // DatagramPacket receivepacket = new DatagramPacket(receivedtestpacket, receivedtestpacket.length);
           // socketClient.receive(receivepacket);
           // String reponse = new String(receivepacket.getData(),0,receivepacket.getLength());
            System.out.println("Port libre = " + port + " sur " + host);
            socketClient.close();


        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
