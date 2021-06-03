package org.example.lab3.UDP;

import org.example.lab3.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;


public class MainUDPTest {

    public static void main(String[] args) throws IOException, InterruptedException, IllegalBlockSizeException {
        System.out.println("main start\n");

        int port = 5432;

        Packet pac0 = null;
        Packet pac1 = null;
        Packet pac2 = null;

        pac0 = new Packet((byte) 1, 1,
                new Message(Message.cTypes.ADD_GROUP_OF_PRODUCTS, 0, "test client 0"));
        pac1 = new Packet((byte) 1, 1,
                new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 1, "test client 1"));
        pac2 = new Packet((byte) 1, 1,
                new Message(Message.cTypes.GET_QUANTITY_OF_PRODUCTS_IN_STOCK, 2, "test client 2"));

        StoreServerUDP ss = new StoreServerUDP(port);

        StoreClientUDP sc0 = new StoreClientUDP(port, pac0);
        StoreClientUDP sc1 = new StoreClientUDP(port, pac1);
        StoreClientUDP sc2 = new StoreClientUDP(port, pac2);


        ss.join();


        System.out.println("\nmain end");
    }

}
