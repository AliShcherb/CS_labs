package org.example.test_lab_1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.example.lab1.Decryption;
import org.example.lab1.Message;
import org.example.lab1.Packet;
import org.junit.Assert;
import org.junit.Test;

public class PacketTest {

    @Test
    public void shouldGenerateValidPacket() throws DecoderException {
        byte bSrc = 15;
        long bPktId = 100;
        Message message = TestUtils.generateTestMessage();
        byte[] messageBytes = message.toBytes();
        int messageSize = messageBytes.length;
        Packet packet = new Packet(bSrc, bPktId, message);
        int expectedPacketSize = 1 + 1 + 8 + 4 + 2 + messageSize + 2;

        byte[] packetBytes = packet.toBytes();

        Assert.assertEquals(expectedPacketSize, packetBytes.length);
        ByteBuffer byteBuffer = ByteBuffer.wrap(packetBytes);

        byteBuffer.order(ByteOrder.BIG_ENDIAN);

        verifyHeader(bSrc, bPktId, messageSize, byteBuffer);
        verifyPayload(messageBytes, byteBuffer);
    }

    private void verifyHeader(byte bSrc, long bPktId, int messageSize, ByteBuffer byteBuffer) throws DecoderException {
        //bMagic
        int expectedMagicByte = 0x13;
        Assert.assertEquals(expectedMagicByte, byteBuffer.get());
        //bSrc
        Assert.assertEquals(bSrc, byteBuffer.get());
        //bPktId
        Assert.assertEquals(bPktId, byteBuffer.getLong());
        //wLen
        Assert.assertEquals(messageSize, byteBuffer.getInt());
        //CRC16
        byte[] headCRC16 = new byte[2];
        byteBuffer.get(headCRC16);
        Assert.assertArrayEquals(Hex.decodeHex("1f37"), headCRC16);
    }

    private void verifyPayload(byte[] messageBytes, ByteBuffer byteBuffer) throws DecoderException {

        byte[] actualMessage = new byte[messageBytes.length];
        byteBuffer.get(actualMessage);
        byte[] decryptedBytes = Decryption.decrypt(actualMessage);
        Assert.assertArrayEquals(messageBytes, decryptedBytes);

        //CRC16
        byte[] dataCRC16 = new byte[2];
        byteBuffer.get(dataCRC16);
        Assert.assertArrayEquals(Hex.decodeHex("689D"), dataCRC16);
    }
}