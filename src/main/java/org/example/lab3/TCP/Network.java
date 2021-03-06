package org.example.lab3.TCP;

import org.example.lab1.Decryption;
import org.example.lab3.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;


public class Network {

    private Socket       socket;
    private InputStream  inputStream;
    private OutputStream outputStream;

    private int maxTimeout;

    private Semaphore outputStreamLock = new Semaphore(1);
    private Semaphore inputStreamLock  = new Semaphore(1);


    public Network(Socket socket, int maxTimeout) throws IOException {
        if(maxTimeout < 100){
            throw new IllegalArgumentException("timeout can't be < 100");
        }
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        this.maxTimeout = maxTimeout;
    }


    /**
     * @return {@code packetBytes} if packet received successfully or {@code null} if the maxTimeout expires
     * @throws IOException      if some I/O error occurs.
     */
    public byte[] receive() throws IOException {
        try {
            inputStreamLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            ArrayList<Byte>     receivedBytes = new ArrayList<>(Packet.HEADER_LENGTH * 3);
            LinkedList<Integer> bMagicIndexes = new LinkedList<>();

            int    wLen    = 0;
            byte[] oneByte = new byte[1];

            byte[] packetBytes;

            long startingTime = System.currentTimeMillis();

            while (true) {
                if (inputStream.available() == 0) {
                    long elapsedTime = System.currentTimeMillis() - startingTime;
                    if (elapsedTime > maxTimeout) {
                        return null;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // task interrupted
                        return null;
                    }
                    continue;
                }

                inputStream.read(oneByte);

                if (Packet.B_MAGIC.equals(oneByte[0]) && receivedBytes.size() > 0) {
                    bMagicIndexes.add(receivedBytes.size());
                }
                receivedBytes.add(oneByte[0]);

                //check message if no errors in header
                if (receivedBytes.size() == Packet.HEADER_LENGTH + wLen + Packet.CRC16_LENGTH) {

                    ByteBuffer buffer = (ByteBuffer) ByteBuffer.allocate(2).put(receivedBytes.get(receivedBytes.size() - 2))
                            .put(receivedBytes.get(receivedBytes.size() - 1)).rewind();
                    final short wCrc16_2= buffer.getShort();

                    packetBytes = toPrimitiveByteArr(receivedBytes.toArray(new Byte[0]));

                    byte[] decryptedBytes = Decryption.decrypt(
                            Arrays.copyOfRange(packetBytes, Packet.HEADER_LENGTH, receivedBytes.size() - 2)
                    );

                    final short crc2Evaluated = CRC16.calculateCRC(decryptedBytes);

                    if (wCrc16_2 == crc2Evaluated) {
                        receivedBytes.clear();
                        bMagicIndexes.clear();
                        return packetBytes;

                    } else {
                        wLen = 0;
                        receivedBytes = resetToFirstBMagic(receivedBytes, bMagicIndexes);
                    }

                    //check header
                } else if (receivedBytes.size() >= Packet.HEADER_LENGTH) {

                    ByteBuffer buffer = (ByteBuffer) ByteBuffer.allocate(2).put(receivedBytes.get(Packet.HEADER_LENGTH - 2))
                            .put(receivedBytes.get(Packet.HEADER_LENGTH - 1)).rewind();
                    final short wCrc16_1 =buffer.getShort();

                    final short crc1Evaluated =
                            CRC16.evaluateCrc(toPrimitiveByteArr(receivedBytes.toArray(new Byte[0])), 0, 14);

                    if (wCrc16_1 == crc1Evaluated) {

                        ByteBuffer buffer1 = (ByteBuffer) ByteBuffer.allocate(4).put(receivedBytes.get(10)).put(receivedBytes.get(11))
                                .put(receivedBytes.get(12)).put(receivedBytes.get(13)).rewind();
                        wLen =buffer1.getInt();

                    } else {
                        receivedBytes = resetToFirstBMagic(receivedBytes, bMagicIndexes);
                    }
                }
            }
        } finally {
            inputStreamLock.release();
        }
    }


    public void send(byte[] msg) throws IOException {
        try {
            outputStreamLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        outputStream.write(msg);

        outputStream.flush();

        outputStreamLock.release();
    }

    public void shutdown() {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private ArrayList<Byte> resetToFirstBMagic(ArrayList<Byte> receivedBytes, LinkedList<Integer> bMagicIndexes) {
        //todo notify client???

        //reset to first bMagic if exists
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
