package org.example.lab2;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.example.lab2.Packet.HEADER_LENGTH;
import static org.example.lab2.Packet.CRC16_LENGTH;


public class Network {

    private BufferedOutputStream out;
    private InputStream in;
    private int max;
    private TimeUnit unit;
    private int headIndex=10;
    public final static Integer SUPER_ZERO = 0;

    public final static Integer MESSAGE_LENGTH = HEADER_LENGTH- CRC16_LENGTH;

    private ArrayList<Byte>     receivedBytes;

    private LinkedList<Integer> list;

    private Object inLock = new Object();
    private Object outLock = new Object();

    public Network(InputStream in, OutputStream out, int max, TimeUnit unit) {
        this.in = in;
        this.out = new BufferedOutputStream(out);
        this.max = Math.max(max, SUPER_ZERO);
        this.unit = unit;
        receivedBytes = new ArrayList<>(HEADER_LENGTH * 3);
        list = new LinkedList<>();
    }


    public byte[] receive() throws IOException, TimeoutException {
        synchronized (inLock) {
            int    wLen    = SUPER_ZERO;
            byte[] oneByte = new byte[1];
            byte[] packetBytes;
            boolean marker = true;
            long startTime = System.currentTimeMillis();
            long timeoutValue = 30000L;
            while (true) {
                if (in.available() == 0) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    if (!marker && elapsedTime > timeoutValue) {
                        throw new TimeoutException();
                    }
                    marker = false;
                        try {
                            System.out.println("No new data...sleeping!!!");
                            unit.sleep(max);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }



                try {
                    in.read(oneByte);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("NEW DATA!!!");
                marker = true;

                int receivedBytesSize=receivedBytes.size();
                if (Packet.B_MAGIC.equals(oneByte[0]) && receivedBytesSize > 0) {
                    list.add(receivedBytesSize);
                }
                receivedBytes.add(oneByte[0]);


                ////
               // System.out.println("First:"+receivedBytes.toString());
                ////

                if (receivedBytes.size() == HEADER_LENGTH + wLen + Packet.CRC16_LENGTH) {
                    ByteBuffer buffer = (ByteBuffer) ByteBuffer.allocate(2).put(receivedBytes.get(receivedBytes.size()-2))
                            .put(receivedBytes.get(receivedBytes.size()-1)).rewind();

                    int byLength = receivedBytes.toArray(new Byte[0]).length;
                    packetBytes = new byte[byLength];

                    for (int i = 0; i < byLength; ++i) {
                        packetBytes[i] = receivedBytes.toArray(new Byte[0])[i];
                    }

                    byte[] decryptedBytes = Decryption.decrypt(
                            Arrays.copyOfRange(packetBytes, HEADER_LENGTH, receivedBytes.size() - 2)
                    );

                    if (buffer.getShort() == CRC16.calculateCRC(decryptedBytes)) {
                        receivedBytes.clear();
                        list.clear();
                        return packetBytes;

                    } else {
                        wLen = SUPER_ZERO;
                        reset();
                    }


                } else if (receivedBytes.size() >= HEADER_LENGTH) {
                    ByteBuffer buffer = (ByteBuffer) ByteBuffer.allocate(2).put(receivedBytes.get(HEADER_LENGTH - 2))
                            .put(receivedBytes.get(HEADER_LENGTH - 1)).rewind();

                    int byLength = receivedBytes.toArray(new Byte[0]).length;
                    byte[] primitiveArr = new byte[byLength];
                    for (int i = 0; i < byLength; ++i) {
                        primitiveArr[i] = receivedBytes.toArray(new Byte[0])[i];
                    }

                    if (buffer.getShort() == CRC16.calculateCRC(primitiveArr, SUPER_ZERO, MESSAGE_LENGTH)) {
                        ByteBuffer buffer1 = (ByteBuffer) ByteBuffer.allocate(4).put(receivedBytes.get(headIndex)).put(receivedBytes.get(headIndex+1))
                                .put(receivedBytes.get(headIndex+2)).put(receivedBytes.get(headIndex+3)).rewind();
                        wLen =buffer1.getInt();
                    } else {
                        reset();
                        Packet ansPac = new Packet((byte) 0, 1, new Message(Message.cTypes.EXCEPTIONS,0, "Wrong header"));
                        send(ansPac.toBytes());

                    }
                }
            }
        }
    }

    public void send(byte[] msg)  {
        synchronized (outLock) {
            try {
                out.write(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void reset() {

        if (!list.isEmpty()) {
            int firstMagicByteIndex = list.poll();

            ArrayList<Byte> byteArrayList = new ArrayList<>(receivedBytes.size());

            for (int i = firstMagicByteIndex; i < receivedBytes.size(); ++i) {
                byteArrayList.add(receivedBytes.get(i));
            }

            receivedBytes = byteArrayList;

        } else {
            receivedBytes.clear();
        }
    }
}