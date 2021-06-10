package org.example.lab3.TCP;

import org.example.lab3.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;


public class Main {
    private static int timeoutStandart = 5000;
    public static void main(String[] args) throws IOException {

        int port = 666;

        StoreServerTCP server = new StoreServerTCP(port, 100, 10, timeoutStandart);
        server.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Packet packet1 = new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_PRODUCT_NAME_TO_GROUP, 1, "Client N1"));
        Packet packet2 = new Packet((byte) 1, 2, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 2, "Client N2"));
        Packet packet3 = new Packet((byte) 1, 3, new Message(Message.cTypes.GET_QUANTITY_OF_PRODUCTS_IN_STOCK, 3, "Client N3"));
        Packet packet4 = new Packet((byte) 1, 4, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 4, "Client N4"));
        Packet packet5 = new Packet((byte) 1, 5, new Message(Message.cTypes.GET_QUANTITY_OF_PRODUCTS_IN_STOCK, 5, "Client N5"));

        StoreClientTCP client1 = new StoreClientTCP(port, packet1);
        StoreClientTCP client2 = new StoreClientTCP(port, packet2);
        StoreClientTCP client3 = new StoreClientTCP(port, packet3);
        StoreClientTCP client4 = new StoreClientTCP(port, packet4);
        StoreClientTCP client5 = new StoreClientTCP(port, packet5);


        client1.start();
        client2.start();
        client3.start();
        client4.start();
        client5.start();


        try {
            Thread.sleep(timeoutStandart);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        server.shutdown();

    }

}
