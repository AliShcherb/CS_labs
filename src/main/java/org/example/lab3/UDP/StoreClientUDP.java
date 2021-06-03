package org.example.lab3.UDP;

import org.example.lab3.Packet;

import java.io.IOException;
import java.net.*;

public class StoreClientUDP extends Thread {
    DatagramSocket dSocket = null;
    DatagramPacket dPacket = null;
    InetAddress ip = InetAddress.getLocalHost();
    private int port;
    private Packet packet;
    private Packet answerPacket;
    private byte[] bytes;
    final int timeoutStandart = 1500;


    public StoreClientUDP(int port, Packet packet) throws UnknownHostException {
        this.port = port;
        this.packet = packet;

        try {
            dSocket = new DatagramSocket();
            dSocket.setSoTimeout(timeoutStandart);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        this.start();
    }

    private void sendReceive(Packet packet) throws SocketException {

        if (dSocket.isClosed()) {
            dSocket = new DatagramSocket();
            dSocket.setSoTimeout(timeoutStandart);
        }
        bytes = packet.toBytes();
        dPacket = new DatagramPacket(bytes, bytes.length, ip, port);
        boolean received = false;

        try {
            dSocket.send(dPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {

            byte[] buff = new byte[1024];
            DatagramPacket incomingDatagramPacket = new DatagramPacket(buff, buff.length);

            try {
                dSocket.receive(incomingDatagramPacket);
            } catch (IOException e) {
                if (received) {
                    dSocket.close();
                    break;
                } else {
                    dSocket.close();
                    System.out.println("Resending packet id(" + packet.getBPktId() + ")");
                    sendReceive(packet);
                    break;
                }
            }

            Packet answerPacket = null;
            answerPacket = Packet.fromBytes(incomingDatagramPacket.getData());
            this.answerPacket = answerPacket;
            Long a = answerPacket.getBPktId();
            Long b = packet.getBPktId();
            if (a == b) received = true;
            System.out.println("Answer for id(" + answerPacket.getBPktId() + "): " + answerPacket.getBMsq().getPayload());
        }
    }

    @Override
    public void run() {

        try {
            sendReceive(packet);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public Packet getAnswer() throws InterruptedException {
        while (true) {
            if (answerPacket != null) {
                break;
            }
            this.sleep(timeoutStandart / 1000);
        }
        return answerPacket;
    }

}
