package Client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import Protocol.KnockKnockProtocol;

public class CommandClientThread extends Thread {
    protected DatagramSocket kkSocket = null;
    protected String hostName = "230.0.0.1";
    protected int portNumber = 0; /// para escuchar comandos
    public int clientID = 0;
    protected List<String> historyCommands = null;
    protected int numberCommands = 0;
    private ArrayBlockingQueue<Boolean> bQueue;
    
    public CommandClientThread(String port, ArrayBlockingQueue<Boolean> bqueue) throws IOException {
        super("CommandClientThread");
        this.portNumber = Integer.parseInt(port);
        // socket del cliente, conectado al server en el puerto arg
        kkSocket = new DatagramSocket(this.portNumber);
        this.historyCommands = new ArrayList<String>();
        this.bQueue = bqueue;
    }
    
    public void run() {
        try
        {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromUser;
            
            byte[] messageByte = new byte[1000];
            
            KnockKnockProtocol kkp = new KnockKnockProtocol();
            //mensaje de bienvenida

            messageByte =  "HelloChromeCast".getBytes();
            InetAddress groupAddress = InetAddress.getByName(hostName);
    
            DatagramPacket packet = new DatagramPacket(messageByte, messageByte.length,groupAddress,4447);
            kkSocket.send(packet); 

            //esperar id de cliente de servidor
            kkSocket.receive(packet);

            String received = new String(packet.getData(),0,packet.getLength());

            this.clientID = Integer.parseInt(received);
            System.out.println("Bienvenido a ChromeCast cliente: " + this.clientID);
            System.out.println( kkp.processInput(null) );

            Boolean brokePipe = false;
            while(!brokePipe){
                
                fromUser = stdIn.readLine();

                if (fromUser != null) {
                    if(fromUser.compareTo("c") == 0){
                        welcomeCommandMode();
                        while( (fromUser = stdIn.readLine()).compareTo("exit") != 0 ){
                            //tratamiento de user input
                            processInput(fromUser, messageByte,packet, groupAddress);
                            System.out.print("\r>>Client"+this.clientID+": ");
                        }
                        this.bQueue.clear();
                    } else if(fromUser.compareTo("exit") == 0){
                        System.out.println("a despedirse");
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName + " Intenta con otro puerto ");
            System.exit(1);
        } catch (InterruptedException e) {
            System.err.println("Problemas al intentar detener escuchas de ChromeCast para tipear comandos");
        }
    }

    private void processInput(String fromUser, byte[] messageByte, DatagramPacket packet, InetAddress groupAddress) throws IOException {
        String toLow = fromUser.toLowerCase();

        registerCommand(fromUser);
        messageByte =  fromUser.getBytes();
        
        if(toLow.compareTo("history") == 0){
            for (String b : historyCommands) {
                System.out.println(b);
            }
        } else if(toLow.contains("pause") || toLow.contains("play") || toLow.contains("stop") || toLow.contains("queue_") || toLow.contains("next") || toLow.contains("jump")){

            packet = new DatagramPacket(messageByte, messageByte.length,groupAddress,4447);
            kkSocket.send(packet);
        } else if(toLow.compareTo("queue") == 0 ){

            packet = new DatagramPacket(messageByte, messageByte.length,groupAddress,4447);
            kkSocket.send(packet);
            
            messageByte = new byte[1000];
            packet = new DatagramPacket(messageByte, messageByte.length,groupAddress,4447);
            
            kkSocket.receive(packet);
    
            String received = new String(packet.getData(),0,packet.getLength());
            System.out.println(received);        
        } else{
            //no se entienden
            System.out.println("Comando no conocido");
        }
    }

    private void registerCommand(String fromUser){
        String command = "Client"+ String.valueOf( this.clientID )+"->'" + fromUser +"' - ID:"+numberCommands;
        increaseCommandNumber();
        this.historyCommands.add(command);
    }

    private void welcomeCommandMode() throws InterruptedException {
        System.out.println("Modo Comandos a ChromeCast");
        //solicitamos bloquear escucha a thread chromecast
        this.bQueue.add(true);
        System.out.println("Escucha a ChromeCast pausada por modo comando");
        sleep(1000);
        System.out.print("\r");
        for(int i = 0; i < 60 ; i++){
            System.out.print("  ");
        }
        System.out.print("\r>>Client"+this.clientID+": ");
    }

    private void increaseCommandNumber(){
        this.numberCommands++;
    }
}