package org.example.lab3.UDP;

import org.example.lab3.*;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.net.*;

public class StoreClientUDP extends Thread {
    DatagramSocket ds = null;
    DatagramPacket dp = null;
    InetAddress ip = InetAddress.getLocalHost();
    private int port;
    private Packet packet;
    private Packet answerPacket;
    private byte[] packetBytes;


    public StoreClientUDP(int port, Packet packet) throws UnknownHostException {
        this.port = port;
        this.packet = packet;

        try {
            ds = new DatagramSocket();
            ds.setSoTimeout(1500);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        this.start();
    }

    @Override
    public void run() {

        sendAndReceive(packet);

    }

    private void sendAndReceive(Packet packet) {

        if (ds.isClosed()) {
            try {
                ds = new DatagramSocket();
                ds.setSoTimeout(1500);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }


        packetBytes = packet.toBytes();
        dp = new DatagramPacket(packetBytes, packetBytes.length, ip, port);
        boolean received = false;

        try {
            ds.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {

            byte[] buff = new byte[1024];
            DatagramPacket incomingDatagramPacket = new DatagramPacket(buff, buff.length);

            try {
                ds.receive(incomingDatagramPacket);
            } catch (IOException e) {
                if (received) {             //if correct packet received from server
                    System.out.println("Client Socket timed out!");
                    ds.close();
                    break;
                } else {
                    ds.close();
                    System.out.println("resending packet (id : " + packet.getBPktId() + ") ..");
                    sendAndReceive(packet);
                    break;
                }
            }

            Packet answerPacket = null;
            answerPacket = Packet.fromBytes(incomingDatagramPacket.getData());
            this.answerPacket = answerPacket; //this field was made to assert incoming message from server

            Long a = answerPacket.getBPktId();
            Long b = packet.getBPktId();
            if (a==b) received = true;
            System.out.println("Message from server : " + answerPacket.getBMsq().getPayload() + " ; Packet id : " + answerPacket.getBPktId());
        }
    }

    public Packet getAnswerPacket() throws InterruptedException {
        while (true) {
            if (answerPacket != null) {
                break;
            }
            this.sleep(1);
        }
        return answerPacket;
    }

}
