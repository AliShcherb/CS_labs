package org.example.lab3.TCP;

import lombok.SneakyThrows;
import org.example.lab3.*;
import org.example.lab3.TCP.Exceptions.InactiveException;
import org.example.lab3.TCP.Exceptions.OverloadException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;



public class StoreClientTCP extends Thread {

    private int     port;
    private Network network;
    private Packet  packet;

    private int timeoutStandart = 500;


    public StoreClientTCP(int port, Packet packet) {
        this.port = port;
        this.packet = packet;
    }

    @SneakyThrows
    private void connect()  {
        int i = 0;

        while (true) {
            try {
                Socket socket = new Socket("localhost", port);
                network = new Network(socket, timeoutStandart*6);
                return;
            } catch (ConnectException e) {
                if (i > 3) {
                    System.out.println("Inactive error");
                    throw new InactiveException();
                }
                    Thread.sleep(timeoutStandart + timeoutStandart * i);
                ++i;
            }
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Client " + Thread.currentThread().getId() + ":");

        try {
            int attempt = 0;
            while (true) {

                if (attempt == 3) throw new OverloadException();

                connect();

                byte[] received = network.receive();
                if (received == null) {
                    ++attempt;
                    continue;
                }
                Packet answerPacket = Packet.fromBytes(received);
                System.out.println("Answer for id(" + Thread.currentThread().getId() + "): " + answerPacket.getBMsq().getPayload());

                network.send(packet.toBytes());

                byte[] dataPacketBytes = network.receive();
                if (dataPacketBytes == null) {
                    System.out.println("Server timeout for id("+Thread.currentThread().getId()+")");
                    ++attempt;
                    continue;
                }
                Packet dataPacket = Packet.fromBytes(dataPacketBytes);
                System.out.println("Answer for id("+Thread.currentThread().getId() + "): " +
                        dataPacket.getBMsq().getPayload());
                break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (network != null) {
                network.shutdown();
            }
        }
    }

}
