package org.example.lab2;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.*;

public class ClientProcessor implements Runnable {

    private Socket clientSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Network network;
    private ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);


    public ClientProcessor(Socket socket, int maxTimeout, TimeUnit timeUnit) throws IOException {
        this.clientSocket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        network = new Network(inputStream, outputStream, maxTimeout, timeUnit);
    }

    @Override
    public void run() {
        Thread.currentThread().setName(Thread.currentThread().getName() + " - ClientHandler");
        try {

            while (true) {
                byte[] packetBytes = network.receive();
                handlePacketBytes(Arrays.copyOf(packetBytes, packetBytes.length));
            }

        } catch (IOException e) {
            if (e.getMessage().equals("Stream closed.")) {
                //todo notify client?
            } else {
                e.printStackTrace();
            }
        } catch (TimeoutException e) {
            System.out.println("client timeout");
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    private void handlePacketBytes(byte[] packetBytes) {
        CompletableFuture.supplyAsync(() -> {
            //to encode in parallel thread todo non synchronized decryption
            Packet packet = null;
            packet = new Packet(packetBytes);
            return packet;
        }, threadPoolExecutor)

                .thenAcceptAsync((inputPacket -> {
                    Packet answerPacket = null;
                    try {
                        answerPacket = Processor.process(inputPacket);
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                        System.out.println("BadPaddingException");
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                        System.out.println("IllegalBlockSizeException");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        System.out.println("null");
                    }

                    try {
                        network.send(answerPacket.toBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }), threadPoolExecutor)

                .exceptionally(ex -> {
                    ex.printStackTrace();
//            System.err.println(ex.toString());
                    return null;
                });
    }


    public void shutdown() {
        //todo shutdown
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " shutdown");
        //close inputStream
        try {
            if (inputStream.available() > 0) {
                Thread.sleep(5000);
            }
            inputStream.close();
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
                    outputStream.close();
                } finally {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
