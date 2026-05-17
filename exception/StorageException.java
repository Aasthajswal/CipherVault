package ciphervault.exception;

/**
 * StorageException — thrown when file read/write operations fail.
 */
public class StorageException extends VaultException {
    public StorageException(String message, Throwable cause) {
        super("STORAGE_ERROR", message, cause);
    }
    public StorageException(String message) {
        super("STORAGE_ERROR", message);
    }
}
