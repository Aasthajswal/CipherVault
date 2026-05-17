package ciphervault.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Credential — the core data model of CipherVault.
 *
 * Represents a saved username+password for a specific platform/site.
 * Demonstrates:
 *   - Encapsulation: all fields are private, accessed via getters/setters
 *   - Constructor overloading
 *   - toString() override for display
 *   - Immutable ID (set once via constructor, no setter)
 */
public class Credential {

    private final String id;           // unique ID — final = immutable after creation
    private String platform;           // e.g., "Gmail", "Instagram"
    private String username;
    private String encryptedPassword;  // stored encrypted — NEVER plain text
    private String cipherType;         // which cipher was used: CAESAR or XOR
    private final String createdAt;    // timestamp — final = set once
    private String lastModified;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    // Full constructor — used when loading from file
    public Credential(String id, String platform, String username,
                      String encryptedPassword, String cipherType,
                      String createdAt, String lastModified) {
        this.id                = id;
        this.platform          = platform;
        this.username          = username;
        this.encryptedPassword = encryptedPassword;
        this.cipherType        = cipherType;
        this.createdAt         = createdAt;
        this.lastModified      = lastModified;
    }

    // Constructor for NEW credentials — auto-generates ID and timestamps
    public Credential(String platform, String username,
                      String encryptedPassword, String cipherType) {
        this.id                = generateId(platform);
        this.platform          = platform;
        this.username          = username;
        this.encryptedPassword = encryptedPassword;
        this.cipherType        = cipherType;
        this.createdAt         = LocalDateTime.now().format(FMT);
        this.lastModified      = this.createdAt;
    }

    // ID = first 4 chars of platform + timestamp millis (ensures uniqueness)
    private String generateId(String platform) {
        String prefix = platform.length() >= 4
            ? platform.substring(0, 4).toUpperCase()
            : platform.toUpperCase();
        return prefix + "-" + System.currentTimeMillis() % 100000;
    }

    // ── Getters ──────────────────────────────────────────────
    public String getId()                { return id; }
    public String getPlatform()          { return platform; }
    public String getUsername()          { return username; }
    public String getEncryptedPassword() { return encryptedPassword; }
    public String getCipherType()        { return cipherType; }
    public String getCreatedAt()         { return createdAt; }
    public String getLastModified()      { return lastModified; }

    // ── Setters (only mutable fields) ────────────────────────
    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
        this.lastModified = LocalDateTime.now().format(FMT);
    }
    public void setUsername(String username) {
        this.username = username;
        this.lastModified = LocalDateTime.now().format(FMT);
    }
    public void setCipherType(String cipherType) { this.cipherType = cipherType; }

    // ── Serialization: converts to pipe-separated line for file storage ──
    public String toFileString() {
        return id + "|" + platform + "|" + username + "|"
             + encryptedPassword + "|" + cipherType + "|"
             + createdAt + "|" + lastModified;
    }

    // ── Deserialize: parses a pipe-separated line back into a Credential ──
    public static Credential fromFileString(String line) {
        String[] parts = line.split("\\|", 7);
        if (parts.length != 7) return null;
        return new Credential(parts[0], parts[1], parts[2],
                              parts[3], parts[4], parts[5], parts[6]);
    }

    @Override
    public String toString() {
        return String.format("  ID       : %s%n  Platform : %s%n  Username : %s%n"
                           + "  Cipher   : %s%n  Created  : %s%n  Modified : %s",
                id, platform, username, cipherType, createdAt, lastModified);
    }
}
