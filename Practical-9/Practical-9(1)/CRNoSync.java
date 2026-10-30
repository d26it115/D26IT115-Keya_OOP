class Counter {
    int count = 0;

    void increment (){
        count ++;
    }
}

public class CRNoSync {
    public static void main (String [] args)throws InterruptedException{
        Counter counter = new Counter();

        int numberOfThreads = 10;
        int incrementsperThreads = 1000;

        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {

            threads[i] = new Thread(() -> {

                for (int j = 0; j < incrementsperThreads; j++) {
                    counter.increment();
                }

            });

            threads[i].start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i].join();
        }
        System.out.println("Expected: 10000");
        System.out.println("Final count: " + counter.count);
    }
}