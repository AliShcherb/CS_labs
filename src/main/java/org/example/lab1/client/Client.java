package org.example.lab1.client;

import org.example.lab1.Message;
import org.example.lab1.Packet;

public class Client {
    public byte[] prepareTestRequest() {
        Message message = new Message(10, 20, "{\"name\" : \"Alisher\"}");
        return new Packet((byte) 1, 100, message).toBytes();
    }
}
