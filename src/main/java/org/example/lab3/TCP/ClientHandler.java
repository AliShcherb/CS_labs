package org.example.lab3.TCP;

import org.example.lab3.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandler implements Runnable {

    private Network network;

    private ThreadPoolExecutor executor;

    private AtomicInteger processingAmount = new AtomicInteger(0);

    public ClientHandler(Socket clientSocket, ThreadPoolExecutor executor, int maxTimeout) throws IOException {
        network = new Network(clientSocket, maxTimeout);
        this.executor = executor;
    }
    public long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    @Override
    public void run() {
        Thread.currentThread().setName(Thread.currentThread().getName() + " - ClientHandler");
        try {
            Packet helloPacket = null;
            helloPacket = new Packet((byte) 0, 0,
                    new Message(Message.cTypes.OK, 0, "connection established"));
            network.send(helloPacket.toBytes());

            while (true) {
                byte[] packetBytes = network.receive();
                if (packetBytes == null) {
                    System.out.println("client timeout");
                    break;
                }
                handlePacketBytes(Arrays.copyOf(packetBytes, packetBytes.length));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    private void handlePacketBytes(byte[] packetBytes) {
        processingAmount.incrementAndGet();

        CompletableFuture.supplyAsync(() -> {
            //to encode in parallel thread todo non synchronized decryption
            Packet packet = null;
            packet = Packet.fromBytes(packetBytes);
            return packet;
        }, executor)

                .thenAcceptAsync((inputPacket -> {
                    Packet answerPacket = null;
                    try {
                        answerPacket = Processor.process(inputPacket);
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                        System.err.println("BadPaddingException");
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                        System.err.println("IllegalBlockSizeException");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        System.err.println("NullPointerException");
                    }

                    try {
                        network.send(answerPacket.toBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    processingAmount.decrementAndGet();

                }), executor)

                .exceptionally(ex -> {
                    ex.printStackTrace();
                    processingAmount.decrementAndGet();
                    return null;
                });
    }


    public void shutdown() {
        while (processingAmount.get() > 0) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        network.shutdown();
    }


}
