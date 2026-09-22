import java.util.Scanner;

public class Driver3 {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter password: ");
        String pw = sc.nextLine();

        PasswordChecker.checkRules(pw);
    }
}