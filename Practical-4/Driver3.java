import java.util.Scanner;

public class Driver3 {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String pw;

        do {
            System.out.print("Enter password: ");
            pw = sc.nextLine();

            PasswordChecker.checkRules(pw);

            if (!PasswordChecker.strength(pw).equals("Strong")) {
                System.out.println("Password is not strong. Try again.");
            }

        } while (!PasswordChecker.strength(pw).equals("Strong"));

        System.out.println("Strong password accepted!");

        sc.close();
    }
}
