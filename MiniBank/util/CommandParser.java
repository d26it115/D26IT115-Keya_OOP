package util;

public class CommandParser {

    // Enum for transaction types
    public enum TransactionType {
        DEPOSIT,
        WITHDRAW,
        TRANSFER
    }

    // Record for storing a command
    public record Command(
            TransactionType type,
            String accountNumber,
            long amount) {
    }

    // Parse the command
    public static Command parse(String line) {

        // Split input by spaces
        String[] parts = line.trim().split("\\s+");

        // Command must have 3 parts
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Invalid command. Format: TYPE ACCOUNT_NUMBER AMOUNT"
            );
        }

        // Convert first part into TransactionType
        TransactionType type;

        try {
            type = TransactionType.valueOf(
                    parts[0].toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid transaction type: " + parts[0]
            );
        }

        // Get account number
        String accountNumber = parts[1];

        // Convert amount to long
        long amount;

        try {
            amount = Long.parseLong(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Amount must be a whole number."
            );
        }

        // Return Command object
        return new Command(
                type,
                accountNumber,
                amount
        );
    }
}