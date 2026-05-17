package ciphervault.storage;

import ciphervault.exception.StorageException;
import ciphervault.model.Credential;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileStorage — handles all file read/write operations.
 *
 * Demonstrates:
 *   - File I/O: BufferedReader, BufferedWriter, FileReader, FileWriter
 *   - try-with-resources (auto-closes streams — no memory leaks)
 *   - Separation of concerns: storage logic is isolated here
 *
 * Data is stored in a plain text file, one credential per line,
 * pipe-separated (|). Example line:
 *   GMAI-83421|Gmail|john@gmail.com|eW91cg==|XOR|01-01-2025 10:30|01-01-2025 10:30
 */
public class FileStorage {

    private final String filePath;

    public FileStorage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all credentials from the file.
     * Returns an empty list if the file doesn't exist yet (first run).
     */
    public List<Credential> loadAll() throws StorageException {
        List<Credential> credentials = new ArrayList<>();
        File file = new File(filePath);

        // First run — no file yet, that's fine
        if (!file.exists()) return credentials;

        // try-with-resources: BufferedReader auto-closes when block ends
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue; // skip comments

                Credential c = Credential.fromFileString(line);
                if (c != null) credentials.add(c);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to read vault file: " + filePath, e);
        }

        return credentials;
    }

    /**
     * Saves the entire credential list to file (overwrites existing content).
     * Called after every add/update/delete operation.
     */
    public void saveAll(List<Credential> credentials) throws StorageException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write("# CipherVault Data File — DO NOT EDIT MANUALLY");
            writer.newLine();
            for (Credential c : credentials) {
                writer.write(c.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new StorageException("Failed to write vault file: " + filePath, e);
        }
    }

    public String getFilePath() { return filePath; }
}
