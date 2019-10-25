package Server;
import java.net.*;
import java.io.*;

public class MulticastServerThread extends Thread {

    private long FIVE_SECONDS = 1000;
    private String ipMulticast = "";
    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected Boolean moreQuotes = true;

    public MulticastServerThread(String ipMulticast) throws IOException{
        super("MulticastServerThread");
        this.ipMulticast = ipMulticast;
        socket = new DatagramSocket(4445);
    }

    public void run(){
        int i = 0;
        while(moreQuotes){
            try {
                //wait for a request first
                
                byte[] buf = new byte[256];
                
                //resolver y enviar respuesta
                String dString = null;
                dString = getNextQuote(i);
                i++;

                buf = dString.getBytes();

                //debemos enviar la respuesta a los cliente
                // se debe saber dirección del cliente y puerto desde donde llegó 
                // la request
                // InetAddress groupAddress = InetAddress.getByName("230.0.0.1");
                InetAddress groupAddress = InetAddress.getByName(ipMulticast);
                
                DatagramPacket packet = new DatagramPacket(buf, buf.length,groupAddress,4446);
                socket.send(packet); 
                try {
                    sleep((long)(Math.random() * FIVE_SECONDS));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                moreQuotes = false;
            }
        }
    }
    
    protected String getNextQuote(int progress){
        String anim= "|/-\\";        
        String data = "\r" + anim.charAt(progress % anim.length()) + " " + progress;
        // System.out.write(data.getBytes());
        // String returnValue = String.format("CCast_%s_%d%%","test",progress);

        return data;
    }


}