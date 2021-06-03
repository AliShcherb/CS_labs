package org.example.lab2;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
@Data
public class Client extends Thread {

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private Packet packet;
    private int port;

    public Client(int port, Packet packet) {
        this.port = port;
        this.packet = packet;
    }

    @SneakyThrows
    @Override
    public void run() {
        int clientId = (int) Thread.currentThread().getId();
        Thread.currentThread().setName("Client:" + clientId);

        try {

            socket = new Socket("localhost", port);
            in = socket.getInputStream();
            out = socket.getOutputStream();

            Network network = new Network(in, out, 10, TimeUnit.SECONDS);


            network.send(packet.toBytes());

            byte[] packetBytes = network.receive();
            Packet packet = Packet.fromBytes(packetBytes);
            // System.out.println("Packet:" + packet);
            System.out.println(Thread.currentThread().getName() + " \n Received answer: " + packet.bMsq.getPayload());
            in.close();
            out.close();

        } finally {
            ///bruh
            socket.close();
        }
    }

    @SneakyThrows
    public void shutdown() {
        try {

            in.close();
            out.close();
        } finally {
            socket.close();
        }

    }
}