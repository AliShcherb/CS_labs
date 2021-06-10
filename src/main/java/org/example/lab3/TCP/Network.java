package org.example.lab3.TCP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import org.example.lab3.CRC16;
import org.example.lab3.Decryption;
import org.example.lab3.Packet;

import static org.example.lab3.Packet.CRC16_LENGTH;
import static org.example.lab3.Packet.HEADER_LENGTH;


public class Network {

    private Socket       socket;
    private InputStream in;
    private OutputStream out;
    public final static Integer SUPER_ZERO = 0;
    public final static Integer MESSAGE_LENGTH = HEADER_LENGTH - CRC16_LENGTH;
    private int maxTimeout;

    private Semaphore outLock = new Semaphore(1);
    private Semaphore inLock = new Semaphore(1);


    public Network(Socket socket, int maxTimeout) throws IOException {
        if(maxTimeout < 100){
            throw new IllegalArgumentException("error");
        }
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();

        this.maxTimeout = maxTimeout;
    }

    public byte[] receive() throws IOException {
        try {
            inLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            ArrayList<Byte>     receivedBytes = new ArrayList<>(HEADER_LENGTH * 3);
            LinkedList<Integer> bMagicIndexes = new LinkedList<>();

            int    wLen    = 0;
            byte[] oneByte = new byte[1];
            byte[] bytes;

            long startingTime = System.currentTimeMillis();

            while (true) {
                if (in.available() == 0) {
                    long elapsedTime = System.currentTimeMillis() - startingTime;
                    if (elapsedTime > maxTimeout) {
                        return null;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return null;
                    }
                    continue;
                }

                in.read(oneByte);

                if (Packet.B_MAGIC.equals(oneByte[0]) && receivedBytes.size() > 0) {
                    bMagicIndexes.add(receivedBytes.size());
                }
                receivedBytes.add(oneByte[0]);

                if (receivedBytes.size() == HEADER_LENGTH + wLen + CRC16_LENGTH) {

                    ByteBuffer buffer = (ByteBuffer) ByteBuffer.allocate(2).put(receivedBytes.get(receivedBytes.size() - 2))
                            .put(receivedBytes.get(receivedBytes.size() - 1)).rewind();

                    int byLength = receivedBytes.toArray(new Byte[0]).length;
                   bytes = new byte[byLength];

                    for (int i = 0; i < byLength; ++i) {
                        bytes[i] = receivedBytes.toArray(new Byte[0])[i];
                    }

                    byte[] decryptedBytes = Decryption.decrypt(
                            Arrays.copyOfRange(bytes, HEADER_LENGTH, receivedBytes.size() - 2)
                    );

                    final short crc2Evaluated = CRC16.calculateCRC(decryptedBytes);
                    if (buffer.getShort() == crc2Evaluated) {
                        receivedBytes.clear();
                        bMagicIndexes.clear();
                        return bytes;

                    } else {
                        wLen = 0;
                        receivedBytes = reset(receivedBytes, bMagicIndexes);
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

                        ByteBuffer buffer1 = (ByteBuffer) ByteBuffer.allocate(4).put(receivedBytes.get(10)).put(receivedBytes.get(11))
                                .put(receivedBytes.get(12)).put(receivedBytes.get(13)).rewind();
                        wLen =buffer1.getInt();

                    } else {
                        receivedBytes = reset(receivedBytes, bMagicIndexes);
                    }
                }
            }
        } finally {
            inLock.release();
        }
    }


    public void send(byte[] msg) throws IOException {
        try {
            outLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        out.write(msg);

        out.flush();

        outLock.release();
    }

    public void shutdown() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private ArrayList<Byte> reset(ArrayList<Byte> receivedBytes, LinkedList<Integer> bMagicIndexes) {
        if (!bMagicIndexes.isEmpty()) {
            int firstMagicByteIndex = bMagicIndexes.poll();

            ArrayList<Byte> res = new ArrayList<>(receivedBytes.size());

            for (int i = firstMagicByteIndex; i < receivedBytes.size(); ++i) {
                res.add(receivedBytes.get(i));
            }
            return res;

        } else {
            receivedBytes.clear();
            return receivedBytes;
        }
    }

    //todo create class Utils
    private byte[] toPrimitiveByteArr(Byte[] objArr) {
        byte[] primitiveArr = new byte[objArr.length];

        for (int i = 0; i < objArr.length; ++i) {
            primitiveArr[i] = objArr[i];
        }

        return primitiveArr;
    }

}
