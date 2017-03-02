package cs450.project_1;

import java.net.*;
import java.io.*;

public class Server {

    public static void main(String[] args) {
        
        final int NUMBER_OF_PINGS = 10;
        int firstPort = 2000;
        
        try {
            
            // Create a datagram socket, bound to the specific port 2000
            
            DatagramSocket socket = new DatagramSocket(firstPort);

            System.out.println("PING SERVER bound to local port " + socket.getLocalPort() + " ...");

            for (int i = 0; i < NUMBER_OF_PINGS; ++i) {
                
                // Create a datagram packet, containing a maximum buffer of 256 bytes
                
                DatagramPacket packet = new DatagramPacket( new byte[256], 256 );

                // Receive a packet (remember by default this is a blocking operation)
                
                socket.receive(packet);

                Thread thr = new RespondThread(packet, i);
                thr.start();
                
            }

            socket.close( );
            
        }
        
        catch (IOException e) 	{
            System.err.println ("ERROR: " + e);
        }
        
    }

}