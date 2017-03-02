package cs450.project_1;

import java.net.*;
import java.io.*;
import java.util.Date;

public class PingThread extends Thread {
    
    private final int threadNum;

    private final int firstPort = 2000;
    private final int secondPort = 2001;
    private final int timeout = 300;

    public PingThread(int val) {
        
        threadNum = val;
        
    }

    @Override
    public void run() {
        
        // Use "localhost" to experiment on a standalone computer
        
        String hostname = "localhost";
        String message = Integer.toString(threadNum);
        
        try { // COMPLETE THIS SECTION
            
            // Create a DatagramSocket, look for the first available port
            DatagramSocket socket = new DatagramSocket();
            
            // Convert PrintStream to byte array
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            PrintStream pOut = new PrintStream(bOut);
            pOut.print(message);
            
            // Create a DatagramPacket, containing a maximum buffer of 256 bytes
            byte[] bArray = bOut.toByteArray();
            DatagramPacket outPacket = new DatagramPacket(bArray, bArray.length);
            
            // Get the InetAddress object
            InetAddress remoteAddr = InetAddress.getByName(hostname);
            
            // Check its IP number
            // Configure the DatagramPacket
            outPacket.setAddress(remoteAddr);
            outPacket.setPort(2000);
            
            // Send the packet
            long sendTime = System.currentTimeMillis();
            socket.send(outPacket);
            
            // Set timeout to 300ms for receive
            socket.setSoTimeout(300);
            DatagramPacket inPacket = new DatagramPacket(new byte[256], 256);
            socket.receive(inPacket);
            
            long deltaTime = System.currentTimeMillis() - sendTime;
            System.out.println("Packet "+ message +" round trip took " + deltaTime + "ms");
        }
        
        catch (UnknownHostException e) {
            System.err.println("Unknown host "+hostname);
        }
        
        catch (IOException e) {
            System.err.println ("Error - " + e);
        }
        
    }
    
}
