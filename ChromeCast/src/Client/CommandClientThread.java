package Client;

import ClientProtocol.KnockKnockProtocol;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;


public class CommandClientThread extends Thread {

    public int clientID = 0;
    protected int numberCommands = 0; //numero de secuencia
    
    protected DatagramSocket kkSocket = null;
    private DatagramPacket packet = null;

    protected String hostName = "";
    protected int portNumber = 0; /// puerto cliente para escuchar comandos
    
    private ArrayBlockingQueue<Boolean> bQueue;
    private Object lock = null;

    private KnockKnockProtocol kkp;
    
    public CommandClientThread(String hostName,String port, ArrayBlockingQueue<Boolean> bqueue, Object lock) throws IOException {
        super("CommandClientThread");
        this.hostName = hostName;
        this.portNumber = Integer.parseInt(port);
        this.kkSocket = new DatagramSocket(this.portNumber);
        this.bQueue = bqueue;
        this.lock = lock;
        // socket del cliente, conectado al server en el puerto arg
        this.kkp = new KnockKnockProtocol(hostName,packet,kkSocket);
    }
    
    @Override
    public void run() {
        try
        {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromUser;
            
            byte[] messageByte = new byte[1000];
            
            this.clientID = kkp.HelloChromeCast(messageByte);

            Boolean brokePipe = false;
            while(!brokePipe){
                
                fromUser = stdIn.readLine();

                if (fromUser != null) {
                    if(fromUser.compareTo("c") == 0){
                        welcomeCommandMode();
                        while( (fromUser = stdIn.readLine()).compareTo("exit") != 0 ){
                            //tratamiento de user input
                            String clientString = registerCommand(fromUser);
        
                            kkp.processInput(clientString, messageByte);
                        
                            System.out.print("\r>>Client"+this.clientID+": ");
                        }
                        this.bQueue.clear();
                        //cliente sale de modo comandos, reactivamos el thread de 
                        //escucha a grupo multicast
                        synchronized(lock){
                            this.lock.notify();
                        }
                    } else if(fromUser.compareTo("exit") == 0){
                        System.out.println("a despedirse");
                        System.exit(1);
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

    private String registerCommand(String fromUser){
        String command = "Client"+ String.valueOf( this.clientID )+"->'" + fromUser +"' - ID:"+numberCommands;
        increaseCommandNumber();
        return command;
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