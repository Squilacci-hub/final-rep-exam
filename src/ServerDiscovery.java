import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class ServerDiscovery {
    private static final int DISCOVERY_PORT = 8888;
    private static final int TIMEOUT = 2000; // 2 secondes

    public static boolean isServerAvailable(String host, int port) {
        try {
            // Création d'un socket UDP pour le broadcast
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.setSoTimeout(TIMEOUT);

            // Message de découverte
            String message = "SERVER_DISCOVERY:" + port;
            byte[] sendData = message.getBytes();

            // Envoi du broadcast
            InetAddress broadcastAddress = InetAddress.getByName(host);
            DatagramPacket sendPacket = new DatagramPacket(
                    sendData,
                    sendData.length,
                    broadcastAddress,
                    DISCOVERY_PORT
            );
            socket.send(sendPacket);

            // Attente de la réponse
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                socket.receive(receivePacket);
                String response = new String(receivePacket.getData()).trim();
                return response.equals("SERVER_ALIVE:" + port);
            } catch (SocketTimeoutException e) {
                return false;
            } finally {
                socket.close();
            }
        } catch (Exception e) {
            return false;
        }
    }
}