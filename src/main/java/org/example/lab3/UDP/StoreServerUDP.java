package org.example.lab3.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class StoreServerUDP extends Thread {
    private DatagramSocket dSocket;
    private int port;

    private ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    static ConcurrentHashMap<Integer, Long> map1 = new ConcurrentHashMap<>();
    static ConcurrentHashMap<Integer, Long> map2 = new ConcurrentHashMap<>();


    public StoreServerUDP(int listenPort) {
        this.port = listenPort;
        try {
            dSocket = new DatagramSocket(listenPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            dSocket.setSoTimeout(7000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.start();
    }

    @Override
    public void run() {
        System.out.println("Port: " + port);

        MapCleanerForClient cleaner = new MapCleanerForClient();

        while (true) {
            byte[] buffer = new byte[2048];
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            try {
                dSocket.receive(datagramPacket);
            } catch (IOException e) {
                threadPoolExecutor.shutdown();
                dSocket.close();
                cleaner.isActive = false;
                break;
            }
            threadPoolExecutor.execute(new UDPResponder(datagramPacket));
        }

    }

    public synchronized static boolean packetValidation(Integer userId, Long packetId) {
        if (!map2.containsKey(userId)) {
            map1.put(userId, packetId);
            map2.put(userId, packetId);
            return true;
        } else if (map2.containsKey(userId)) {
            if (map2.get(userId).compareTo(packetId) >= 0) return false;
            map2.put(userId, packetId);
            return true;
        }
        return false;
    }

    public synchronized static void clearMaps() {
        for (Integer userId : map2.keySet()) {
            if (map1.containsKey(userId) && map1.get(userId).compareTo(map2.get(userId)) == 0) {
                map1.remove(userId);
                map2.remove(userId);
            }
        }
        map1.putAll(map2);
    }


}
