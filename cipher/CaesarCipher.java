package ciphervault.cipher;

/**
 * CaesarCipher — shifts every character by 'key' positions in ASCII.
 *
 * How it works:
 *   encrypt("hello", 3) → "khoor"
 *   decrypt("khoor", 3) → "hello"
 *
 * Implements the Cipher interface — polymorphism in action.
 */
public class CaesarCipher implements Cipher {

    @Override
    public String encrypt(String plainText, int key) {
        if (plainText == null || plainText.isEmpty()) return plainText;

        StringBuilder result = new StringBuilder();
        for (char ch : plainText.toCharArray()) {
            // Shift printable ASCII characters (32–126) only
            if (ch >= 32 && ch <= 126) {
                result.append((char) (32 + (ch - 32 + key) % 95));
            } else {
                result.append(ch);  // non-printable chars stay as-is
            }
        }
        return result.toString();
    }

    @Override
    public String decrypt(String cipherText, int key) {
        if (cipherText == null || cipherText.isEmpty()) return cipherText;

        StringBuilder result = new StringBuilder();
        for (char ch : cipherText.toCharArray()) {
            if (ch >= 32 && ch <= 126) {
                // Add 95 before subtracting to avoid negative modulo
                result.append((char) (32 + (ch - 32 - key % 95 + 95) % 95));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    @Override
    public String getCipherName() {
        return "CaesarCipher";
    }
}
