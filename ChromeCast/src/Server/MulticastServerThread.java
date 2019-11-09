package Server;
import java.net.*;
import java.util.Deque;
import java.util.concurrent.ArrayBlockingQueue;

import Protocol.Song;

import java.io.*;

public class MulticastServerThread extends Thread {

    private long ONE_SECONDS = 1000;
    private String ipMulticast = "";
    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected Boolean moreQuotes = true;

    private ServerStatus state = null;
    private ServerStatus previousState = null;
    
    private int progress = 0;
    private int maxProgress = 0;

    private ArrayBlockingQueue<String> aQueue;
    private Deque<Song> reproductionQueue;

    public MulticastServerThread(String ipMulticast, ArrayBlockingQueue<String> aQueue, Deque<Song> reproductionQueue2) throws IOException{
        super("MulticastServerThread");
        this.ipMulticast = ipMulticast;
        socket = new DatagramSocket(4445);
        state =  ServerStatus.STOP;
        previousState=  ServerStatus.STOP;
        this.aQueue = aQueue;
        this.reproductionQueue = reproductionQueue2;
        this.maxProgress = this.reproductionQueue.element().seconds;
    }

	public void run(){
        String lastCommand = "";
        while(moreQuotes){
            try {
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
        String data = "\r>>CCast_";
        if(previousState != state){
            data = "\n" + data;
            previousState = state;
        } 
        switch (state) {
            case STOP:
                data += "Stop";
                break;
            case PLAY:
                if(this.reproductionQueue.size()>0){
                    if(progress == maxProgress){
                        this.reproductionQueue.remove();
                        progress = 0;
                        maxProgress = 0;
                        //si hay mas canciones en la cola, cambiar la canción
                        if(this.reproductionQueue.size()>0){
                            maxProgress = this.reproductionQueue.element().seconds;
                        } else{
                            this.state = ServerStatus.STOP;
                        }
                    } else{
                        data += "Play_"+this.reproductionQueue.element().nameSong + " - "+this.reproductionQueue.element().author;
                        data += anim.charAt(progress % anim.length()) + " " + progress + "[s] >> ";
                        progress++;
                    }
                    break;
                }
            case PAUSE:
                data += "PAUSED _ (Pausa en modo comandos para reanudar)";
                break;
            default:
                break;
        }

        return data;
    }

    protected void processMessage(String inMString) throws NumberFormatException, InterruptedException {
        //Queue_<cancion>-<autor>_<segundos>
        //Play_<cancion>-<autor>_<segundos>
        String[] inListString = inMString.split("_");
        String[] themeAuthor;
        switch (inListString[0]) {
            case "play":
            case "Play":
                //tomar canción y reproducir ¿sin importar orden? que pasa con la cola?
                themeAuthor = inListString[1].split("-");
                reproductionQueue.addFirst(new Song(reproductionQueue.size()+1,themeAuthor[0],themeAuthor[1],Integer.valueOf(inListString[2])));
                state = ServerStatus.PLAY;
                maxProgress = Integer.valueOf(inListString[2]);
                break;
            case "queue":
            case "Queue":
                themeAuthor = inListString[1].split("-");
                reproductionQueue.addLast(new Song(reproductionQueue.size()+1,themeAuthor[0],themeAuthor[1],Integer.valueOf(inListString[2])));
                break;
            case "stop":
            case "Stop":
                //limpiar cola de reproducción
                reproductionQueue.clear();
                state = ServerStatus.STOP;
                break;
            case "pause":
            case "Pause":
                if(state == ServerStatus.PAUSE){
                    state = ServerStatus.PLAY;
                } else{
                    state = ServerStatus.PAUSE;       
                }
                break;
            case "next":
            case "Next":
                if(reproductionQueue.size()>1){
                    reproductionQueue.removeFirst();
                }
                break;
            case "jump":
            case "Jump":
                int position = Integer.valueOf( inListString[1] );
                if(reproductionQueue.size()>=position){
                    for(int i = 0; i < position; i++){
                        reproductionQueue.removeFirst();
                    }
                }
                break;
            default:
                break;
        }
    }


}