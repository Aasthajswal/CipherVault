package ciphervault;

import ciphervault.cipher.Cipher;
import ciphervault.cipher.CipherFactory;
import ciphervault.exception.*;
import ciphervault.model.Credential;
import ciphervault.storage.FileStorage;

import java.util.*;

/**
 * VaultManager — the brain of CipherVault.
 *
 * This class is the SERVICE LAYER — it coordinates between:
 *   - The cipher layer  (encryption/decryption)
 *   - The model layer   (Credential objects)
 *   - The storage layer (FileStorage)
 *
 * The UI talks to VaultManager. VaultManager never talks to the UI.
 * That's "Separation of Concerns" — a key design principle.
 *
 * Demonstrates:
 *   - HashMap for O(1) credential lookup by ID
 *   - ArrayList for ordered iteration
 *   - Master key validation
 *   - Full CRUD operations (Create, Read, Update, Delete)
 *   - Search with case-insensitive matching
 *   - Sorted views using Comparator
 */
public class VaultManager {

    // HashMap: key = credential ID, value = Credential object
    // WHY HashMap? Lookup by ID is O(1) — instant, no loop needed
    private final Map<String, Credential> vault = new HashMap<>();

    private final FileStorage storage;
    private int masterKey;   // the encryption key for this session

    public VaultManager(String storagePath, int masterKey)
            throws InvalidMasterKeyException, StorageException {
        validateKey(masterKey);
        this.masterKey = masterKey;
        this.storage   = new FileStorage(storagePath);
        loadFromDisk();
    }

    // ── VALIDATION ────────────────────────────────────────────────────────

    private void validateKey(int key) throws InvalidMasterKeyException {
        if (key < 1 || key > 94) {
            throw new InvalidMasterKeyException();
        }
    }

    // ── CRUD OPERATIONS ───────────────────────────────────────────────────

    /**
     * ADD a new credential.
     * The password is encrypted BEFORE being stored — never plain text.
     */
    public Credential addCredential(String platform, String username,
                                    String plainPassword, String cipherType)
            throws VaultException {

        if (platform == null || platform.trim().isEmpty())
            throw new VaultException("INVALID_INPUT", "Platform cannot be empty.");
        if (username == null || username.trim().isEmpty())
            throw new VaultException("INVALID_INPUT", "Username cannot be empty.");
        if (plainPassword == null || plainPassword.isEmpty())
            throw new VaultException("INVALID_INPUT", "Password cannot be empty.");

        Cipher cipher = CipherFactory.getCipher(cipherType);
        String encrypted = cipher.encrypt(plainPassword, masterKey);

        Credential cred = new Credential(
            platform.trim(), username.trim(), encrypted, cipher.getCipherName()
        );

        vault.put(cred.getId(), cred);
        saveToDisk();
        return cred;
    }

    /**
     * GET and DECRYPT a credential's password.
     * Decrypts on-the-fly — encrypted form stays in storage.
     */
    public String getDecryptedPassword(String credentialId)
            throws CredentialNotFoundException {

        Credential cred = findById(credentialId);
        Cipher cipher = CipherFactory.getCipher(cred.getCipherType());
        return cipher.decrypt(cred.getEncryptedPassword(), masterKey);
    }

    /**
     * UPDATE a credential's password.
     */
    public void updatePassword(String credentialId, String newPlainPassword)
            throws VaultException {

        if (newPlainPassword == null || newPlainPassword.isEmpty())
            throw new VaultException("INVALID_INPUT", "New password cannot be empty.");

        Credential cred = findById(credentialId);
        Cipher cipher = CipherFactory.getCipher(cred.getCipherType());
        cred.setEncryptedPassword(cipher.encrypt(newPlainPassword, masterKey));
        saveToDisk();
    }

    /**
     * DELETE a credential by ID.
     */
    public void deleteCredential(String credentialId)
            throws CredentialNotFoundException, StorageException {
        if (!vault.containsKey(credentialId)) {
            throw new CredentialNotFoundException(credentialId);
        }
        vault.remove(credentialId);
        saveToDisk();
    }

    // ── SEARCH & LIST ─────────────────────────────────────────────────────

    /**
     * Search credentials by platform name (case-insensitive, partial match).
     * Uses ArrayList + enhanced for-loop — demonstrates iteration over Collection.
     */
    public List<Credential> searchByPlatform(String query) {
        List<Credential> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Credential c : vault.values()) {
            if (c.getPlatform().toLowerCase().contains(lowerQuery)) {
                results.add(c);
            }
        }
        // Sort results alphabetically by platform name
        results.sort(Comparator.comparing(Credential::getPlatform));
        return results;
    }

    /**
     * Get all credentials sorted alphabetically by platform.
     * Returns a copy — caller cannot modify the internal vault map.
     */
    public List<Credential> getAllSorted() {
        List<Credential> list = new ArrayList<>(vault.values());
        list.sort(Comparator.comparing(Credential::getPlatform)
                            .thenComparing(Credential::getUsername));
        return list;
    }

    /**
     * Find by exact ID — throws CredentialNotFoundException if not found.
     */
    public Credential findById(String id) throws CredentialNotFoundException {
        Credential c = vault.get(id);  // O(1) HashMap lookup
        if (c == null) throw new CredentialNotFoundException(id);
        return c;
    }

    // ── VAULT STATS ───────────────────────────────────────────────────────

    public int getTotalCredentials() { return vault.size(); }

    /**
     * Returns breakdown of credentials by cipher type.
     * Demonstrates: HashMap aggregation pattern.
     */
    public Map<String, Integer> getCipherStats() {
        Map<String, Integer> stats = new HashMap<>();
        for (Credential c : vault.values()) {
            stats.merge(c.getCipherType(), 1, Integer::sum);
        }
        return stats;
    }

    // ── MASTER KEY CHANGE ─────────────────────────────────────────────────

    /**
     * Re-encrypts ALL credentials with a new master key.
     * Decrypts with old key, encrypts with new key — atomically.
     *
     * Interview line: "I implemented re-keying: all existing passwords
     * are re-encrypted under the new key in a single operation."
     */
    public void changeMasterKey(int newKey)
            throws InvalidMasterKeyException, StorageException {
        validateKey(newKey);

        for (Credential cred : vault.values()) {
            Cipher cipher = CipherFactory.getCipher(cred.getCipherType());
            // Step 1: decrypt with OLD key
            String plain = cipher.decrypt(cred.getEncryptedPassword(), masterKey);
            // Step 2: re-encrypt with NEW key
            cred.setEncryptedPassword(cipher.encrypt(plain, newKey));
        }

        this.masterKey = newKey;
        saveToDisk();
    }

    // ── INTERNAL ──────────────────────────────────────────────────────────

    private void loadFromDisk() throws StorageException {
        List<Credential> loaded = storage.loadAll();
        vault.clear();
        for (Credential c : loaded) {
            vault.put(c.getId(), c);
        }
    }

    private void saveToDisk() throws StorageException {
        storage.saveAll(new ArrayList<>(vault.values()));
    }
}
