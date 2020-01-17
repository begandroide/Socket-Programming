import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;

import Client.CommandClientThread;
import Client.MulticastClientThread;

public class Client {
    
    public static void main(String[] args) throws IOException {

        //230.0.0.1
        if(args.length < 2){
            System.out.println("Uso: java client <ip_multicast> <port>");
            System.exit(1);
        }

        /* blockingQueue -> cola para solicitar bloquear el thread escucha de grupo
         * multicast por parte de cliente.
         */
        ArrayBlockingQueue<Boolean> blockingQueue = new ArrayBlockingQueue<Boolean>(1,true);
        /**
         * lock -> objeto sincronizado para bloquear/reanudar el thread de escucha de 
         * grupo multicast por parte del cliente.
         */
        Object lock = new Object();

        try {
            //thread comandos de cliente
            new CommandClientThread(args[0],args[1],blockingQueue,lock).start();
            //thread de escucha a grupo multicast
            new MulticastClientThread(args[0],blockingQueue,lock).start();
		} catch (java.net.BindException e) {
			System.out.println("Puerto " + args[1] + " actualmente usado, intenta con otro");
        }
    }
}