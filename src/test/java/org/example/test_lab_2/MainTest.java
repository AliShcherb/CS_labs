package org.example.test_lab_2;

import org.example.lab2.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainTest {


    @Test
    void packetValidation() {

        int port = 288;

        Server server = null;
        try {
            server = new Server(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();


        Packet packet1 = new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_PRODUCT_NAME_TO_GROUP, 1, "Client N1"));
        Packet packet2 = new Packet((byte) 1, 2, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 2, "Client N2"));
        Packet packet3 = new Packet((byte) 1, 3, new Message(Message.cTypes.GET_QUANTITY_OF_PRODUCTS_IN_STOCK, 3, "Client N3"));
        Packet packet4 = new Packet((byte) 1, 4, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 4, "Client N4"));
        Packet packet5 = new Packet((byte) 1, 5, new Message(Message.cTypes.GET_QUANTITY_OF_PRODUCTS_IN_STOCK, 5, "Client N5"));

        Client client1 = new Client(port, packet1);
        Client client2 = new Client(port, packet2);
        Client client3 = new Client(port, packet3);
        Client client4 = new Client(port, packet4);
        Client client5 = new Client(port, packet5);

        client1.start();
        client2.start();
        client3.start();
        client4.start();
        client5.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    }


//Wait, coz works 30sec
    @Test
    void timeoutException() throws IOException{
        int port = 229;
        Server server = null;
        try {
            server = new Server(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();

        Socket socket = new Socket("localhost", port);

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        Packet packet1 = new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_PRODUCT_NAME_TO_GROUP, 1, "Client N1"));
        Network network = new Network(in, out, 5, TimeUnit.SECONDS);

        byte[] bytes = new byte[7];
        System.arraycopy(packet1.toBytes(), 0, bytes, 0, 7);
        network.send(bytes);
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
        assertThrows(TimeoutException.class, () -> network.receive());
    }

    @Test
    void invalidMByte() throws IOException,  TimeoutException {
        int port = 230;
        Server server = null;
        try {
            server = new Server(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();

        Socket socket = new Socket("localhost", port);

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        Packet packet = new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_PRODUCT_NAME_TO_GROUP, 1, "Client N1"));
        Network network = new Network(in, out, 5, TimeUnit.SECONDS);

        byte[] bytes = packet.toBytes();

        bytes[0] = 39;
        network.send(bytes);

        byte[] receive = network.receive();
        Packet answerPacketFromServer = Packet.fromBytes(receive);


        assertEquals("Wrong header", answerPacketFromServer.getBMsq().getPayload());
    }

}
