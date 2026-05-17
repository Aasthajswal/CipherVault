package ciphervault;

import ciphervault.ui.ConsoleUI;

/**
 * Main — entry point of CipherVault.
 *
 * Keeps main() clean: just creates the UI and starts it.
 * ALL logic lives in VaultManager and ConsoleUI — not here.
 *
 * HOW TO RUN:
 *   Compile:  javac -d out src/ciphervault/**\/*.java src/ciphervault/*.java
 *   Run:      java -cp out ciphervault.Main
 */
public class Main {
    public static void main(String[] args) {
        new ConsoleUI().start();
    }
}
