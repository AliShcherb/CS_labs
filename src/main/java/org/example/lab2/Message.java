package org.example.lab2;



import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import lombok.Data;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

@Data
public class Message {
    private byte[] encryptedMessageInBytes;
    public Message() {

    }

    public enum cTypes {
        GET_PRODUCT_AMOUNT,
        GET_PRODUCT,
        ADD_PRODUCT,
        ADD_PRODUCT_GROUP,
        ADD_PRODUCT_TITLE_TO_GROUP,
        SET_PRODUCT_PRICE,
        EXCEPTION_FROM_SERVER,
        OK
    }

    public void setCType(int cType) {
        this.cType = cType;
    }

    private Integer cType;
    private Integer bUserId;
    private String payload;

    public byte[] getEncryptedMessageInBytes() {
        return encryptedMessageInBytes;
    }
    public void setEncryptedMessageInBytes(byte[] encryptedMessageInBytes) {
        this.encryptedMessageInBytes = encryptedMessageInBytes;
    }

    public Message(cTypes cType, Integer bUserId, String message) throws BadPaddingException, IllegalBlockSizeException {
        this.cType = cType.ordinal();
        this.bUserId = bUserId;
        this.payload = message;
        encode();
    }

    public byte[] toBytes() {
        byte[] payloadBytes = payload.getBytes();
        return ByteBuffer.allocate(8 + payloadBytes.length)
                .putInt(cType)
                .putInt(bUserId)
                .put(payloadBytes)
                .array();
    }

    public void encode() throws BadPaddingException, IllegalBlockSizeException {
        byte[] myMes = payload.getBytes(StandardCharsets.UTF_8);
        encryptedMessageInBytes = Cryptor.encryptMessage(myMes);
    }
}
