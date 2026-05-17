package ciphervault.ui;

import ciphervault.VaultManager;
import ciphervault.cipher.CipherFactory;
import ciphervault.exception.*;
import ciphervault.model.Credential;

import java.util.*;

/**
 * ConsoleUI — the user interface layer of CipherVault.
 *
 * Responsibilities:
 *   - Display menus and prompts
 *   - Read user input via Scanner
 *   - Call VaultManager methods
 *   - Display results and errors
 *
 * This class ONLY handles display/input. Zero business logic here.
 * That's MVC-style separation — even without a framework.
 */
public class ConsoleUI {

    private final Scanner scanner = new Scanner(System.in);
    private VaultManager manager;

    // ANSI color codes for terminal styling
    private static final String RESET  = "\u001B[0m";
    private static final String GREEN  = "\u001B[32m";
    private static final String CYAN   = "\u001B[36m";
    private static final String RED    = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BOLD   = "\u001B[1m";

    // ── ENTRY POINT ───────────────────────────────────────────────────────

    public void start() {
        printBanner();
        initializeVault();
        runMainLoop();
    }

    // ── INITIALIZATION ────────────────────────────────────────────────────

    private void initializeVault() {
        System.out.println(CYAN + "  Enter your master key (1–94): " + RESET);
        int key = readInt("  > ", 1, 94);

        try {
            manager = new VaultManager("vault.dat", key);
            int count = manager.getTotalCredentials();
            success("  Vault unlocked. " + count + " credential(s) loaded.");
        } catch (InvalidMasterKeyException e) {
            error("  " + e.getMessage());
            initializeVault();  // retry on invalid key
        } catch (StorageException e) {
            error("  Storage error: " + e.getMessage());
            System.exit(1);
        }
    }

    // ── MAIN MENU LOOP ────────────────────────────────────────────────────

    private void runMainLoop() {
        while (true) {
            printMainMenu();
            int choice = readInt("  Choose: ", 0, 7);

            switch (choice) {
                case 1: addCredential();         break;
                case 2: viewAllCredentials();    break;
                case 3: searchCredentials();     break;
                case 4: revealPassword();        break;
                case 5: updatePassword();        break;
                case 6: deleteCredential();      break;
                case 7: changeMasterKey();       break;
                case 0:
                    System.out.println(CYAN + "\n  Vault locked. Goodbye.\n" + RESET);
                    return;
            }
        }
    }

    // ── MENU ACTIONS ─────────────────────────────────────────────────────

    private void addCredential() {
        println(BOLD + "\n  ── Add Credential ──" + RESET);

        System.out.print("  Platform (e.g., Gmail): ");
        String platform = scanner.nextLine().trim();

        System.out.print("  Username / Email: ");
        String username = scanner.nextLine().trim();

        System.out.print("  Password: ");
        String password = scanner.nextLine();

        println("  Cipher — [1] Caesar  [2] XOR  (default: Caesar)");
        System.out.print("  Choice: ");
        String cipherInput = scanner.nextLine().trim();
        String cipherType = cipherInput.equals("2") ? CipherFactory.XOR : CipherFactory.CAESAR;

        try {
            Credential c = manager.addCredential(platform, username, password, cipherType);
            success("  Saved! Credential ID: " + BOLD + c.getId() + RESET);
            println(CYAN + "  (Keep this ID to retrieve or update this entry)" + RESET);
        } catch (VaultException e) {
            error("  " + e);
        }
    }

    private void viewAllCredentials() {
        List<Credential> all = manager.getAllSorted();
        if (all.isEmpty()) {
            warn("  No credentials stored yet.");
            return;
        }
        println(BOLD + "\n  ── All Credentials (" + all.size() + ") ──" + RESET);
        for (Credential c : all) {
            println(CYAN + "\n" + c + RESET);
            divider();
        }
        printStats();
    }

    private void searchCredentials() {
        System.out.print("\n  Search platform: ");
        String query = scanner.nextLine().trim();
        List<Credential> results = manager.searchByPlatform(query);

        if (results.isEmpty()) {
            warn("  No matches for '" + query + "'");
            return;
        }
        println(GREEN + "  Found " + results.size() + " result(s):" + RESET);
        for (Credential c : results) {
            println(CYAN + "\n" + c + RESET);
            divider();
        }
    }

