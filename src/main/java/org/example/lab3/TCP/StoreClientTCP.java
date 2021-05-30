package org.example.lab3.TCP;

import org.example.lab3.*;
import org.example.lab3.TCP.Exceptions.InactiveServerException;
import org.example.lab3.TCP.Exceptions.ServerOverloadException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;



public class StoreClientTCP extends Thread {

    private int     port;
    private Network network;
    private Packet  packet;

    private int connectionTimeoutUnit = 500;


    public StoreClientTCP(int port, Packet packet) {
        this.port = port;
        this.packet = packet;
    }

    private void connect() throws IOException {
        int attempt = 0;

        while (true) {
            try {
                Socket socket = new Socket("localhost", port);
                network = new Network(socket, 3000);
                return;
            } catch (ConnectException e) {
                if (attempt > 3) {
                    System.out.println(Thread.currentThread().getName() + " server is inactive");
                    throw new InactiveServerException();
                }

                try {
                    Thread.sleep(connectionTimeoutUnit + connectionTimeoutUnit * attempt);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                ++attempt;
            }
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Client " + Thread.currentThread().getId() + ":");

        System.out.println(Thread.currentThread().getName() + " start");

        try {
            int attempt = 0;
            while (true) {

                if (attempt == 3) throw new ServerOverloadException();

                connect();

                byte[] helloPacketBytes = network.receive();
                if (helloPacketBytes == null) {
                    System.out.println(Thread.currentThread().getName() + " server timeout");
                    ++attempt;
                    continue;
                }
                Packet helloPacket = Packet.fromBytes(helloPacketBytes);
                System.out.println(Thread.currentThread().getName() + " answer from server: " +
                        helloPacket.getBMsq().getPayload());


                network.send(packet.toBytes());

                byte[] dataPacketBytes = network.receive();
                if (dataPacketBytes == null) {
                    System.out.println(Thread.currentThread().getName() + " server timeout");
                    ++attempt;
                    continue;
                }
                Packet dataPacket = Packet.fromBytes(dataPacketBytes);
                System.out.println(Thread.currentThread().getName() + " answer from server: " +
                        dataPacket.getBMsq().getPayload());
                break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (network != null) {
                network.shutdown();
            }
            System.out.println(Thread.currentThread().getName() + " end");
        }
    }

    public void shutdown() {
        //todo
    }
}
