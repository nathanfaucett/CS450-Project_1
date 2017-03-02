package cs450.project_1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RespondThread extends Thread {
	
    private final DatagramPacket packet;
    private final int threadNumber;
    private final int secondPort = 2001;

    public RespondThread(DatagramPacket packet, int threadNumber) {

        this.packet = packet;
        this.threadNumber = threadNumber;

    }

    @Override
    public void run() { // COMPLETE THIS SECTION
        
        // Display packet information
        InetAddress remoteAddr = packet.getAddress();
        System.out.println ("SENDER: " + remoteAddr.getHostAddress());
        System.out.println ("From port: " + packet.getPort());
        
        // Display packet contents, by reading from byte array
        ByteArrayInputStream bin = new ByteArrayInputStream(packet.getData());
        String packetId = "";
        for(int i = 0; i < packet.getLength(); ++i) {
            int data = bin.read();

            if(data == -1) {
                break;
            } else {
                packetId += (char) data;
            }
        }
        // Display only up to the length of the original UDP packet
        System.out.println(packetId);
        
        // send response
        try {
            sendResponse((char) threadNumber, remoteAddr.getHostName(), packet.getPort());
        } catch (InterruptedException ex) {
            System.out.println("UNKNOWN HOST:  " + ex);
        }
    }

    private void sendResponse(char x, String returnAddress, int port) throws InterruptedException {
        
        String hostname = returnAddress;
        String message = Character.toString(x);
        
        try { // COMPLETE THIS SECTION
            
            // Create a datagram socket, look for the first available port
            DatagramSocket socket = new DatagramSocket();
            
            // Convert printstream to byte array
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            PrintStream pOut = new PrintStream(bOut);
            pOut.print(message);
            
            // Create a new datagram packet, containing a maximum buffer of 256 bytes
            byte[] bArray = bOut.toByteArray();
            DatagramPacket packet = new DatagramPacket(bArray, bArray.length);
            
            // Get the InetAddress object
            InetAddress remoteAddr = InetAddress.getByName(hostname);
            
            // Check its IP number
            // Configure the DataGramPacket
            packet.setAddress(remoteAddr);
            packet.setPort(port);
            
            // Send the packet
            // Generate random number from 1 to 10; if the number is 1, throw away the packet
            // If not, wait a random interval of time (<200ms) before sending new packet
            if ((1 + (int) (Math.random() * 10f)) != 1) {
                Thread.sleep((int) (Math.random() * 200f));
                socket.send(packet);
            }
        }
        
        catch (UnknownHostException e) {
            System.out.println("UNKNOWN HOST:  " + hostname);
        }
        
        catch (IOException e) {
            System.out.println("ERROR: " + e);
        }
        
    }
    
}
