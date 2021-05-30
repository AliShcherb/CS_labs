package org.example.lab2;



import java.nio.ByteBuffer;

import lombok.Data;

@Data
public class Message {

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


    public Message(cTypes cType, Integer bUserId, String message) {
        this.cType = cType.ordinal();
        this.bUserId = bUserId;
        this.payload = message;
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