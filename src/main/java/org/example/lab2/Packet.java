package org.example.lab2;
import static org.example.lab2.CRC16.calculateCRC;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import lombok.Data;
import lombok.SneakyThrows;
import org.example.lab1.Decryption;
import org.example.lab1.Encryption;

@Data
public class Packet {
    public final static Byte    B_MAGIC       = 0x13;
    public final static Integer HEADER_LENGTH = 16;
    public final static Integer CRC16_LENGTH = 2;
    public Message bMsq;
    public final byte bSrc;
    public final long bPktId;
    private final Integer wLen;
    private static Message.cTypes cType;

    public Packet(Byte bSrc, long bPktId, Message bMsq) {
        this.bSrc = bSrc;
        this.bPktId = bPktId;
        this.bMsq = bMsq;
        wLen = bMsq.toBytes().length;
    }



    @SneakyThrows
    public static Packet fromBytes(byte[] encodedPacket) {
        ByteBuffer buffer = ByteBuffer.wrap(encodedPacket).order(ByteOrder.BIG_ENDIAN);
        if (buffer.get() != B_MAGIC)
            throw new IllegalArgumentException("Unexpected bMagic");

        byte clientId = buffer.get();
        System.out.println("Client ID: " + clientId);

        long packetId = buffer.getLong();
        System.out.println("Packet ID: " + packetId);

        int mLength = buffer.getInt();
        System.out.println("Length:" + mLength);

        short crcHead = buffer.getShort();
        System.out.println("CRC16 of header:" + Integer.toBinaryString(0xFFFF & crcHead));

        byte[] head = ByteBuffer.allocate(14)
                .order(ByteOrder.BIG_ENDIAN)
                .put(B_MAGIC)
                .put(clientId)
                .putLong(packetId)
                .putInt(mLength)
                .array();

        if (calculateCRC(head) != crcHead) {
            throw new IllegalArgumentException("Head CRC16");
        }

        byte[] messageBytes = new byte[mLength];
        buffer.get(messageBytes);
        byte[] decryptedBytes = Decryption.decrypt(messageBytes);

        ByteBuffer messageBuffer = ByteBuffer.wrap(decryptedBytes)
                .order(ByteOrder.BIG_ENDIAN);

        int bUserId = messageBuffer.getInt();
        byte[] messageBody = new byte[mLength - 8];
        messageBuffer.get(messageBody);


        Message message = new Message(cType, bUserId, new String(messageBody));

        message.setCType(buffer.getInt());
        short crcMessage = buffer.getShort();
        if (calculateCRC(message.toBytes()) != crcMessage) {
            throw new IllegalArgumentException("Message CRC16");
        }

        return new Packet(clientId, packetId, message);
    }


    public byte[] toBytes() {
        byte[] messageBytes = bMsq.toBytes();
        byte[] encryptedBytes = Encryption.encrypt(messageBytes);

        byte[] head = ByteBuffer.allocate(14)
                .order(ByteOrder.BIG_ENDIAN)
                .put(B_MAGIC)
                .put(bSrc)
                .putLong(bPktId)
                .putInt(wLen)
                .array();

        return ByteBuffer.allocate(head.length + 2 + messageBytes.length + 2)
                .order(ByteOrder.BIG_ENDIAN)
                .put(head)
                .putShort(calculateCRC(head))
                .put(encryptedBytes)
                .putShort(calculateCRC(messageBytes))
                .array();
    }

    @Override
    public String toString() {
        return "Packet{" +
                "bSrc=" + bSrc +
                ", bPktId=" + bPktId +
                ", bMsq=" + Arrays.toString(new Message[]{bMsq}) +
                '}';
    }
}