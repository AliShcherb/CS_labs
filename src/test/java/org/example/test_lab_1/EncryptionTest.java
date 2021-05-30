package org.example.test_lab_1;

import org.example.lab1.Decryption;
import org.example.lab1.Encryption;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class EncryptionTest {
    String sources = "Call me by your name";
    byte[] arr = sources.getBytes();

    @Test
    public void testEncryption() {
        byte[] encode = Encryption.encrypt(arr);
        byte[] decode = Decryption.decrypt(encode);
        String s = new String(decode, StandardCharsets.UTF_8);
        assertEquals(s, sources);
    }


}

