package org.example.lab3.TCP;

import org.example.lab3.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("main start\n");

        int port = 54321;

        StoreServerTCP server = null;
        try {
            server = new StoreServerTCP(port, 40, 10, 5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Packet packet1 = null;

        Packet packet2 = null;

        packet1 =
                new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_GROUP_OF_PRODUCTS, 1, "client1"));
        packet2 = new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 1, "client2"));

        StoreClientTCP client1 = new StoreClientTCP(port, packet1);

        StoreClientTCP client2 = new StoreClientTCP(port, packet2);

        client1.start();
        client2.start();


        try {
            Thread.sleep(3_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        server.shutdown();


        System.out.println("\nmain end");
    }

}
