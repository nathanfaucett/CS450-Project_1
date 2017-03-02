package cs450.project_1;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        
        final int NUMBER_OF_PINGS = 10;

        for (int i = 0; i < NUMBER_OF_PINGS; ++i){

            Thread thr = new PingThread(i);
            thr.start();
            
            // Using join() isn't required, but it makes the pings come back in order.
            
            thr.join();

        }
    }

}