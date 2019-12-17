package Server;

import Player.Player;
import Player.Song;
import java.io.*;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;



public class MulticastServerThread extends Thread {

    private long ONE_SECONDS = 1000;
    private String ipMulticast = "";
    protected DatagramSocket socket = null;
    protected Boolean moreQuotes = true;

    private ServerStatus state = null;
    private ServerStatus previousState = null;
    
    private Player player;

    private ArrayBlockingQueue<String> instructionQueue;

    public MulticastServerThread(String ipMulticast, ArrayBlockingQueue<String> instructionQueue, Player player) throws IOException{
        super("MulticastServerThread");
        this.ipMulticast = ipMulticast;
        socket = new DatagramSocket(4445);
        state =  ServerStatus.STOP;
        previousState=  ServerStatus.STOP;
        this.player = player;
        this.instructionQueue = instructionQueue;
    }

 @Override
	public void run(){
        String lastCommand = "";
        while(moreQuotes){
            try {
                if( instructionQueue.size() > 0 )
                {
                    lastCommand = instructionQueue.take(); 
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
                    sleep(ONE_SECONDS);
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
                if(this.player.getSizeReproductionQueue()>0){
                    if(player.timeOut()){
                        if(!player.next()){
                            this.state = ServerStatus.STOP;
                        }
                    } else{
                        Song currentSong = player.getCurrentSong();
                        data += "Play_"+currentSong.nameSong + " - "+currentSong.author;
                        int progress = player.getProgress();
                        data += anim.charAt(progress % anim.length()) + " " + progress + "[s] >> ";
                        player.incrementProgress();
                    }
                }else{
                    this.state = ServerStatus.STOP;    
                }
                break;
            case PAUSE:
                data += "PAUSED _ (Pausa en modo comandos (c) para reanudar)";
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
                player.putInHead(new Song(player.getSizeReproductionQueue()+1,themeAuthor[0],themeAuthor[1],Integer.valueOf(inListString[2])));
                state = ServerStatus.PLAY;
                player.setMaxProgress(Integer.valueOf(inListString[2]));
                break;
            case "queue":
            case "Queue":
                themeAuthor = inListString[1].split("-");
                player.putInTail(new Song(player.getSizeReproductionQueue()+1,themeAuthor[0],themeAuthor[1],Integer.valueOf(inListString[2])));
                if(player.getSizeReproductionQueue() == 1){
                    state = ServerStatus.PLAY;
                    player.setMaxProgress(Integer.valueOf(inListString[2]));
                } 
                break;
            case "stop":
            case "Stop":
                //limpiar cola de reproducción
                player.clearReproductionQueue();
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
                if(!player.next()){
                    state = ServerStatus.STOP;
                }
                break;
            case "jump":
            case "Jump":
                int position = Integer.valueOf( inListString[1] );
                player.jump(position);
                break;
            default:
                break;
        }
    }


}