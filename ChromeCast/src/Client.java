
import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;

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
            ArrayBlockingQueue<Boolean> bqueue = new ArrayBlockingQueue<Boolean>(1,true); 
            new CommandClientThread(args[0],bqueue).start();
            new MulticastClientThread(bqueue).start();
		} catch (java.net.BindException e) {
			System.out.println("Puerto actualmente usado, intenta con otro");
        }
    }
}