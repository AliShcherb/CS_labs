package org.example.lab1.server;

import org.example.lab1.Packet;

public class Server {


    public Packet readPacket(byte[] packetBytes) {
        return Packet.fromBytes(packetBytes);
    }
}
