package org.example.test_lab_1;

import org.example.lab1.Message;

public class TestUtils {

    private TestUtils () {
        throw new UnsupportedOperationException("Object of utility class TestUtils can't be created.");
    }

    public static Message generateTestMessage() {
        String testJsonPayload = "{\"name\": \"Alisher\"}";
        int cType = 15;
        int bUserId = 20;
        return new Message(cType, bUserId, testJsonPayload);
    }
}
