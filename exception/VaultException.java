package ciphervault.exception;

/**
 * VaultException — base custom exception for CipherVault.
 *
 * WHY custom exceptions?
 *   Using generic Exception everywhere makes debugging hard.
 *   Our own exception hierarchy lets callers catch SPECIFIC problems.
 *
 * Interview line: "I created a custom exception hierarchy so the application
 * fails with meaningful, domain-specific error messages instead of generic ones."
 */
public class VaultException extends Exception {

    private final String errorCode;

    public VaultException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public VaultException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() { return errorCode; }

    @Override
    public String toString() {
        return "[" + errorCode + "] " + getMessage();
    }
}
