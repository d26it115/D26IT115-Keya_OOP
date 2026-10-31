import java.util.LinkedList;
import java.util.Queue;

public class TransactionBuffer {

    private final Queue<String> buffer = new LinkedList<>();
    private final int capacity;

    public TransactionBuffer(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(String transaction)
            throws InterruptedException {

        while (buffer.size() >= capacity) {
            wait();
        }

        buffer.add(transaction);

        System.out.println("Produced: " + transaction);

        notifyAll();
    }

    public synchronized String take()
            throws InterruptedException {

        while (buffer.isEmpty()) {
            wait();
        }

        String transaction = buffer.remove();

        notifyAll();

        return transaction;
    }
}