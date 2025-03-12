import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPIO {
    private ByteArrayOutputStream sendData = new ByteArrayOutputStream();
    private DataOutputStream sendStream = new DataOutputStream(sendData);
    private byte[] receiveArray = new byte[1024];
    private ByteArrayInputStream receiveData = new ByteArrayInputStream(receiveArray);
    private DataInputStream receiveStream = new DataInputStream(receiveData);
    private DatagramSocket socket;
    DatagramPacket receivePacket = new DatagramPacket(receiveArray, receiveArray.length);

    /**
     * Constructeur de la classe UDPIO
     */
    public UDPIO(){
        try{
            socket = new DatagramSocket(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Méthode qui envoie des données à une adresse et un port spécifiés
     * @param address
     * @param port
     * @throws IOException
     */
    public void sendData(InetAddress address, int port)throws IOException{
        sendData.flush(); // Fait en sorte que toutes les données soient écrites
        byte[] databytes = sendData.toByteArray();
        DatagramPacket sendPacket = new DatagramPacket(databytes, databytes.length, address, port);
        socket.send(sendPacket);
        sendData.reset();
    }

    /**
     * Méthode qui reçoit des données d'un client
     * @throws IOException
     */
    public void receiveData()throws IOException{
        
        socket.receive(receivePacket);
        receiveData.reset();

    }

    /**
     * Méthode qui retourne le flux de sortie de données
     * @return
     */
    public DataInputStream getInput(){
        return receiveStream;
    }

    /**
     * Méthode qui retourne le flux d'entrée de données
     * @return
     */
    public DataOutputStream getOutput(){
        return sendStream;
    }

    /**
     * Méthode qui retourne le socket de la classe
     * @return
     */
    public DatagramSocket getSocket(){
        return socket;
    }

    /**
     * Méthode qui retourne le paquet reçu par le socket
     * @return
     */
    public DatagramPacket getReceivePacket(){
        return receivePacket;
    }
}


