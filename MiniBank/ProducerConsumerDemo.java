public class ProducerConsumerDemo {

    private static final String STOP = "STOP";

    public static void runDemo()
            throws InterruptedException {

        System.out.println("\n===== PRODUCER-CONSUMER =====");

        TransactionBuffer buffer = new TransactionBuffer(3);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.put("Transaction-" + i);
                }

                buffer.put(STOP);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    String transaction = buffer.take();

                    if (STOP.equals(transaction)) {
                        break;
                    }

                    System.out.println(
                            Thread.currentThread().getName()
                                    + " processed: " + transaction
                    );
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        System.out.println("Producer and consumer completed.");
    }
}