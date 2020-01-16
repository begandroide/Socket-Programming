package Server;

import Player.Player;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class ServerCommandThread extends Thread {
    private boolean moreQuotes = true;
    public final int MAX_QUEUE = 7;
    public Vector<String> Queue = new Vector<String>();
    private ArrayBlockingQueue<String> bQueue;
    private Player player;
    private int activeClients = 0;
    /**
     * Historial de comandos
     */
    protected List<String> historyCommands = null;

    public ServerCommandThread( ArrayBlockingQueue<String> bqueue, int activeClients, Player player) throws IOException {
        super("ServerCommandThread");
        this.bQueue = bqueue;
        this.activeClients = activeClients;
        this.player = player;
        this.historyCommands = new ArrayList<String>();
    }

    @Override
    public void run() {
        InetAddress address;
        try(
            MulticastSocket multiSocket = new MulticastSocket(5007);
            ) 
            {
            address = InetAddress.getByName("230.0.0.1");
            multiSocket.joinGroup(address);
            while(moreQuotes){
    
                DatagramPacket packet;
                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                multiSocket.receive(packet);
    
                String received = new String(packet.getData(),0,packet.getLength());

                decodeReceived(received, packet, multiSocket);
                
                System.out.println(received);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void decodeReceived(String received, DatagramPacket packet, MulticastSocket multiSocket) throws IOException{ 
        //HelloChromeCast
        String outBrute = extractBruteCommand(received);
        String tmpBrute = outBrute.split(";")[0];
        if(tmpBrute.compareTo("hellochromecast") == 0){
            System.out.println("Nuevo cliente");
            activeClients++;
            byte[] activeNow = String.valueOf( activeClients ).getBytes();
            packet = new DatagramPacket(activeNow, activeNow.length,packet.getAddress(),packet.getPort());
            multiSocket.send(packet); 
        } else if( tmpBrute.compareTo("queue") == 0 ){
            System.out.println("Consulta cola");
            byte[] activeNow = player.reproductionQueueToString();
            packet = new DatagramPacket(activeNow, activeNow.length,packet.getAddress(),packet.getPort());
            multiSocket.send(packet); 
        } else if(tmpBrute.compareTo("byebyechromecast") == 0){
            activeClients--;
        } else if(tmpBrute.compareTo("history") == 0 ){
            System.out.println("Consulta historial");
            byte[] activeNow = this.historyCommands.toString().getBytes();
            packet = new DatagramPacket(activeNow, activeNow.length,packet.getAddress(),packet.getPort());
            multiSocket.send(packet);
        } else {
            this.historyCommands.add(received);
            bQueue.add(outBrute);
        }
    }

    public String extractBruteCommand(String fromUser){
        String brute = "";
        String regex = "Client([0-9])+(->){1}'([\\w|\\s|_|-]+)'(\\s-\\s)ID:([0-9])+$";
        fromUser.matches(regex);
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fromUser);
        if (matcher.matches()) {
            brute = matcher.group(3);
            brute += ";" + matcher.group(1);
            System.out.println("brute command " + brute);
        } else if(fromUser.compareTo("HelloChromeCast") == 0 || fromUser.compareTo("ByeByeChromeCast") == 0){
            brute = fromUser;
        }

        return brute.toLowerCase();
    }
}