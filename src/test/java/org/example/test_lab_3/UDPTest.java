package org.example.test_lab_3;

import org.example.lab3.*;
import org.example.lab3.UDP.StoreClientUDP;
import org.example.lab3.UDP.StoreServerUDP;
import org.junit.jupiter.api.Test;

import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

public class UDPTest {


    @Test
    void ifEverythingCorrect() throws UnknownHostException, InterruptedException {

        int port = 666;

        Packet packet1 = new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_PRODUCT_NAME_TO_GROUP, 1, "Client N1"));
        Packet packet2 = new Packet((byte) 1, 2, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 2, "Client N2"));
        Packet packet3 = new Packet((byte) 1, 3, new Message(Message.cTypes.GET_QUANTITY_OF_PRODUCTS_IN_STOCK, 3, "Client N3"));

        StoreServerUDP ssup = new StoreServerUDP(port);

        StoreClientUDP scup1 = new StoreClientUDP(port, packet1);
        StoreClientUDP scup2 = new StoreClientUDP(port, packet2);
        StoreClientUDP scup3 = new StoreClientUDP(port, packet3);

        ssup.join();

    }


    @Test
    void processedPacketWithDelay() throws InterruptedException, UnknownHostException {

        int port = 999;

        Packet packet1 = new Packet((byte) 1, 1,
                    new Message(Message.cTypes.ADD_GROUP_OF_PRODUCTS, 1, "test packet 2"));
        Packet packet2 = new Packet((byte) 1,1,
                    new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 1, "test packet 1"));

        StoreServerUDP ssup = new StoreServerUDP(port);

        StoreClientUDP scup1 = new StoreClientUDP(port, packet1);
        Thread.sleep(100);
        StoreClientUDP scup2 = new StoreClientUDP(port, packet2);

        assertEquals("Already processed", scup2.getAnswer().getBMsq().getPayload());

        ssup.join();

    }



    @Test
    void passAfterLastPacketResetToZero() throws InterruptedException, UnknownHostException {
        int port = 999;


        Packet packet1 = new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_PRODUCT_NAME_TO_GROUP, 1, "Client N1"));
        Packet packet2 = new Packet((byte) 1, 2, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 2, "Client N2"));
        Packet packet3 = new Packet((byte) 1, 3, new Message(Message.cTypes.GET_QUANTITY_OF_PRODUCTS_IN_STOCK, 3, "Client N3"));
        Packet packet4 = new Packet((byte) 1, 4, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 4, "Client N4"));

        StoreServerUDP ssup = new StoreServerUDP(port);

        StoreClientUDP scup1 = new StoreClientUDP(port, packet1);
        StoreClientUDP scup2 = new StoreClientUDP(port, packet2);
        //turn on MapCleaner
        Thread.sleep(6000);
        StoreClientUDP scup3 = new StoreClientUDP(port, packet3);
        StoreClientUDP scup4 = new StoreClientUDP(port, packet4);

        assertEquals("Client N3 OK!", scup3.getAnswer().getBMsq().getPayload());
        assertEquals("Client N1 OK!", scup1.getAnswer().getBMsq().getPayload());
        ssup.join();

    }


    @Test
    void alreadyProcessedPacketIsNotProcessedAgain() throws InterruptedException, UnknownHostException {
        //if server correctly processed client`s packet,
        //but client did not receive confirmation packet from server and consequently resent this packet

        int port = 111;

        Packet packet1 = new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_PRODUCT_NAME_TO_GROUP, 1, "Client N1"));
        Packet packet2 = new Packet((byte) 1, 2, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 2, "Client N2"));

        StoreServerUDP ssup = new StoreServerUDP(port);

        StoreClientUDP scup1 = new StoreClientUDP(port, packet1);
        StoreClientUDP scup2 = new StoreClientUDP(port, packet2);
        Thread.sleep(100);
        StoreClientUDP scup1_1 = new StoreClientUDP(port, packet1);
        StoreClientUDP scup2_1 = new StoreClientUDP(port, packet2);

        assertEquals("Already processed", scup1_1.getAnswer().getBMsq().getPayload());
        assertEquals("Already processed", scup2_1.getAnswer().getBMsq().getPayload());

        ssup.join();

    }

    @Test
    void resentPacketAndReceivedConfirmation() throws UnknownHostException, InterruptedException {

        int port = 777;

        Packet packet1 = new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_PRODUCT_NAME_TO_GROUP, 1, "Client N1"));
        Packet packet2 = new Packet((byte) 1, 2, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 2, "Client N2"));

        StoreClientUDP scup1 = new StoreClientUDP(port, packet1);
        StoreClientUDP scup2 = new StoreClientUDP(port, packet2);

        Thread.sleep(4000);

        //start server after sending a packet
        StoreServerUDP ssup = new StoreServerUDP(port);

        assertEquals("Client N1 OK!", scup1.getAnswer().getBMsq().getPayload());
        assertEquals("Client N2 OK!", scup2.getAnswer().getBMsq().getPayload());

        ssup.join();

    }


}
