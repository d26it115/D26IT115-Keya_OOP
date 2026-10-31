
import model.Account;
import model.SavingsAccount;
import model.CurrentAccount;
import model.FixedDepositAccount;

import exception.BankException;
import exception.AccountNotFoundException;
import exception.InvalidAmountException;

import util.Validator;
import util.StatementFormatter;
import util.CommandParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BankService {

    private final Repository<Account> repository;
    private final AccountStore accountStore;
    private final TransactionProcessor transactionProcessor;

    public BankService(
            Repository<Account> repository,
            AccountStore accountStore,
            TransactionProcessor transactionProcessor) {

        if (repository == null ||
                accountStore == null ||
                transactionProcessor == null) {

            throw new IllegalArgumentException(
                    "Bank service dependencies cannot be null."
            );
        }

        this.repository = repository;
        this.accountStore = accountStore;
        this.transactionProcessor = transactionProcessor;

        // Synchronize the repository with existing accounts.
        for (Account account : accountStore.getAll()) {
            repository.save(account);
        }
    }

    // Open an account based on its type.
    public Account openAccount(
            String accountType,
            String ownerName,
            long openingBalance,
            long additionalLimit) throws BankException {

        validateAccountDetails(ownerName, openingBalance);

        if (accountType == null) {
            throw new BankException("Account type is required.");
        }

        Account account;

        switch (accountType.trim().toUpperCase()) {

            case "SAVINGS":

                if (additionalLimit < 0 ||
                        additionalLimit > openingBalance) {

                    throw new BankException(
                            "Invalid minimum balance."
                    );
                }

                account = new SavingsAccount(
                        ownerName,
                        openingBalance,
                        additionalLimit
                );
                break;

            case "CURRENT":

                if (additionalLimit < 0) {
                    throw new BankException(
                            "Overdraft limit cannot be negative."
                    );
                }

                account = new CurrentAccount(
                        ownerName,
                        openingBalance,
                        additionalLimit
                );
                break;

            case "FIXED":
            case "FIXED_DEPOSIT":

                account = new FixedDepositAccount(
                        ownerName,
                        openingBalance
                );
                break;

            default:
                throw new BankException(
                        "Invalid account type."
                );
        }

        registerAccount(account);

        return account;
    }

    // Register account in all three stores.
    private void registerAccount(Account account) {

        accountStore.add(account);

        transactionProcessor.registerAccount(account);

        repository.save(account);
    }

    // Deposit money.
    public void deposit(
            String accountNumber,
            long amount) throws BankException {

        Account account = getRequiredAccount(accountNumber);

        account.deposit(amount);

        accountStore.addTransaction(
                "DEPOSIT|" + accountNumber + "|" + amount
        );

        repository.save(account);
    }

    // Withdraw money.
    public void withdraw(
            String accountNumber,
            long amount) throws BankException {

        Account account = getRequiredAccount(accountNumber);

        account.withdraw(amount);

        accountStore.addTransaction(
                "WITHDRAW|" + accountNumber + "|" + amount
        );

        repository.save(account);
    }

    // Transfer money safely between accounts.
    public void transfer(
            String fromAccountNumber,
            String toAccountNumber,
            long amount) throws BankException {

        if (fromAccountNumber == null ||
                toAccountNumber == null) {

            throw new BankException(
                    "Account numbers cannot be empty."
            );
        }

        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new BankException(
                    "Source and destination must be different."
            );
        }

        Account from = getRequiredAccount(fromAccountNumber);
        Account to = getRequiredAccount(toAccountNumber);

        SafeTransfer.transferSafely(from, to, amount);

        accountStore.addTransaction(
                "WITHDRAW|" + fromAccountNumber + "|" + amount
        );

        accountStore.addTransaction(
                "DEPOSIT|" + toAccountNumber + "|" + amount
        );

        repository.save(from);
        repository.save(to);
    }

    // Generate account statement.
    public String statement(String accountNumber)
            throws BankException {

        Account account = getRequiredAccount(accountNumber);

        return StatementFormatter.buildStatement(account);
    }

    // Return all accounts.
    public ArrayList<Account> listAccounts() {
        return accountStore.getAll();
    }

    // Find account or throw an exception.
    public Account getRequiredAccount(String accountNumber)
            throws AccountNotFoundException {

        if (accountNumber == null ||
                accountNumber.isBlank()) {

            throw new AccountNotFoundException(
                    "Account number cannot be empty."
            );
        }

        Account account =
                transactionProcessor.getAccount(accountNumber);

        if (account == null) {
            throw new AccountNotFoundException(
                    "Account not found: " + accountNumber
            );
        }

        return account;
    }

    // Validate account opening details.
    private void validateAccountDetails(
            String ownerName,
            long openingBalance) throws BankException {

        if (ownerName == null || ownerName.isBlank()) {
            throw new BankException(
                    "Account holder name cannot be empty."
            );
        }

        if (openingBalance < 0) {
            throw new InvalidAmountException(
                    "Opening balance cannot be negative."
            );
        }
    }

    // Validate customer information.
    public void validateCustomerDetails(
            String mobile,
            String email,
            String pan,
            String ifsc) throws BankException {

        if (!Validator.isValidMobile(mobile)) {
            throw new BankException(
                    "Invalid mobile number."
            );
        }

        if (!Validator.isValidEmail(email)) {
            throw new BankException(
                    "Invalid email address."
            );
        }

        if (!Validator.isValidPan(pan)) {
            throw new BankException(
                    "Invalid PAN number."
            );
        }

        if (!Validator.isValidIfsc(ifsc)) {
            throw new BankException(
                    "Invalid IFSC code."
            );
        }
    }

    // Process batch transactions using the thread pool.
    public ArrayList<String> processBatch(
            List<CommandParser.Command> commands) {

        ArrayList<String> results = new ArrayList<>();

        if (commands == null || commands.isEmpty()) {
            results.add("No transactions to process.");
            return results;
        }

        CountDownLatch latch =
                new CountDownLatch(commands.size());

        ConcurrentLinkedQueue<String> messages =
                new ConcurrentLinkedQueue<>();

        for (CommandParser.Command command : commands) {

            transactionProcessor.submit(() -> {

                try {
                    Account account = getRequiredAccount(
                            command.accountNumber()
                    );

                    switch (command.type()) {

                        case DEPOSIT:

                            account.deposit(command.amount());

                            accountStore.addTransaction(
                                    "DEPOSIT|"
                                            + command.accountNumber()
                                            + "|" + command.amount()
                            );

                            repository.save(account);

                            messages.add(
                                    "Deposit successful for "
                                            + command.accountNumber()
                            );
                            break;

                        case WITHDRAW:

                            account.withdraw(command.amount());

                            accountStore.addTransaction(
                                    "WITHDRAW|"
                                            + command.accountNumber()
                                            + "|" + command.amount()
                            );

                            repository.save(account);

                            messages.add(
                                    "Withdrawal successful for "
                                            + command.accountNumber()
                            );
                            break;

                        case TRANSFER:

                            messages.add(
                                    "Batch transfer requires a source "
                                            + "and destination account."
                            );
                            break;

                        default:

                            messages.add(
                                    "Unsupported transaction."
                            );
                    }

                } catch (Exception e) {

                    messages.add(
                            "Transaction failed for "
                                    + command.accountNumber()
                                    + ": " + e.getMessage()
                    );

                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            messages.add(
                    "Batch processing was interrupted."
            );
        }

        results.addAll(messages);

        results.add(
                "Batch processing completed."
        );

        return results;
    }
}
