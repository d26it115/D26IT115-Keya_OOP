import java.util.*;

class OutOfStockException extends Exception {

    private int shortfall;

    public OutOfStockException(int shortfall) {
        super("Out of stock. Shortfall = " + shortfall);
        this.shortfall = shortfall;
    }

    public int getShortfall() {
        return shortfall;
    }
}

class InvalidQuantityException extends Exception {

    public InvalidQuantityException(String message) {
        super(message);
    }
}

class Warehouse {

    private Map<String, Integer> stock = new HashMap<>();

    public Warehouse() {
        stock.put("Laptop", 5);
        stock.put("Mouse", 10);
        stock.put("Keyboard", 7);
        stock.put("Monitor", 3);
    }

    public void issue(String item, int qty)
            throws OutOfStockException, InvalidQuantityException {

        if (qty <= 0) {
            throw new InvalidQuantityException(
                    "Quantity must be greater than 0."
            );
        }

        if (!stock.containsKey(item)) {
            throw new OutOfStockException(qty);
        }

        int available = stock.get(item);

        if (qty > available) {
            int shortfall = qty - available;
            throw new OutOfStockException(shortfall);
        }

        stock.put(item, available - qty);

        System.out.println(
                "Issued " + qty + " " + item +
                        "(s). Remaining stock = " + (available - qty)
        );
    }

    public void displayStock() {
        System.out.println("\nCurrent Stock:");
        for (Map.Entry<String, Integer> entry : stock.entrySet()) {
            System.out.println(
                    entry.getKey() + " : " + entry.getValue()
            );
        }
    }
}

public class StockIssue {

    public static void main(String[] args) {

        Warehouse warehouse = new Warehouse();

        String[][] requests = {
                {"Laptop", "2"},
                {"Mouse", "5"},
                {"Monitor", "5"},
                {"Keyboard", "0"},
                {"Keyboard", "3"},
                {"Tablet", "2"},
                {"Mouse", "3"}
        };

        System.out.println("Processing Requests...\n");

        for (String[] request : requests) {

            String item = request[0];
            int qty = Integer.parseInt(request[1]);

            System.out.println(
                    "Request: " + item + ", Quantity: " + qty
            );

            try {

                warehouse.issue(item, qty);

            } catch (OutOfStockException e) {

                System.out.println(
                        "Failed: Out of stock. Shortfall = "
                                + e.getShortfall()
                );

            } catch (InvalidQuantityException e) {

                System.out.println(
                        "Failed: " + e.getMessage()
                );
            }


        }

        warehouse.displayStock();
    }
}