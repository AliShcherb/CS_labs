package org.example.test_lab_3;

import org.example.lab3.*;
import org.example.lab3.UDP.StoreClientUDP;
import org.example.lab3.UDP.StoreServerUDP;
import org.junit.jupiter.api.Test;

import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

public class UDPTest {


    @Test
    void shouldPass_whenAllPacketsProcessedCorrectly() throws UnknownHostException, InterruptedException {
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


    @Test
    void shouldPass_whenBelatedPacketIsProcessedCorrectly() throws InterruptedException, UnknownHostException {

        System.out.println("main start\n");

        int port = 5433;

        Packet pac0 = null;
        Packet pac1 = null;

        pac0 = new Packet((byte) 1, 2,
                new Message(Message.cTypes.ADD_GROUP_OF_PRODUCTS, 1, "test packet 2"));
        pac1 = new Packet((byte) 1, 1,
                new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 1, "test packet 1"));

        StoreServerUDP ss = new StoreServerUDP(port);

        StoreClientUDP sc0 = new StoreClientUDP(port, pac0);
        Thread.sleep(100);  //in final project client will be waiting for answer from server,
        //and only after receiving answer packet client will send next packet

        StoreClientUDP sc1 = new StoreClientUDP(port, pac1);

        assertEquals("This packet has been processed yet!", sc1.getAnswerPacket().getBMsq().getPayload());

        ss.join();


        System.out.println("\nmain end");
    }


    @Test
    void shouldPass_whenLastPacketIdTrackerResetToZero() throws InterruptedException, UnknownHostException {
        //Last packet id tracker resets every 5 seconds in order not to store clients
        //that haven`t sent anything to server for this period.

        System.out.println("main start\n");

        int port = 5434;

        Packet pac0 = null;
        Packet pac1 = null;

        pac0 = new Packet((byte) 1, 2,
                new Message(Message.cTypes.ADD_GROUP_OF_PRODUCTS, 1, "test packet 2"));
        pac1 = new Packet((byte) 1, 1,
                new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 1, "test packet 1"));

        StoreServerUDP ss = new StoreServerUDP(port);

        StoreClientUDP sc0 = new StoreClientUDP(port, pac0);
        Thread.sleep(6000);                              //wait for ClientMapCleaner
        StoreClientUDP sc1 = new StoreClientUDP(port, pac1);

        assertEquals("test packet 1 OK!", sc1.getAnswerPacket().getBMsq().getPayload());

        ss.join();


        System.out.println("\nmain end");
    }


    @Test
    void shouldPass_whenAlreadyProcessedPacketIsNotProcessedAgain() throws InterruptedException, UnknownHostException {

        //this situation may happen if server correctly processed client`s packet,
        //but client did not receive confirmation packet from server and consequently resent this packet

        System.out.println("main start\n");

        int port = 5435;

        Packet pac0 = null;

        pac0 = new Packet((byte) 1, 1,
                new Message(Message.cTypes.ADD_GROUP_OF_PRODUCTS, 1, "test packet 1"));

        StoreServerUDP ss = new StoreServerUDP(port);

        StoreClientUDP sc0 = new StoreClientUDP(port, pac0);
        Thread.sleep(100);                 //in final project client will be waiting for answer from server,
                                                //and only after receiving answer packet client will send next packet
        StoreClientUDP sc1 = new StoreClientUDP(port, pac0);

        assertEquals("This packet has been processed yet!", sc1.getAnswerPacket().getBMsq().getPayload());

        ss.join();


        System.out.println("\nmain end");
    }

    @Test
    void shouldPass_whenClientResentPacketAndReceivedConfirmation() throws UnknownHostException, InterruptedException {
        System.out.println("main start\n");

        int port = 5436;

        Packet pac0 = null;


        pac0 = new Packet((byte) 1, 1,
                new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 0, "test"));


        StoreClientUDP sc0 = new StoreClientUDP(port, pac0);

        Thread.sleep(4000);
        //start server after sending a packet
        StoreServerUDP ss = new StoreServerUDP(port);

        assertEquals("test OK!", sc0.getAnswerPacket().getBMsq().getPayload());

        ss.join();


        System.out.println("\nmain end");
    }


}
