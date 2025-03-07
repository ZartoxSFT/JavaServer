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

    public UDPIO(){
        try{
            socket = new DatagramSocket(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     public void sendData(InetAddress address, int port)throws IOException{
        sendData.flush(); // Fait en sorte que tout les données soient écrites
        byte[] databytes = sendData.toByteArray();
        DatagramPacket sendPacket = new DatagramPacket(databytes, databytes.length, address, port);
        socket.send(sendPacket);
        sendData.reset();
    }

    public void receiveData()throws IOException{
        
        socket.receive(receivePacket);
        receiveData.reset();

    }

    public DataInputStream getInput(){
        return receiveStream;
    }

    public DataOutputStream getOutput(){
        return sendStream;
    }

    public DatagramSocket getSocket(){
        return socket;
    }

    public DatagramPacket getReceivePacket(){
        return receivePacket;
    }
}


