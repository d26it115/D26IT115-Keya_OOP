
import model.Account;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountStore {

    private final HashMap<String, Account> accounts =
            new HashMap<>();

    private final ArrayList<String> transactionHistory =
            new ArrayList<>();

    public synchronized void add(Account account) {
        if (account == null) {
            throw new IllegalArgumentException(
                    "Account cannot be null."
            );
        }

        String accountNumber = account.getAccountNumber();

        if (accounts.containsKey(accountNumber)) {
            throw new IllegalArgumentException(
                    "Account number already exists: " + accountNumber
            );
        }

        accounts.put(accountNumber, account);
    }

    public synchronized Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public synchronized ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    public synchronized ArrayList<Account> sortedByBalance() {
        ArrayList<Account> sorted =
                new ArrayList<>(accounts.values());

        sorted.sort(
                Comparator.comparingLong(Account::getBalance)
                        .thenComparing(Account::getAccountNumber)
        );

        return sorted;
    }

    public synchronized void addTransaction(String transaction) {
        if (transaction != null && !transaction.isBlank()) {
            transactionHistory.add(transaction);
        }
    }

    public synchronized ArrayList<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    public synchronized int size() {
        return accounts.size();
    }

    public synchronized boolean isEmpty() {
        return accounts.isEmpty();
    }

    public synchronized void clear() {
        accounts.clear();
        transactionHistory.clear();
    }


    public synchronized void replaceAll(List<Account> newAccounts) {
        accounts.clear();

        for (Account account : newAccounts) {
            add(account);
        }
    }

    public synchronized Map<String, Account> snapshot() {
        return new HashMap<>(accounts);
    }
}
