package org.example.lab3.TCP;

import org.example.lab3.*;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.CompletableFuture.*;

public class ClientProcessor implements Runnable {

    private Network network;

    private final ThreadPoolExecutor threadPoolExecutor;
    public final static Integer SUPER_ZERO = 0;
    private int timeoutStandart = 500;
    private AtomicInteger atomicInteger = new AtomicInteger(SUPER_ZERO);

    public ClientProcessor(Socket clientSocket, ThreadPoolExecutor executor, int maxTimeout) throws IOException {
        network = new Network(clientSocket, maxTimeout);
        this.threadPoolExecutor = executor;
    }

    @Override
    public void run() {
        try {

            Packet answerPacket = new Packet((byte) 0, SUPER_ZERO,
                    new Message(Message.cTypes.STANDART_ANSWER, SUPER_ZERO, "Сonnected"));
            network.send(answerPacket.toBytes());

            while (true) {
                byte[] packetBytes = network.receive();
                if (packetBytes == null) {
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
        atomicInteger.incrementAndGet();

        supplyAsync(() -> {
            Packet packet = null;
            packet = Packet.fromBytes(packetBytes);
            return packet;
        }, threadPoolExecutor)

                .thenAcceptAsync((inputPacket -> {
                    Packet answerPacket = null;
                    try {
                        answerPacket = Processor.process(inputPacket);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        System.err.println("NullPointerException");
                    }

                    try {
                        network.send(answerPacket.toBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    atomicInteger.decrementAndGet();

                }), threadPoolExecutor)

                .exceptionally(ex -> {
                    ex.printStackTrace();
                    atomicInteger.decrementAndGet();
                    return null;
                });
    }


    public void shutdown() {
        while (atomicInteger.get() > SUPER_ZERO) {
            try {
                Thread.sleep(timeoutStandart*10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        network.shutdown();
    }


}
