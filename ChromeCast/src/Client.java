import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;

import Client.CommandClientThread;
import Client.MulticastClientThread;

public class Client {
    
    public static void main(String[] args) throws IOException {

        //230.0.0.1
        if(args.length < 1){
            System.out.println("Uso: java client <ip_host> <port>");
            System.exit(1);
        }

        //cola bloqueante para entrar a modo comandos y dejar en wait al thread escucha
        ArrayBlockingQueue<Boolean> bqueue = new ArrayBlockingQueue<Boolean>(1,true);
        //lock para bloquear/reanudar al thread de escucha multicast, util para 
        //modo comandos
        Object lock = new Object();
        try {
            new CommandClientThread(args[0],bqueue,lock).start();
            new MulticastClientThread(bqueue,lock).start();
		} catch (java.net.BindException e) {
			System.out.println("Puerto actualmente usado, intenta con otro");
        }
    }
}