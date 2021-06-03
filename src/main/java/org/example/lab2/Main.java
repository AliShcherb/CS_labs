package org.example.lab2;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;


public class Main {

    public static void main(String[] args) {

        int port = 666;
        Server server = null;
        try {
            server = new Server(port);
        } catch (IOException e) {
            e.printStackTrace();
        }


        server.start();


        Packet packet1 = new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_PRODUCT_NAME_TO_GROUP, 1, "Client N1"));
        Client client1 = new Client(port, packet1);
        System.out.println(packet1);
        client1.start();

        Packet packet2 = new Packet((byte) 1, 2, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 2, "Client N2"));
        Client client2 = new Client(port, packet2);
        System.out.println(packet2);
        client2.start();

        Packet packet3 = new Packet((byte) 1, 3, new Message(Message.cTypes.GET_QUANTITY_OF_PRODUCTS_IN_STOCK, 3, "Client N3"));
        Client client3 = new Client(port, packet3);
        System.out.println(packet3);
        client3.start();











    }

}
