
import model.Account;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransactionProcessor {

    private final ExecutorService executor;

    private final ConcurrentHashMap<String, Account> sharedAccounts;

    public TransactionProcessor() {
        this(new ConcurrentHashMap<>());
    }

    public TransactionProcessor(
            ConcurrentHashMap<String, Account> sharedAccounts) {

        if (sharedAccounts == null) {
            throw new IllegalArgumentException(
                    "Shared account map cannot be null."
            );
        }

        this.sharedAccounts = sharedAccounts;
        this.executor = Executors.newFixedThreadPool(4);
    }

    public void registerAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException(
                    "Account cannot be null."
            );
        }

        Account existing = sharedAccounts.putIfAbsent(
                account.getAccountNumber(), account
        );

        if (existing != null && existing != account) {
            throw new IllegalArgumentException(
                    "Account number already exists: "
                            + account.getAccountNumber()
            );
        }
    }

    public Account getAccount(String accountNumber) {
        return sharedAccounts.get(accountNumber);
    }

    public ConcurrentHashMap<String, Account> getSharedAccounts() {
        return sharedAccounts;
    }
    public void submit(Runnable task) {
        executor.execute(task);
    }

    public void stop() {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("Transaction processor stopped.");
    }
}
