package org.example.lab2;



import org.example.lab2.client.Client;
import org.example.lab2.server.Server;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("main start\n");

        int port = 54321;

        Server server = null;
        try {
            server = new Server(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        server.setDaemon(true);
        server.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Packet packet1 = null;
        Packet packet2 = null;
        packet1  = new Packet((byte) 1, (long) 1, new Message(Message.cTypes.ADD_GROUP_OF_PRODUCTS,1,"client1"));
        System.out.println(packet1);
        packet2  = new Packet((byte) 1, (long) 1, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK,1,"client2"));
        System.out.println(packet1);

        Client client1 = new Client(port, packet1);
      //  System.out.println(packet1);
//        client.setDaemon(true);


        Client client2 = new Client(port, packet2);
      //  System.out.println(packet2);
//      client2.setDaemon(true);

        client1.start();
        client2.start();


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        server.shutdown();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nmain end");
    }

}
