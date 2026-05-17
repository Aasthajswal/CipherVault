package ciphervault.cipher;

import java.util.Base64;

/**
 * XORCipher — uses bitwise XOR operation to encrypt.
 *
 * How it works:
 *   Every character's ASCII value is XOR-ed with the key.
 *   XOR is self-inverse: encrypt(encrypt(text, key), key) == text
 *   So the same method works for both encrypt and decrypt!
 *
 * Result is Base64-encoded so it's safely storable as a String.
 */
public class XORCipher implements Cipher {

    @Override
    public String encrypt(String plainText, int key) {
        if (plainText == null || plainText.isEmpty()) return plainText;

        byte[] bytes = plainText.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (bytes[i] ^ key);   // XOR each byte with key
        }
        // Encode to Base64 so the result is printable/storable
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public String decrypt(String cipherText, int key) {
        if (cipherText == null || cipherText.isEmpty()) return cipherText;

        byte[] bytes = Base64.getDecoder().decode(cipherText);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (bytes[i] ^ key);   // XOR again = original
        }
        return new String(bytes);
    }

    @Override
    public String getCipherName() {
        return "XORCipher";
    }
}
