package org.example.test_lab_2;

import org.example.lab2.*;
import org.example.lab2.client.Client;
import org.example.lab2.server.Server;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainTest {


    @Test
    void shouldPass_whenPacketIsValid() {

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
        packet1 = new Packet((byte) 1, (long)1, new Message(Message.cTypes. ADD_GROUP_OF_PRODUCTS, 1, "client1"));
        packet2 = new Packet((byte) 1, (long)1, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 1, "client1"));

        Client client1 = new Client(port, packet1);
        Client client2 = new Client(port, packet2);

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

    @Test
        //USER ID = 19
    void shouldPass_whenPseudoMagicByteProcessedCorrectly() {
        System.out.println("main start\n");

        int port = 54322;

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
        packet1 = new Packet((byte) 1, (long)1, new Message(Message.cTypes. ADD_GROUP_OF_PRODUCTS, 19, "client1"));
        packet2 = new Packet((byte) 1, (long)1, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 19, "client1"));

        Client client1 = new Client(port, packet1);
//        client.setDaemon(true);

        Client client2 = new Client(port, packet2);
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


    @Test
    void shouldThrowTimeoutException_whenFullPacketNotReceived() throws IOException, BadPaddingException, IllegalBlockSizeException, TimeoutException {
        int port = 54323;
        Server server = new Server(port);
        server.start();

        Socket socket = new Socket("localhost", port);

        InputStream input = socket.getInputStream();
        OutputStream output = socket.getOutputStream();
        Packet packet1 = new Packet((byte) 1, (long)1, new Message(Message.cTypes.ADD_GROUP_OF_PRODUCTS, 1, "client1"));


        Network network = new Network(input, output, 5, TimeUnit.SECONDS);

        byte[] corruptedPac = new byte[7];
        System.arraycopy(packet1.toBytes(), 0, corruptedPac, 0, 7);
        network.send(corruptedPac);

        assertThrows(TimeoutException.class, () -> network.receive());
    }

    @Test
    void shouldPass_whenInvalidMagicByte() throws IOException, BadPaddingException, IllegalBlockSizeException, TimeoutException {
        int port = 54324;
        Server server = new Server(port);
        server.start();

        Socket socket = new Socket("localhost", port);

        InputStream input = socket.getInputStream();
        OutputStream output = socket.getOutputStream();
        Packet packet1 = new Packet((byte) 1, (long)1, new Message(Message.cTypes.ADD_GROUP_OF_PRODUCTS, 1, "client1"));


        Network network = new Network(input, output, 5, TimeUnit.SECONDS);

        byte[] corruptedPac = packet1.toBytes();
        corruptedPac[0] = 10; //must be 19
        network.send(corruptedPac);

        byte[] answerPacketBytes = network.receive();
        Packet answerPacketFromServer = new Packet(answerPacketBytes);


        assertEquals("Corrupted header!", answerPacketFromServer.getBMsq().getPayload());
    }

}
