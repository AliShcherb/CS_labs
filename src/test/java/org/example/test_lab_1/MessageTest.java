package org.example.test_lab_1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import org.example.lab1.Message;
import org.junit.Assert;
import org.junit.Test;

public class MessageTest {

    @Test
    public void shouldGenerateValidMessage() {
        String testJsonPayload = "{\"name\": \"Alisher\"}";
        int cType = 10;
        int bUserId = 20;

        int expectedMessageSize = 4 + 4 +
                testJsonPayload.getBytes().length;


        Message message = new Message(cType, bUserId, testJsonPayload);
        byte[] messageBytes = message.toBytes();

        Assert.assertEquals(expectedMessageSize, messageBytes.length);
        ByteBuffer byteBuffer = ByteBuffer.wrap(messageBytes);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);

        Assert.assertEquals(cType, byteBuffer.getInt());
        Assert.assertEquals(bUserId, byteBuffer.getInt());

        byte[] payload = new byte[testJsonPayload.length()];
        byteBuffer.get(payload);
        Assert.assertEquals(testJsonPayload, new String(payload, StandardCharsets.UTF_8));
    }
}
