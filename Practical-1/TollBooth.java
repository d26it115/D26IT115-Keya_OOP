import java.util.Scanner;

public class TollBooth {

    record Vehicle(String number, String type) {}

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int total = 0;
        int bike = 0, car = 0, truck = 0;

        while (true) {
            System.out.print("Enter vehicle number: ");
            String number = sc.next();

            if (number.equalsIgnoreCase("done"))
                break;

            System.out.print("Enter type: ");
            String type = sc.next().toLowerCase();

            Vehicle v = new Vehicle(number, type);

            int toll = switch (v.type()) {
                case "bike" -> 20;
                case "car" -> 50;
                case "truck" -> 150;
                default -> 0;
            };

            total += toll;

            switch (v.type()) {
                case "bike" -> bike++;
                case "car" -> car++;
                case "truck" -> truck++;
            }
        }

        String mostFrequent;

        if (car >= bike && car >= truck)
            mostFrequent = "car";
        else if (bike >= truck)
            mostFrequent = "bike";
        else
            mostFrequent = "truck";

        System.out.println("Total toll: " + total);
        System.out.println("Most frequent: " + mostFrequent);
    }
}