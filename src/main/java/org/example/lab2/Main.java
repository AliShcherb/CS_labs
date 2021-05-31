package org.example.lab2;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException, BadPaddingException, IllegalBlockSizeException {
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
        packet1  = new Packet ((byte) 1, 1, new Message(Message.cTypes.ADD_PRODUCT_GROUP,1,"client1"));

        System.out.println(packet1);
        packet2  = new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_PRODUCT,1,"client2"));
        System.out.println(packet1);
        Client client1 = new Client(port, packet1);
        System.out.println(packet1);
//        client.setDaemon(true);

        Client client2 = new Client(port, packet2);
        System.out.println(packet2);
//        client2.setDaemon(true);

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
