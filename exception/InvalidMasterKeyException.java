package ciphervault.exception;

/**
 * InvalidMasterKeyException — thrown when master key validation fails.
 */
public class InvalidMasterKeyException extends VaultException {
    public InvalidMasterKeyException() {
        super("INVALID_KEY",
              "Master key must be a number between 1 and 94.");
    }
    public InvalidMasterKeyException(String detail) {
        super("INVALID_KEY", detail);
    }
}
