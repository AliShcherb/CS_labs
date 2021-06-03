package org.example.lab3.UDP;

import lombok.SneakyThrows;
import org.example.lab3.Message;
import org.example.lab3.Packet;
import org.example.lab3.Processor;

import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class UDPResponder implements Runnable {
    private Packet answerPacket = null;
    private DatagramSocket dSocket;
    private DatagramPacket dPacket;
    private Object lock = new Object();

    UDPResponder(DatagramPacket dp) {
        this.dPacket = dp;
    }

    @SneakyThrows
    @Override
    public void run() {

        synchronized (lock) {

            Packet toProceed = Packet.fromBytes(dPacket.getData());

            if (StoreServerUDP.packetValidation(toProceed.getBMsq().getBUserId(), toProceed.getBPktId())) {

                answerPacket = Processor.process(toProceed);
                dSocket = new DatagramSocket();
                dSocket.send(new DatagramPacket(answerPacket.toBytes(), answerPacket.toBytes().length, dPacket.getAddress(), dPacket.getPort()));
                dSocket.close();

            } else {

                answerPacket = new Packet(toProceed.getBSrc(), toProceed.getBPktId(),
                        new Message(Message.cTypes.EXCEPTIONS, 0, "Already processed"));

                dSocket = new DatagramSocket();
                dSocket.send(new DatagramPacket(answerPacket.toBytes(), answerPacket.toBytes().length, dPacket.getAddress(), dPacket.getPort()));

                dSocket.close();

            }

        }

    }

}
