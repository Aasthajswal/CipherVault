package ciphervault.cipher;

/**
 * Cipher — interface defining encryption/decryption contract.
 * Any cipher algorithm MUST implement these two methods.
 * This is the "I" in SOLID — Interface Segregation / Dependency Inversion.
 */
public interface Cipher {
    String encrypt(String plainText, int key);
    String decrypt(String cipherText, int key);
    String getCipherName();
}
