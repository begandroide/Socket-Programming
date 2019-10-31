package Server;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.io.*;

public class MulticastServerThread extends Thread {

    private long ONE_SECONDS = 1000;
    private String ipMulticast = "";
    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected Boolean moreQuotes = true;

    private ServerStatus state = null;
    private int progress = 0;

    private ArrayBlockingQueue<String> aQueue;

    public MulticastServerThread(String ipMulticast, ArrayBlockingQueue<String> aQueue) throws IOException{
        super("MulticastServerThread");
        this.ipMulticast = ipMulticast;
        socket = new DatagramSocket(4445);
        state =  ServerStatus.STOP;
        this.aQueue = aQueue;
    }

	public void run(){
        String lastCommand = "";
        while(moreQuotes){
            try {
                //wait for a request first
                //ERROR: funciona solo para un thread
                if( aQueue.size() > 0 )
                {
                    lastCommand = aQueue.take(); 
                    processMessage(lastCommand);
                }
                byte[] buf = new byte[256];
                
                //resolver y enviar respuesta
                String dString = null;
                dString = getNextQuote();

                buf = dString.getBytes();

                //debemos enviar la respuesta a los cliente
                // se debe saber dirección del cliente y puerto desde donde llegó la request
                InetAddress groupAddress = InetAddress.getByName(ipMulticast);
                
                DatagramPacket packet = new DatagramPacket(buf, buf.length,groupAddress,4446);
                socket.send(packet); 
                try {
                    sleep((long)(ONE_SECONDS));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                moreQuotes = false;
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
    
    protected String getNextQuote(){
        String anim = "|/-\\";   
        String data = "";     
        switch (state) {
            case STOP:
                data = "\r" + anim.charAt((int)Math.random() % anim.length()) + " CCast STOPED";
                break;
            case PLAY:
                data = "\r" + anim.charAt(progress % anim.length()) + " " + progress + "%% >> ";
                progress++;
                break;
            case PAUSE:
                data = "\r" + anim.charAt((int)Math.random() % anim.length()) + " CCast PAUSED";
                break;
            default:
                break;
        }

        return data;
    }

    protected void processMessage(String inMString){
        switch (inMString) {
            case "0":
                state = ServerStatus.PLAY;
                break;
            case "1":
                state = ServerStatus.STOP;
                break;
            case "2":
                state = ServerStatus.PAUSE;            
                break;
            default:
                break;
        }
    }


}