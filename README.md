# CipherVault — Encrypted Password Manager
### Pure Java Project | Cognizant Portfolio

A command-line password manager written in **100% Core Java** (no frameworks, no external libraries).  
Stores credentials encrypted using Caesar and XOR ciphers, persisted to a local file.

---

## Project Structure

```
ciphervault/
├── cipher/
│   ├── Cipher.java            ← Interface (contract for all ciphers)
│   ├── CaesarCipher.java      ← Caesar shift encryption
│   ├── XORCipher.java         ← XOR bitwise encryption
│   └── CipherFactory.java     ← Factory Design Pattern
├── model/
│   └── Credential.java        ← Data model with serialization
├── exception/
│   ├── VaultException.java              ← Base custom exception
│   ├── CredentialNotFoundException.java ← Specific exception
│   ├── InvalidMasterKeyException.java   ← Specific exception
│   └── StorageException.java            ← Specific exception
├── storage/
│   └── FileStorage.java       ← File I/O layer
├── ui/
│   └── ConsoleUI.java         ← Interactive menu (Scanner-based)
├── VaultManager.java          ← Core business logic / service layer
└── Main.java                  ← Entry point
```

---

## Java Concepts Used

| Concept | Where Applied |
|---|---|
| Interface | `Cipher.java` — defines contract |
| Polymorphism | `CaesarCipher` and `XORCipher` both implement `Cipher` |
| Encapsulation | `Credential.java` — all fields private, getters/setters |
| Inheritance | Custom exceptions extend `VaultException` |
| Factory Pattern | `CipherFactory.getCipher(type)` |
| HashMap | `VaultManager` — O(1) credential lookup |
| ArrayList + sort | `getAllSorted()`, `searchByPlatform()` |
| Comparator | Sorting credentials by platform + username |
| File I/O | `FileStorage` — BufferedReader/Writer |
| try-with-resources | Auto-close streams in FileStorage |
| Custom Exceptions | `CredentialNotFoundException`, `StorageException`, `InvalidMasterKeyException` |
| Constructor overloading | `Credential.java` — 2 constructors |
| Static methods | `CipherFactory`, `Credential.fromFileString()` |
| `final` fields | Immutable `id` and `createdAt` in Credential |

---

## How to Run

```bash
# Compile
javac -d out src/ciphervault/*.java src/ciphervault/**/*.java

# Run
java -cp out ciphervault.Main
```

---

## Features

- Add credentials (platform + username + password)
- Choose encryption: Caesar Cipher or XOR Cipher
- Reveal decrypted password on demand
- Update password (auto re-encrypts)
- Delete credential
- Search by platform name (partial, case-insensitive)
- Change master key (re-encrypts all stored passwords)
- Persistent storage — data survives between sessions
- Full audit via custom exceptions with error codes
