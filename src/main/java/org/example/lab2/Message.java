package org.example.lab2;


import lombok.Data;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Data
public class Message {

    public enum cTypes {
        GET_QUANTITY_OF_PRODUCTS_IN_STOCK,
        DELETE_QUANTITY_OF_PRODUCTS_IN_STOCK,
        ADD_QUANTITY_OF_PRODUCTS_IN_STOCK,
        ADD_GROUP_OF_PRODUCTS,
        ADD_PRODUCT_NAME_TO_GROUP,
        SET_PRICE_OF_PRODUCT,
        EXCEPTIONS,
        STANDART_ANSWER
    }

    Integer cType;
    Integer bUserId;
    public String payload;
    private byte[] messBytes;
    public static final int BYTES_WITHOUT_MESSAGE = Integer.BYTES + Integer.BYTES;
    public int FULL_LENGTH() {return messBytes.length + BYTES_WITHOUT_MESSAGE; }
    public Message() { }

    public Message(cTypes cType, Integer bUserId, String message){
        this.cType = cType.ordinal();
        this.bUserId = bUserId;
        this.payload = message;
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        messBytes = Cryptor.encrypt(payloadBytes);
    }


    public byte[] toBytes() {
        return ByteBuffer.allocate(FULL_LENGTH())
                .putInt(cType)
                .putInt(bUserId)
                .put(messBytes)
                .array();
    }


    public void decode(){

        byte[] decryptedMessage = Cryptor.decrypt(messBytes);
        payload = new String(decryptedMessage);
    }


}