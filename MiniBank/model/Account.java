
package model;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import model.annotation.Positive;
import service.InterestBearing;
import service.Transactable;

import java.io.Serializable;
import java.util.Objects;

public abstract class Account
        implements Transactable, InterestBearing,
        Comparable<Account>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String accountNumber;

    private String ownerName;

    @Positive
    private long balance;

    private boolean active;

    private static long accountCounter = 0;

    private static volatile boolean synchronizedMode = false;

    private static synchronized String generateAccountNumber() {
        accountCounter++;
        return String.format("AC%04d", accountCounter);
    }

    public static synchronized void observeAccountNumber(String number) {
        if (number == null || !number.matches("AC\\d+")) {
            return;
        }

        try {
            long value = Long.parseLong(number.substring(2));

            if (value > accountCounter) {
                accountCounter = value;
            }
        } catch (NumberFormatException ignored) {
            // Ignore malformed account numbers.
        }
    }

    public synchronized void assignNewAccountNumber() {
        this.accountNumber = generateAccountNumber();
    }

    public Account(String ownerName, long openingBalance) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = openingBalance;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    @Override
    public void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be greater than zero."
            );
        }

        if (synchronizedMode) {
            synchronized (this) {
                performDeposit(amount);
            }
        } else {
            performDeposit(amount);
        }
    }

    private void performDeposit(long amount) {
        long currentBalance = balance;
        Thread.yield();
        balance = currentBalance + amount;
    }

    @Override
    public boolean withdraw(long amount)
            throws InsufficientFundsException, InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Withdrawal amount must be greater than zero."
            );
        }

        if (synchronizedMode) {
            synchronized (this) {
                return performWithdraw(amount);
            }
        } else {
            return performWithdraw(amount);
        }
    }

    private boolean performWithdraw(long amount)
            throws InsufficientFundsException {

        if (!canWithdraw(amount)) {
            long shortfall = Math.max(0, amount - balance);

            throw new InsufficientFundsException(
                    "Insufficient funds for withdrawal.",
                    shortfall
            );
        }

        long currentBalance = balance;
        Thread.yield();
        balance = currentBalance - amount;

        return true;
    }

    public void transfer(Account to, long amount) throws BankException {
        try {
            if (to == null) {
                throw new BankException(
                        "Destination account does not exist."
                );
            }

            withdraw(amount);
            to.deposit(amount);

        } catch (InsufficientFundsException | InvalidAmountException e) {
            throw e;
        } finally {
            System.out.println("Transfer operation completed.");
        }
    }

    @Override
    public int compareTo(Account other) {
        if (other == null) {
            throw new NullPointerException(
                    "Cannot compare an account with null."
            );
        }

        return this.accountNumber.compareTo(other.accountNumber);
    }

    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public synchronized long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    public static void setSynchronizedMode(boolean value) {
        synchronizedMode = value;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", balance=" + getBalance() +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Account)) {
            return false;
        }

        Account account = (Account) o;

        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}
