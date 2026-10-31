import exception.BankException;
import model.Account;

public class SafeTransfer implements Runnable {

    private final Account from;
    private final Account to;
    private final long amount;

    public SafeTransfer(Account from, Account to, long amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public static void transferSafely(
            Account from,
            Account to,
            long amount) throws BankException {

        if (from == null || to == null) {
            throw new BankException("Account does not exist.");
        }

        if (from == to) {
            throw new BankException(
                    "Source and destination must be different."
            );
        }

        if (amount <= 0) {
            throw new BankException(
                    "Transfer amount must be positive."
            );
        }

        Account first;
        Account second;

        if (from.getAccountNumber().compareTo(
                to.getAccountNumber()) < 0) {
            first = from;
            second = to;
        } else {
            first = to;
            second = from;
        }

        synchronized (first) {
            synchronized (second) {

                from.withdraw(amount);
                to.deposit(amount);

                System.out.println(
                        Thread.currentThread().getName()
                                + " transferred Rs. " + amount
                                + " from " + from.getAccountNumber()
                                + " to " + to.getAccountNumber()
                );
            }
        }
    }

    @Override
    public void run() {
        try {
            transferSafely(from, to, amount);

        } catch (BankException e) {
            System.out.println(
                    Thread.currentThread().getName()
                            + " failed: " + e.getMessage()
            );
        }
    }
}