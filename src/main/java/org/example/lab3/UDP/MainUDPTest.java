package org.example.lab3.UDP;

import org.example.lab3.Message;
import org.example.lab3.Packet;

import java.io.IOException;


public class MainUDPTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        
        int port = 666;

        Packet packet1 = new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_PRODUCT_NAME_TO_GROUP, 1, "Client N1"));
        Packet packet2 = new Packet((byte) 1, 2, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 2, "Client N2"));
       Packet packet3 = new Packet((byte) 1, 3, new Message(Message.cTypes.GET_QUANTITY_OF_PRODUCTS_IN_STOCK, 3, "Client N3"));
        Packet packet4 = new Packet((byte) 1, 4, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 4, "Client N4"));
        Packet packet5 = new Packet((byte) 1, 5, new Message(Message.cTypes.GET_QUANTITY_OF_PRODUCTS_IN_STOCK, 5, "Client N5"));

        StoreServerUDP ssup = new StoreServerUDP(port);

        StoreClientUDP scup1 = new StoreClientUDP(port, packet1);
        StoreClientUDP scup2 = new StoreClientUDP(port, packet2);
        StoreClientUDP scup3 = new StoreClientUDP(port, packet3);
        StoreClientUDP scup4 = new StoreClientUDP(port, packet4);
        StoreClientUDP scup5 = new StoreClientUDP(port, packet5);


        ssup.join();

    }

}