    private void revealPassword() {
        System.out.print("\n  Enter Credential ID: ");
        String id = scanner.nextLine().trim();
        try {
            String plain = manager.getDecryptedPassword(id);
            Credential c = manager.findById(id);
            println(GREEN + "\n  Platform : " + c.getPlatform() + RESET);
            println(GREEN + "  Username : " + c.getUsername() + RESET);
            println(BOLD + GREEN + "  Password : " + plain + RESET);
        } catch (CredentialNotFoundException e) {
            error("  " + e);
        }
    }

    private void updatePassword() {
        System.out.print("\n  Enter Credential ID to update: ");
        String id = scanner.nextLine().trim();
        System.out.print("  New password: ");
        String newPass = scanner.nextLine();
        try {
            manager.updatePassword(id, newPass);
            success("  Password updated and re-encrypted.");
        } catch (VaultException e) {
            error("  " + e);
        }
    }

    private void deleteCredential() {
        System.out.print("\n  Enter Credential ID to delete: ");
        String id = scanner.nextLine().trim();
        System.out.print("  Confirm delete? (yes/no): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("yes")) {
            warn("  Deletion cancelled.");
            return;
        }
        try {
            manager.deleteCredential(id);
            success("  Credential deleted.");
        } catch (VaultException e) {
            error("  " + e);
        }
    }

    private void changeMasterKey() {
        println(YELLOW + "\n  WARNING: All passwords will be re-encrypted with the new key." + RESET);
        println("  You must remember the new key to access your vault.");
        System.out.print("  New master key (1–94): ");
        int newKey = readInt("  > ", 1, 94);
        try {
            manager.changeMasterKey(newKey);
            success("  Master key changed. All " + manager.getTotalCredentials()
                  + " credential(s) re-encrypted.");
        } catch (Exception e) {
            error("  " + e.getMessage());
        }
    }

    // ── UI HELPERS ────────────────────────────────────────────────────────

    private void printBanner() {
        println(CYAN + BOLD);
        println("  ██████╗██╗██████╗ ██╗  ██╗███████╗██████╗");
        println(" ██╔════╝██║██╔══██╗██║  ██║██╔════╝██╔══██╗");
        println(" ██║     ██║██████╔╝███████║█████╗  ██████╔╝");
        println(" ██║     ██║██╔═══╝ ██╔══██║██╔══╝  ██╔══██╗");
        println(" ╚██████╗██║██║     ██║  ██║███████╗██║  ██║");
        println("  ╚═════╝╚═╝╚═╝     ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝");
        println("              V A U L T  " + RESET);
        println(CYAN + "       Encrypted Password Manager " + RESET);
        divider();
    }

    private void printMainMenu() {
        println(BOLD + "\n  ── Menu ───────────────────────────────" + RESET);
        println("  [1] Add credential");
        println("  [2] View all credentials");
        println("  [3] Search by platform");
        println("  [4] Reveal password");
        println("  [5] Update password");
        println("  [6] Delete credential");
        println("  [7] Change master key");
        println("  [0] Exit & lock vault");
    }

    private void printStats() {
        Map<String, Integer> stats = manager.getCipherStats();
        println(YELLOW + "\n  Vault stats — Total: " + manager.getTotalCredentials() + RESET);
        for (Map.Entry<String, Integer> e : stats.entrySet()) {
            println(YELLOW + "    " + e.getKey() + ": " + e.getValue() + RESET);
        }
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(scanner.nextLine().trim());
                if (val >= min && val <= max) return val;
                warn("  Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                warn("  Invalid input. Enter a number.");
            }
        }
    }

    private void println(String s) { System.out.println(s); }
    private void divider()         { System.out.println("  " + "─".repeat(42)); }
    private void success(String s) { System.out.println(GREEN  + s + RESET); }
    private void error(String s)   { System.out.println(RED    + s + RESET); }
    private void warn(String s)    { System.out.println(YELLOW + s + RESET); }
}
