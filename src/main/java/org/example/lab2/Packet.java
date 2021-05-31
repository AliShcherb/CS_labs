package org.example.lab2;

import lombok.Data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@Data
public class Packet {

    public final static Byte B_MAGIC = 0x13;
    public final static Integer HEADER_LENGTH = 16;
    public final static Integer CRC16_LENGTH = 2;

    Long bPktId;
    Byte bSrc;
    Integer wLen;
    Short crcHead;
    public Message bMsq;
    Short crcMessage;


    public final static Integer headWithoutWLen = B_MAGIC.BYTES + Byte.BYTES + Long.BYTES;
    public final static Integer headLength = headWithoutWLen + Integer.BYTES;
    public final static Integer packetHeadAndCRC = headLength + Short.BYTES;


    public Packet(Byte bSrc, Long bPktId, Message bMsq) {
        this.bSrc = bSrc;
        this.bPktId = bPktId;
        this.bMsq = bMsq;
        wLen = bMsq.toBytes().length;
        ;
    }


    public Packet(byte[] encodedPacket) {

        ByteBuffer buffer = ByteBuffer.wrap(encodedPacket).order(ByteOrder.BIG_ENDIAN);

        if (buffer.get() != B_MAGIC)
            throw new IllegalArgumentException("Unexpected bMagic");

        //final byte clientId = buffer.get();
        // System.out.println("Client ID: " + clientId);

        // long packetId = buffer.getLong();
        //System.out.println("Packet ID: " + packetId);

        // int mLength = buffer.getInt();
        // System.out.println("Length:" + mLength);

        //short crcHead = buffer.getShort();
        //System.out.println("CRC16 of header:" + Integer.toBinaryString(0xFFFF & crcHead));

        /*byte[] head = ByteBuffer.allocate(14)
                .order(ByteOrder.BIG_ENDIAN)
                .put(B_MAGIC)
                .put(buffer.get())
                .putLong(buffer.getLong())
                .putInt(buffer.getInt())
                .array();


*/
        bSrc = buffer.get();
        bPktId = buffer.getLong();
        wLen = buffer.getInt();
        crcHead = buffer.getShort();

        final Integer messageBodyLength = wLen - 8;
       /* if (calculateCRC(head) != buffer.getShort()) {
            throw new IllegalArgumentException("Head CRC16");
        }*/


        bMsq = new Message();
        bMsq.setCType(buffer.getInt());
        bMsq.setBUserId(buffer.getInt());

        byte[] messageBody = new byte[messageBodyLength];
        buffer.get(messageBody);

        crcMessage = buffer.getShort();

        bMsq.setMessBytes(messageBody);
        bMsq.decode();

        //short crcMessage = buffer.getShort();
         /*if (calculateCRC(bMsq.toBytes()) != crcMessage) {
            throw new IllegalArgumentException("Message CRC16");
        }*/
    }


    public byte[] toBytes() {

        Message message = getBMsq();

        byte[] head = ByteBuffer.allocate(headLength)
                .put(B_MAGIC)
                .put(bSrc)
                .putLong(bPktId)
                .putInt(wLen)
                .array();


        Integer messageLength = message.toBytes().length;
        byte[] body = ByteBuffer.allocate(messageLength)
                .put(message.toBytes())
                .array();

        crcMessage = CRC16.calculateCRC(body);
        crcHead = CRC16.calculateCRC(head);

        Integer packetLength = headLength + +messageLength + crcHead.BYTES + crcMessage.BYTES;
        return ByteBuffer.allocate(packetLength)
                .put(head)
                .putShort(CRC16.calculateCRC(head))
                .put(body)
                .putShort(CRC16.calculateCRC(body))
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
