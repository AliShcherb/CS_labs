package org.example.lab3.TCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class StoreServerTCP extends Thread {

    public ServerSocket server;

    private int                port;
    private ThreadPoolExecutor connectionPool;
    private ThreadPoolExecutor processPool;
    private int                clientTimeout;

    public StoreServerTCP(int port, int maxConnectionThreads, int maxProcessThreads, int maxClientTimeout)
            throws IOException {
        this.port = port;
        connectionPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxConnectionThreads);
        processPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxProcessThreads);
        this.clientTimeout = maxClientTimeout;
        server = new ServerSocket(port);
    }


    @Override
    public void run() {
        System.out.println("Port: " + port);
        int i = 0;
        while (i < connectionPool.getMaximumPoolSize()){
            connectionPool.execute(() -> {
                while(true) {
                    try {
                        ClientProcessor clientProcessor = new ClientProcessor(server.accept(), processPool, clientTimeout);
                        processPool.execute(clientProcessor);
                    } catch (SocketException e) {
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            i++;
        }
    }

    public void shutdown() throws IOException {
            server.close();
            connectionPool.shutdownNow();
            processPool.shutdownNow();
    }

}