import java.net.*;
import java.io.*;
import java.util.*;


public class Ping {

    public static void main(String args[]) {
        try {
            UDPServer server = new UDPServer();
            server.start();

            while (!server.isListening()) {
                Thread.yield();
            }

            UDPClient client = new UDPClient();
            client.start();

            client.join();
            server.kill();
        } catch(Exception e) {
            System.err.println("MAIN ERROR: " + e);
        }
    }


    public static class UDPServer extends Thread {
        private DatagramSocket socket;
        private int port;
        private String hostname;
        private boolean listening;
        private float packetLossChance;


        public UDPServer(int port, String hostname, float packetLossChance) {
            this.port = port;
            this.hostname = hostname;
            this.packetLossChance = packetLossChance;
        }
        public UDPServer() {
            // default to port 2000 with a 10% chance of packet loss
            this(2000, "localhost", 0.1f);
        }

        public boolean isListening() {
            return listening;
        }
        public void kill() {
            if (listening) {
                listening = false;
                socket.close();
            }
        }

        public void run() {
            try {
                socket = new DatagramSocket(port);
                System.out.println("UDP SERVER bound to local port: " + socket.getLocalPort() + " ...");

                DatagramPacket inPacket = new DatagramPacket(new byte[256], 256);

                listening = true;
                while(listening) {
                    socket.receive(inPacket);

                    // simulating percent chance of a packet loss
                    if (Math.random() > packetLossChance) {
                        Thread.sleep((int) (Math.random() * 200));

                        InetAddress remoteAddr = inPacket.getAddress();

                        ByteArrayInputStream bin = new ByteArrayInputStream(inPacket.getData());
                        String inPacketId = "";
                        for(int i = 0; i < inPacket.getLength(); ++i) {
                            int data = bin.read();

                            if(data == -1) {
                                break;
                            } else {
                                inPacketId += (char) data;
                            }
                        }
                        System.out.println("SERVER: PACKET RECEIVED: " + inPacketId);

                        DatagramPacket outPacket = new DatagramPacket(inPacket.getData(), inPacket.getLength());

                        outPacket.setAddress(remoteAddr);
                        outPacket.setPort(inPacket.getPort());

                        socket.send(outPacket);
                        System.out.println("SERVER: PACKET ACK FOR " + inPacketId + " SENT");
                    } else {
                        System.out.println("SERVER: PACKET DROPPED");
                    }
                }
            } catch(IOException e) {
                if (!e.getMessage().equals("Socket is closed")) {
                    System.err.println("SERVER ERROR: " + e);
                }
            } catch(InterruptedException e) {
                System.err.println("SERVER ERROR: " + e);
            }
        }
    }

    public static class UDPClient extends Thread {
        private String hostname;
        private int iterations;


        public UDPClient(String hostname, int iterations) {
            this.hostname = hostname;
            this.iterations = iterations;
        }
        public UDPClient() {
            this("localhost", 10);
        }

        public void run() {
            try {
                List<Thread> clients = new ArrayList<>();
                for (int i = 0; i < iterations; i++) {
                    Thread client = new UDPClientPing(hostname);
                    clients.add(client);
                    client.start();
                }

                for (Thread client: clients) {
                    client.join();
                }
            } catch (InterruptedException e) {
                System.err.println("CLIENT ERROR: " + e);
            }
        }
    }

    public static class UDPClientPing extends Thread {
        private String id;
        private String hostname;


        public UDPClientPing(String hostname) {
            this.id = System.nanoTime() + "";
            this.hostname = hostname;
        }

        public void run() {
            try {
                DatagramSocket socket = new DatagramSocket();

                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                PrintStream pOut = new PrintStream(bOut);
                pOut.print(id);

                byte[] bArray = bOut.toByteArray();
                DatagramPacket outPacket = new DatagramPacket(bArray, bArray.length);
                InetAddress remoteAddr = InetAddress.getByName(hostname);

                outPacket.setAddress(remoteAddr);
                outPacket.setPort(2000);

                long startTime = System.currentTimeMillis();
                socket.send(outPacket);
                System.out.println("CLIENT: PACKET " + id + " SENT");

                DatagramPacket inPacket = new DatagramPacket(new byte[256], 256);
                socket.setSoTimeout(300);
                socket.receive(inPacket);

                long endTime = System.currentTimeMillis();
                System.out.println("CLIENT: PACKET "+ id +" ACK RECEIVED, Total = " + (endTime - startTime) + "ms");
            } catch(SocketTimeoutException e){
                System.err.println("CLIENT Timeout the host is unreachable");
            } catch(UnknownHostException e){
                System.err.println("CLIENT PING UNKNOWN HOST: " + hostname);
            } catch(IOException e) {
                System.err.println("CLIENT PING ERROR: " + e);
            }
        }
    }
}
