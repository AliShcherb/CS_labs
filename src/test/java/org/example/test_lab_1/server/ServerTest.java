package org.example.test_lab_1.server;

import org.example.lab1.Packet;
import org.example.test_lab_1.TestUtils;
import org.example.lab1.server.Server;
import org.junit.Assert;
import org.junit.Test;

public class ServerTest {

    @Test
    public void shouldReadValidPacket() {
        Server server = new Server();
        Packet expectedPacket = new Packet((byte) 15, 100, TestUtils.generateTestMessage());
        byte[] packetBytes = expectedPacket.toBytes();

        Packet actualPacket = server.readPacket(packetBytes);
        Assert.assertEquals(expectedPacket, actualPacket);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfPacketPayloadHasWrongCRC() {
        Server server = new Server();
        Packet expectedPacket = new Packet((byte) 15, 100, TestUtils.generateTestMessage());
        byte[] packetBytes = expectedPacket.toBytes();

        // Поменяем любой из байтов (представим что он был поврежден при передаче по сети)
        packetBytes[packetBytes.length - 1] = (byte) (packetBytes[20] + 1);

        // Проверим, что сервер падает с ошибкой потому что CRC не сходится
        Packet actualPacket = server.readPacket(packetBytes);
        Assert.assertEquals(expectedPacket, actualPacket);
    }

}
