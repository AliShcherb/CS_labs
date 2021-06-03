package org.example.lab3.UDP;

import lombok.SneakyThrows;

public class MapCleanerForClient extends Thread {

    public boolean isActive = true;
    final int timeoutStandart = 1500;

    MapCleanerForClient() {
        this.start();
    }

    @SneakyThrows
    @Override
    public void run() {
        while (isActive) {
            this.sleep(timeoutStandart / 3);

            if (!isActive) {
                break;
            }
            StoreServerUDP.clearMaps();
        }
    }
}
