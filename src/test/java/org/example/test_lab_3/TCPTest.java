package org.example.test_lab_3;

import org.example.lab3.*;
import org.example.lab3.TCP.Exceptions.InactiveException;
import org.example.lab3.TCP.Exceptions.OverloadException;
import org.example.lab3.TCP.StoreClientTCP;
import org.example.lab3.TCP.StoreServerTCP;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TCPTest {

    private class ThrowableWrapper {
        public Throwable e;
    }

    @Test
    public void inactiveException() throws InterruptedException {
        int port = 888;

        ThrowableWrapper wrapper = new ThrowableWrapper();

        Thread.UncaughtExceptionHandler h = (th, ex) -> wrapper.e = ex;

        Packet packet1 = new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_PRODUCT_NAME_TO_GROUP, 1, "Client N1"));
        Packet packet2 = new Packet((byte) 1, 2, new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 2, "Client N2"));

       StoreClientTCP client1 = new StoreClientTCP(port, packet1);
        StoreClientTCP client2 = new StoreClientTCP(port, packet2);

        client1.setUncaughtExceptionHandler(h);
        client2.setUncaughtExceptionHandler(h);

        client1.start();
        client1.join();

        client2.start();
        client2.join();


        assertThrows(InactiveException.class, () -> {throw wrapper.e;});
    }

    @Test
    public void overloadException() throws IOException {
        int port = 999;

            StoreServerTCP  server = new StoreServerTCP(port, 1, 1, 1000);

            server.start();

        ThrowableWrapper wrapper = new ThrowableWrapper();

        Thread.UncaughtExceptionHandler h = (th, ex) -> wrapper.e = ex;

        Packet packet = new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_GROUP_OF_PRODUCTS, 1, "Client N1"));

        int amount = 5;
        ArrayList<StoreClientTCP> clients = new ArrayList<>(amount);


        for (int i = 0; i < amount; i++) {
            clients.add(new StoreClientTCP(port, packet));
        }

        for (int i = 0; i < amount; i++) {
            clients.get(i).setUncaughtExceptionHandler(h);
        }

        for (int i = 0; i < amount; i++) {
            clients.get(i).start();
        }

        try {
            for (int i = 0; i < amount; i++) {
                clients.get(i).join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.shutdown();

        assertThrows(OverloadException.class, () -> {throw wrapper.e;});
    }
}
