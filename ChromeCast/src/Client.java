
import java.io.*;

import Client.CommandClientThread;
import Client.MulticastClientThread;

public class Client {
    
    public static void main(String[] args) throws IOException {

        //230.0.0.1
        if(args.length < 1){
            System.out.println("Uso: java client <port>");
            System.exit(1);
        }

        try {
            CommandClientThread csThread = new CommandClientThread(args[0]);
            csThread.start();
			Thread.currentThread().sleep((long)1000);
            new MulticastClientThread(csThread.clientID).start();
		} catch (InterruptedException e) {
            e.printStackTrace();
		} catch (java.net.BindException e){
			System.out.println("Puerto actualmente usado, intenta con otro");
        }
    }
}