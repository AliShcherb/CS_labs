package org.example.lab2;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server extends Thread {

    public ServerSocket server;
    private ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private int port;

    public Server(int port) throws IOException {
        this.port = port;
        server = new ServerSocket(port);
    }


    @Override
    public void run() {

        try {
            System.out.println("Port: " + port);

            while (true) {
                threadPoolExecutor.execute(new ClientProcessor(server.accept(), 2, TimeUnit.SECONDS));
            }

        } catch (SocketException e) {
            System.out.println(e);

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            threadPoolExecutor.shutdown();
        }

    }

    public void shutdown() {
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
