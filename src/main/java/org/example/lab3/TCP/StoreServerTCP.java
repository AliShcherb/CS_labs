package org.example.lab3.TCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class StoreServerTCP extends Thread {

    public ServerSocket server;

    private int                port;
    private ThreadPoolExecutor connectionPool;
    private ThreadPoolExecutor processPool;
    private int                clientTimeout;

    /**
     * @param port
     * @param maxConnectionThreads max number of connected users
     * @param maxProcessThreads    max number of heavy threads
     * @param maxClientTimeout     the timeout must be > 0. A timeout of zero is interpreted as an infinite timeout.
     * @throws IOException
     */
    public StoreServerTCP(int port, int maxConnectionThreads, int maxProcessThreads, int maxClientTimeout)
            throws IOException {
        super("Server");
        if (maxClientTimeout < 0) throw new IllegalArgumentException("timeout can't be negative");
        this.port = port;
        connectionPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxConnectionThreads);
        processPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxProcessThreads);
        this.clientTimeout = maxClientTimeout;
        server = new ServerSocket(port);
    }


    @Override
    public void run() {
        System.out.println("Server running on port: " + port);

        for(int i = 0; i < connectionPool.getMaximumPoolSize(); i++) {
            // connection pool is used to receive the client request only
            connectionPool.execute(() -> {
                while(true) {
                    try {
                        ClientHandler clientHandler = new ClientHandler(server.accept(), processPool, clientTimeout);
                        // handle the client request in a separate thread pool
                        processPool.execute(clientHandler);
                    } catch (SocketException e) {
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void shutdown() {
        try {
            server.close();
            connectionPool.shutdownNow();
            processPool.shutdownNow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
