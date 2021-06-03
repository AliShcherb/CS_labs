package org.example.test_lab_3;

import org.example.lab3.*;
import org.example.lab3.TCP.Exceptions.InactiveServerException;
import org.example.lab3.TCP.Exceptions.ServerOverloadException;
import org.example.lab3.TCP.StoreClientTCP;
import org.example.lab3.TCP.StoreServerTCP;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TCPTest {

    //it is better to run tests separately

    private class ThrowableWrapper {
        public Throwable e;
    }

    @Test
    public void shouldPass_whenInactiveServerExceptionThrown() {
        int port = 43210;

        ThrowableWrapper wrapper = new ThrowableWrapper();

        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                wrapper.e = ex;
            }
        };


        Packet packet = null;
        packet =
                new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_GROUP_OF_PRODUCTS, 1, "client1"));
        StoreClientTCP clientTCP = new StoreClientTCP(port, packet);

        clientTCP.setUncaughtExceptionHandler(h);

        clientTCP.start();
        try {
            clientTCP.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThrows(InactiveServerException.class, () -> {throw wrapper.e;});
    }

    @Test
    public void shouldPass_whenServerOverloadExceptionThrown() {
        int port = 43210;

        StoreServerTCP server = null;
        try {
            server = new StoreServerTCP(port, 1, 1, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();

        ThrowableWrapper wrapper = new ThrowableWrapper();

        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                wrapper.e = ex;
            }
        };


        Packet packet = null;
        packet =
                new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_GROUP_OF_PRODUCTS, 1, "client1"));

        int clientsAmount = 10;
        ArrayList<StoreClientTCP> clients = new ArrayList<>(clientsAmount);


        for (int i = 0; i < clientsAmount; i++) {
            clients.add(new StoreClientTCP(port, packet));
        }

        for (int i = 0; i < clientsAmount; i++) {
            clients.get(i).setUncaughtExceptionHandler(h);
        }

        for (int i = 0; i < clientsAmount; i++) {
            clients.get(i).start();
        }

        try {
            for (int i = 0; i < clientsAmount; i++) {
                clients.get(i).join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.shutdown();

        assertThrows(ServerOverloadException.class, () -> {throw wrapper.e;});
    }


    @Test
    public void shouldPass_whenNoExceptionThrown() {
        int port = 43210;

        StoreServerTCP server = null;
        try {
            server = new StoreServerTCP(port, 5, 1, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();

        ThrowableWrapper wrapper = new ThrowableWrapper();

        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                wrapper.e = ex;
            }
        };


        Packet packet = null;
        packet =
                new Packet((byte) 1, 1, new Message(Message.cTypes.ADD_GROUP_OF_PRODUCTS, 1, "client1"));

        int clientsAmount = 10;
        ArrayList<StoreClientTCP> clients = new ArrayList<>(clientsAmount);


        for (int i = 0; i < clientsAmount; i++) {
            clients.add(new StoreClientTCP(port, packet));
        }

        for (int i = 0; i < clientsAmount; i++) {
            clients.get(i).setUncaughtExceptionHandler(h);
        }

        for (int i = 0; i < clientsAmount; i++) {
            clients.get(i).start();
        }

        try {
            for (int i = 0; i < clientsAmount; i++) {
                clients.get(i).join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.shutdown();

        assertEquals(null, wrapper.e);
    }
}
