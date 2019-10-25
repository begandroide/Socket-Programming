package Server;
import java.net.*;
import java.util.Date;
import java.io.*;

public class MulticastServerThread extends QuoteServerThread {

    private long FIVE_SECONDS = 8000;
    private String ipMulticast = "";

    public MulticastServerThread(String ipMulticast) throws IOException{
        super("MulticastServerThread");
        this.ipMulticast = ipMulticast;
    }

    public void run(){
        
        while(moreQuotes){
            try {
                //wait for a request first
                
                byte[] buf = new byte[256];
                
                //resolver y enviar respuesta
                String dString = null;
                if(in == null){
                    dString = new Date().toString();
                } else{
                    dString = getNextQuote();
                }

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

}