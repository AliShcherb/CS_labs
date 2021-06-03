package org.example.lab2;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

import static java.lang.Thread.currentThread;
import static java.util.concurrent.CompletableFuture.*;

public class ClientProcessor implements Runnable {

    private Socket socket;

    private InputStream in;
    private OutputStream out;

    private Network network;

    private ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);


    public ClientProcessor(Socket socket, int max, TimeUnit unit) throws IOException {
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();
        network = new Network(in, out, max, unit);
    }

    @Override
    public void run() {
        currentThread().setName(currentThread().getName() + "ClientProcessor");
        try {

            while (true) {
                byte[] packetBytes = network.receive();
                handlePacketBytes(Arrays.copyOf(packetBytes, packetBytes.length));
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    private void handlePacketBytes(byte[] packetBytes) {
        supplyAsync(() -> {
            Packet packet;
            packet = Packet.fromBytes(packetBytes);
            return packet; }, threadPoolExecutor)

                .thenAcceptAsync((inputPacket -> {
                    Packet answerPacket = null;
                    try {
                        answerPacket = Processor.process(inputPacket);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        System.out.println(e);
                    }

                    try {
                        network.send(answerPacket.toBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }), threadPoolExecutor)

                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }


    public void shutdown() {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(currentThread().getName() + "  disconnect");

        try {
            if (in.available() > 0) {
                Thread.sleep(3000);
            }
            in.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

        } finally {

            if (threadPoolExecutor.getActiveCount() > 0) {
                try {
                    threadPoolExecutor.awaitTermination(2, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            threadPoolExecutor.shutdown();

            try {
                try {
                    out.close();
                } finally {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
