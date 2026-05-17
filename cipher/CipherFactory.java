package ciphervault.cipher;

/**
 * CipherFactory — Factory Design Pattern.
 *
 * WHY: Instead of calling "new CaesarCipher()" or "new XORCipher()" directly
 * throughout the code, we ask the factory to give us the right cipher.
 *
 * BENEFIT: If we add a new cipher tomorrow (e.g., VigenereCipher),
 * we only change THIS class — nothing else in the codebase needs to change.
 * That's the Open/Closed Principle.
 *
 * Interview line: "I used the Factory Pattern to decouple object creation
 * from business logic, making the code extensible without modification."
 */
public class CipherFactory {

    public static final String CAESAR = "CAESAR";
    public static final String XOR    = "XOR";

    /**
     * Returns a Cipher instance based on the type string.
     * Returns CaesarCipher as default for unknown types.
     */
    public static Cipher getCipher(String type) {
        if (type == null) return new CaesarCipher();

        switch (type.toUpperCase()) {
            case XOR:    return new XORCipher();
            case CAESAR: // fall-through intentional
            default:     return new CaesarCipher();
        }
    }

    // Private constructor — this class should never be instantiated
    private CipherFactory() {}
}
