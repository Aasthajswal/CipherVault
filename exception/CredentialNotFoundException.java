package ciphervault.exception;

/**
 * CredentialNotFoundException — thrown when lookup by ID returns nothing.
 * Extends VaultException — this is inheritance applied to exceptions.
 */
public class CredentialNotFoundException extends VaultException {
    public CredentialNotFoundException(String credentialId) {
        super("CRED_NOT_FOUND",
              "No credential found with ID: '" + credentialId + "'");
    }
}
