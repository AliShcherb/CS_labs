package org.example.lab3;


import lombok.Data;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Data
public class Message {
    private byte[] bytes;
    public Message() { }
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

    private Integer cType;
    private Integer bUserId;
    private String payload;

    public Message(cTypes cType, Integer bUserId, String message)  {
        this.cType = cType.ordinal();
        this.bUserId = bUserId;
        this.payload = message;
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        bytes = Encryption.encrypt(payloadBytes);
    }

    public byte[] toBytes() {
        byte[] payloadBytes = payload.getBytes();
        return ByteBuffer.allocate(8 + payloadBytes.length)
                .putInt(cType)
                .putInt(bUserId)
                .put(payloadBytes)
                .array();
    }


}
