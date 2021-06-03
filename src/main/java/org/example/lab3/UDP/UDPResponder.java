package org.example.lab3.UDP;
import org.example.lab3.*;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class UDPResponder implements Runnable {
    private Packet answerPac = null;
    private DatagramSocket ds;
    private DatagramPacket dp;
    private Object lock = new Object();

    UDPResponder(DatagramPacket dp) {
        this.dp = dp;
    }

    @Override
    public void run() {
        // System.out.println("In run");

        synchronized (lock) {

            Packet pacToBeProcessed = null;
            // System.out.println("In get");
            pacToBeProcessed = Packet.fromBytes(dp.getData());

            if(StoreServerUDP.packetCanBeProcessed(pacToBeProcessed.getBMsq().getBUserId(), pacToBeProcessed.getBPktId())) {
                //  System.out.println("In process");
                answerPac = Processor.process(pacToBeProcessed);

                try {
                    ds = new DatagramSocket();
                } catch (SocketException e) {
                    e.printStackTrace();
                }

                try {
                    ds.send(new DatagramPacket(answerPac.toBytes(), answerPac.toBytes().length, dp.getAddress(), dp.getPort()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ds.close();


            } else {


                answerPac = new Packet(pacToBeProcessed.getBSrc(), pacToBeProcessed.getBPktId(),
                        new Message(Message.cTypes.EXCEPTIONS, 0, "This packet has been processed yet!"));

                try {
                    ds = new DatagramSocket();
                } catch (SocketException e) {
                    e.printStackTrace();
                }

                try {
                    ds.send(new DatagramPacket(answerPac.toBytes(), answerPac.toBytes().length, dp.getAddress(), dp.getPort()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ds.close();

            }

        }

    }

}
