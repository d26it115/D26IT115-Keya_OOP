
import model.Account;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import util.CommandParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MiniBank {

    private static final Scanner sc = new Scanner(System.in);

    private static final AccountStore accountStore = new AccountStore();

    private static final ConcurrentHashMap<String, Account> sharedAccounts =
            new ConcurrentHashMap<>();

    private static final TransactionProcessor transactionProcessor =
            new TransactionProcessor(sharedAccounts);

    private static final Repository<Account> repository =
            new Repository<>();

    private static BankService bankService;

    private static final Path dataFolder = Path.of("data");
    private static final Path logsFolder = Path.of("logs");

    private static final Path stateFile =
            dataFolder.resolve("accounts.dat");

    private static final Path transactionFile =
            logsFolder.resolve("transactions.log");

    private static final Path reportFile =
            dataFolder.resolve("end-of-day-report.txt");

    public static void main(String[] args) {

        Account.setSynchronizedMode(true);

        loadSavedAccounts();

        // Initialize BankService after loading saved accounts.
        bankService = new BankService(
                repository,
                accountStore,
                transactionProcessor
        );

        int choice;

        do {
            displayMenu();

            choice = readInt();

            try {
                switch (choice) {

                    case 1:
                        openAccount();
                        break;

                    case 2:
                        deposit();
                        break;

                    case 3:
                        withdraw();
                        break;

                    case 4:
                        transfer();
                        break;

                    case 5:
                        accountStatement();
                        break;

                    case 6:
                        displayAllAccounts();
                        break;

                    case 7:
                        processBatchTransactions();
                        break;

                    case 8:
                        generateEndOfDayReport();
                        break;

                    case 9:
                        saveAndReport();
                        System.out.println(
                                "Thank you for using MiniBank."
                        );
                        break;

                    default:
                        System.out.println(
                                "Invalid choice. Please try again."
                        );
                }

            } catch (Exception e) {
                System.out.println(
                        "Operation failed: " + e.getMessage()
                );
            }

        } while (choice != 9);

        transactionProcessor.stop();
    }

    // Display the main menu
    private static void displayMenu() {

        System.out.println("\n================================");
        System.out.println("             MINIBANK");
        System.out.println("================================");
        System.out.println("1. Open Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer");
        System.out.println("5. Account Statement");
        System.out.println("6. Display All Accounts");
        System.out.println("7. Process Batch Transactions");
        System.out.println("8. Generate End-of-Day Report");
        System.out.println("9. Exit");
        System.out.println("================================");
        System.out.print("Enter your choice: ");
    }

    // Load previously saved accounts
    private static void loadSavedAccounts() {

        try {
            if (!Files.exists(stateFile)) {
                System.out.println("No saved accounts found.");
                return;
            }

            Account[] savedAccounts = StatePersister.load(stateFile);

            if (savedAccounts == null) {
                System.out.println("No saved accounts found.");
                return;
            }

            for (Account account : savedAccounts) {
                Account.observeAccountNumber(
                        account.getAccountNumber()
                );
            }

            Set<String> usedNumbers = new HashSet<>();
            boolean repaired = false;

            for (Account account : savedAccounts) {

                String number = account.getAccountNumber();

                if (!usedNumbers.add(number)) {

                    account.assignNewAccountNumber();

                    while (!usedNumbers.add(
                            account.getAccountNumber())) {
                        account.assignNewAccountNumber();
                    }

                    repaired = true;

                    System.out.println(
                            "Duplicate account number repaired for "
                                    + account.getOwnerName()
                                    + ". New number: "
                                    + account.getAccountNumber()
                    );
                }

                accountStore.add(account);
                transactionProcessor.registerAccount(account);
            }

            if (repaired) {
                saveAccounts();

                System.out.println(
                        "Corrected account numbers have been saved."
                );
            }

            System.out.println(
                    accountStore.size() + " saved account(s) loaded."
            );

        } catch (Exception e) {
            System.out.println(
                    "Could not load saved accounts: " + e.getMessage()
            );
        }
    }

    // Open a new bank account
    private static void openAccount()  throws Exception{

        System.out.print("Enter account holder name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.println("\nSelect account type:");
        System.out.println("1. Savings Account");
        System.out.println("2. Current Account");
        System.out.println("3. Fixed Deposit Account");
        System.out.print("Enter choice: ");

        int type = readInt();

        System.out.print("Enter opening balance: Rs. ");
        long openingBalance = readLong();

        if (openingBalance < 0) {
            System.out.println(
                    "Opening balance cannot be negative."
            );
            return;
        }

        String accountType;
        long additionalLimit = 0;

        switch (type) {

            case 1:
                accountType = "SAVINGS";
                break;

            case 2:
                accountType = "CURRENT";

                System.out.print("Enter overdraft limit: Rs. ");
                additionalLimit = readLong();

                if (additionalLimit < 0) {
                    System.out.println(
                            "Invalid overdraft limit."
                    );
                    return;
                }
                break;

            case 3:
                accountType = "FIXED_DEPOSIT";
                break;

            default:
                System.out.println("Invalid account type.");
                return;
        }

        Account account = bankService.openAccount(
                accountType,
                name,
                openingBalance,
                additionalLimit
        );

        System.out.println("\nAccount created successfully.");

        if (account != null) {
            System.out.println(
                    "Account number: " + account.getAccountNumber()
            );
            System.out.println(
                    "Account holder: " + account.getOwnerName()
            );
            System.out.println(
                    "Balance: Rs. " + account.getBalance()
            );
        }

        saveAccounts();
    }

    // Deposit money
    private static void deposit() throws Exception {

        String accountNumber = readAccountNumber();

        if (accountNumber == null) {
            return;
        }

        System.out.print("Enter deposit amount: Rs. ");
        long amount = readLong();

        bankService.deposit(accountNumber, amount);

        recordTransaction(
                "DEPOSIT|" + accountNumber + "|" + amount
        );

        saveAccounts();

        System.out.println("Deposit successful.");
    }

    // Withdraw money
    private static void withdraw() throws Exception {

        String accountNumber = readAccountNumber();

        if (accountNumber == null) {
            return;
        }

        System.out.print("Enter withdrawal amount: Rs. ");
        long amount = readLong();

        try {
            bankService.withdraw(accountNumber, amount);

            recordTransaction(
                    "WITHDRAW|" + accountNumber + "|" + amount
            );

            saveAccounts();

            System.out.println("Withdrawal successful.");

        } catch (InsufficientFundsException e) {

            System.out.println(
                    "Withdrawal failed: " + e.getMessage()
            );

            System.out.println(
                    "Shortfall: Rs. " + e.getShortfall()
            );

        } catch (InvalidAmountException e) {

            System.out.println(
                    "Invalid withdrawal: " + e.getMessage()
            );
        }
    }

    // Transfer money between accounts
    private static void transfer() throws Exception {

        System.out.println("\n--- Source Account ---");

        String fromAccount = readAccountNumber();

        if (fromAccount == null) {
            return;
        }

        System.out.println("\n--- Destination Account ---");

        String toAccount = readAccountNumber();

        if (toAccount == null) {
            return;
        }

        if (fromAccount.equals(toAccount)) {
            System.out.println(
                    "Source and destination must be different accounts."
            );
            return;
        }

        System.out.print("Enter transfer amount: Rs. ");
        long amount = readLong();

        bankService.transfer(
                fromAccount,
                toAccount,
                amount
        );

        recordTransaction(
                "WITHDRAW|" + fromAccount + "|" + amount
        );

        recordTransaction(
                "DEPOSIT|" + toAccount + "|" + amount
        );

        saveAccounts();

        System.out.println("Transfer successful.");
    }

    // Display an account statement
    private static void accountStatement()  throws Exception{

        String accountNumber = readAccountNumber();

        if (accountNumber == null) {
            return;
        }

        System.out.println("\n========== ACCOUNT STATEMENT ==========");

        System.out.println(
                bankService.statement(accountNumber)
        );

        System.out.println("=======================================");
    }

    // Display all accounts
    private static void displayAllAccounts() {

        ArrayList<Account> accounts =
                bankService.listAccounts();

        if (accounts.isEmpty()) {
            System.out.println("No accounts to display.");
            return;
        }

        System.out.println("\n========== ALL BANK ACCOUNTS ==========");

        for (Account account : accounts) {

            System.out.println("---------------------------------------");
            System.out.println(
                    "Account Number : " + account.getAccountNumber()
            );
            System.out.println(
                    "Account Holder : " + account.getOwnerName()
            );
            System.out.println(
                    "Account Type   : "
                            + account.getClass().getSimpleName()
            );
            System.out.println(
                    "Balance        : Rs. " + account.getBalance()
            );
        }

        System.out.println("---------------------------------------");
    }

    // Process multiple transactions
    private static void processBatchTransactions() {

        List<CommandParser.Command> commands = new ArrayList<>();

        System.out.println("\n========== BATCH PROCESSING ==========");
        System.out.println("Enter transactions in this format:");
        System.out.println("DEPOSIT AC0001 500");
        System.out.println("WITHDRAW AC0002 200");
        System.out.println("Press ENTER on an empty line to finish.");
        System.out.println("--------------------------------------");

        while (true) {

            System.out.print("Transaction: ");
            String line = sc.nextLine().trim();

            if (line.isEmpty()) {
                break;
            }

            try {
                CommandParser.Command command =
                        CommandParser.parse(line);

                if (command.type() == CommandParser.TransactionType.TRANSFER) {
                    System.out.println(
                            "Batch transfer is not supported by the current "
                                    + "three-field command format. Use the "
                                    + "Transfer menu option instead."
                    );
                    continue;
                }

                commands.add(command);

            } catch (Exception e) {
                System.out.println(
                        "Invalid transaction: " + e.getMessage()
                );
            }
        }

        if (commands.isEmpty()) {
            System.out.println("No valid transactions entered.");
            return;
        }

        System.out.println("\nProcessing batch transactions...");

        List<String> results = bankService.processBatch(commands);

        for (String result : results) {
            System.out.println(result);
        }

        // Record batch transactions in the persistent log.
        for (CommandParser.Command command : commands) {

            String transaction =
                    command.type().name()
                            + "|"
                            + command.accountNumber()
                            + "|"
                            + command.amount();

            try {
                TransactionLog.append(transactionFile, transaction);
            } catch (IOException e) {
                System.out.println(
                        "Could not write transaction log: "
                                + e.getMessage()
                );
            }
        }

        saveAccounts();

        System.out.println("Batch processing completed.");
    }

    // Generate end-of-day report
    private static void generateEndOfDayReport() {

        try {
            Files.createDirectories(dataFolder);
            Files.createDirectories(logsFolder);

            System.out.println("\nGenerating end-of-day report...");

            ReportGenerator.generate(
                    logsFolder,
                    reportFile
            );

            System.out.println(
                    "Report generated successfully."
            );

            System.out.println(
                    "Report location: " + reportFile
            );

        } catch (Exception e) {
            System.out.println(
                    "Could not generate report: " + e.getMessage()
            );
        }
    }

    // Read and validate an account number
    private static String readAccountNumber() {

        if (accountStore.isEmpty()) {
            System.out.println(
                    "No accounts exist. Open an account first."
            );
            return null;
        }

        System.out.println("\nAvailable accounts:");

        for (Account account : bankService.listAccounts()) {

            System.out.println(
                    account.getAccountNumber()
                            + " | " + account.getOwnerName()
                            + " | Balance: Rs. "
                            + account.getBalance()
            );
        }

        System.out.print("Enter account number: ");

        String accountNumber = sc.nextLine().trim();

        if (transactionProcessor.getAccount(accountNumber) == null) {
            System.out.println("Account not found.");
            return null;
        }

        return accountNumber;
    }

    // Record a transaction in the persistent log
    private static void recordTransaction(String transaction)
            throws IOException {

        Files.createDirectories(logsFolder);

        TransactionLog.append(transactionFile, transaction);
    }

    // Save all accounts
    private static void saveAccounts() {

        try {
            Files.createDirectories(dataFolder);

            StatePersister.save(
                    accountStore.getAll().toArray(new Account[0]),
                    stateFile
            );

            System.out.println(
                    "Accounts saved to: " + stateFile
            );

        } catch (Exception e) {
            System.out.println(
                    "Could not save accounts: " + e.getMessage()
            );
        }
    }

    // Save accounts and generate report before exiting
    private static void saveAndReport() {

        try {
            Files.createDirectories(dataFolder);
            Files.createDirectories(logsFolder);

            saveAccounts();

            System.out.println("\n--- SAVED ACCOUNTS ---");

            Account[] loadedAccounts =
                    StatePersister.load(stateFile);

            for (Account account : loadedAccounts) {
                System.out.println(account);
            }

            System.out.println("\n--- END-OF-DAY REPORT ---");

            ReportGenerator.generate(
                    logsFolder,
                    reportFile
            );

            System.out.println(
                    "Report saved to: " + reportFile
            );

        } catch (Exception e) {
            System.out.println(
                    "Could not save accounts or generate report: "
                            + e.getMessage()
            );
        }
    }

    // Read integer input
    private static int readInt() {

        try {
            return Integer.parseInt(sc.nextLine().trim());

        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Read long input
    private static long readLong() {

        try {
            return Long.parseLong(sc.nextLine().trim());

        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
