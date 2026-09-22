import java.util.Scanner;

public class Driver1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of cards: ");
        int n = sc.nextInt();
        sc.nextLine();

        Card[] cards = new Card[n];

        for (int i = 0; i < n; i++) {
            System.out.print("Enter rank: ");
            String rank = sc.nextLine();

            System.out.print("Enter suit: ");
            String suit = sc.nextLine();

            Card newCard = new Card(rank, suit);

            for (int j = 0; j < i; j++) {
                if (newCard.equals(cards[j])) {
                    System.out.println("Duplicate found: " + newCard);
                    return;
                }
            }

            cards[i] = newCard;
        }
    }
}