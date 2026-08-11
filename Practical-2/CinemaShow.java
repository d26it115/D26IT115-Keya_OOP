class CinemaShow {
    private String title;
    private int seatsAvailable;
    private final int capacity;
    private static int totalBooked = 0;

    CinemaShow(String title, int capacity) {
        this.title = title;
        this.capacity = capacity;
        seatsAvailable = capacity;
    }

    public CinemaShow(String title) {
        this(title, 100);
    }

    void book(int n) {
        if (n <= seatsAvailable) {
            seatsAvailable -= n;
            totalBooked += n;
        }
    }

    void cancel(int n) {
        seatsAvailable += n;

        if (seatsAvailable > capacity) {
            seatsAvailable = capacity;
        }
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    static int getTotalBooked() {
        return totalBooked;
    }

    public static void main(String args[]) {

    CinemaShow c1 = new CinemaShow("Spiderman", 50);

    c1.book(10);
    System.out.println("Book 10");
    System.out.println("Show available seats: " + c1.getSeatsAvailable());

    c1.book(20);
    System.out.println("Book 20");
    System.out.println("Show available seats: " + c1.getSeatsAvailable());

    c1.book(30);
    System.out.println("Book 30");
    System.out.println("Show available seats: " + c1.getSeatsAvailable());

    c1.cancel(10);
    System.out.println("After cancelling 10 seats: " + c1.getSeatsAvailable());

    c1.cancel(5);
    System.out.println("After cancelling 5 seats: " + c1.getSeatsAvailable());

    System.out.println("Total seats booked: " + c1.getTotalBooked());
}
}