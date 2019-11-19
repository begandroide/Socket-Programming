package Client;

import ClientProtocol.KnockKnockProtocol;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;


public class CommandClientThread extends Thread {

    public int clientID = 0;
    protected DatagramSocket kkSocket = null;
    protected String hostName = "230.0.0.1";
    protected int portNumber = 0; /// para escuchar comandos
    protected List<String> historyCommands = null;
    protected int numberCommands = 0;
    private ArrayBlockingQueue<Boolean> bQueue;
    private Object lock = null;
    private DatagramPacket packet = null;
    private KnockKnockProtocol kkp;
    
    public CommandClientThread(String port, ArrayBlockingQueue<Boolean> bqueue, Object lock) throws IOException {
        super("CommandClientThread");
        this.portNumber = Integer.parseInt(port);
        // socket del cliente, conectado al server en el puerto arg
        kkSocket = new DatagramSocket(this.portNumber);
        this.historyCommands = new ArrayList<String>();
        this.bQueue = bqueue;
        this.lock = lock;
        kkp = new KnockKnockProtocol(hostName,packet,kkSocket);
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
                            registerCommand(fromUser);
                            String toLow = fromUser.toLowerCase();
        
                            if(toLow.compareTo("history") == 0){
                                for (String b : historyCommands) {
                                    System.out.println(b);
                                }
                            } else{
                                kkp.processInput(fromUser, messageByte);
                            }
                        
                            System.out.print("\r>>Client"+this.clientID+": ");
                        }
                        this.bQueue.clear();
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