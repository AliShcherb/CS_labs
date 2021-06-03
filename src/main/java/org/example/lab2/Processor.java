package org.example.lab2;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;


public class Processor {


    public static Packet process(Packet inputPacket)  {

        Message message = inputPacket.getBMsq();
        Message answer=new Message(Message.cTypes.STANDART_ANSWER, 0, message.getPayload() + " OK!");

        //GET_QUANTITY_OF_PRODUCTS_IN_STOCK
        if (message.getCType() == Message.cTypes.GET_QUANTITY_OF_PRODUCTS_IN_STOCK.ordinal()) {
//answer = new Message(Message.cTypes.DELETE_QUANTITY_OF_PRODUCTS_IN_STOCK, 0, message.getPayload() + "GET_QUANTITY_OF_PRODUCTS_IN_STOCK");

            //DELETE_QUANTITY_OF_PRODUCTS_IN_STOCK
        } else if (message.getCType() == Message.cTypes.DELETE_QUANTITY_OF_PRODUCTS_IN_STOCK.ordinal()) {
            //answer = new Message(Message.cTypes.DELETE_QUANTITY_OF_PRODUCTS_IN_STOCK, 0, message.getPayload() + "DELETE_QUANTITY_OF_PRODUCTS_IN_STOCK");

            //ADD_QUANTITY_OF_PRODUCTS_IN_STOCK
        } else if (message.getCType() == Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK.ordinal()) {
           // answer = new Message(Message.cTypes.ADD_QUANTITY_OF_PRODUCTS_IN_STOCK, 0, message.getPayload() + "ADD_QUANTITY_OF_PRODUCTS_IN_STOCK");

            //ADD_GROUP_OF_PRODUCTS
        } else if (message.getCType() == Message.cTypes.ADD_GROUP_OF_PRODUCTS.ordinal()) {
           // answer = new Message(Message.cTypes.ADD_GROUP_OF_PRODUCTS, 0, message.getPayload() + "ADD_GROUP_OF_PRODUCTS");

            //ADD_PRODUCT_NAME_TO_GROUP
        } else if (message.getCType() == Message.cTypes.ADD_PRODUCT_NAME_TO_GROUP.ordinal()) {
           // answer = new Message(Message.cTypes.ADD_PRODUCT_NAME_TO_GROUP, 0, message.getPayload() + "ADD_PRODUCT_NAME_TO_GROUP");
//
            //SET_PRICE_OF_PRODUCT
        } else if (message.getCType() == Message.cTypes.SET_PRICE_OF_PRODUCT.ordinal()) {
           // answer = new Message(Message.cTypes.SET_PRICE_OF_PRODUCT, 0, message.getPayload() + "SET_PRICE_OF_PRODUCT");

            //STANDART_ANSWER
        } else {
            answer = new Message(Message.cTypes.STANDART_ANSWER, 0, message.getPayload() + "JUST OK");
        }

        Packet answerPacket = new Packet(inputPacket.bSrc, inputPacket.bPktId, answer);
        return answerPacket;
    }


}
