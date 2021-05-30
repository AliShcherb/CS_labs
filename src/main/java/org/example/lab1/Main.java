package org.example.lab1;

import org.example.lab1.client.Client;
import org.example.lab1.server.Server;

public class Main {
    public static void main(String[] args) {


        Client client = new Client();
        Server server = new Server();

        byte[] requestPacket = client.prepareTestRequest();
        Packet packet = server.readPacket(requestPacket);

        JsonEncode js=new JsonEncode();
        String s;
        s=js.toJSON(packet);
        System.out.println(s);

        System.out.println(packet);
    }
}
