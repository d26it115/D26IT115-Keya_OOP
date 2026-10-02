public class Driver {

    public static void main(String[] args) {

        String[] passwords = {
                "abc",
                "abcdefgh",
                "Abcdefgh",
                "abcdefg1",
                "Abcd1234!",
                "Password1"
        };

        for (String pw : passwords) {

            System.out.println("Password: " + pw);

            if (PasswordChecker.hasMinimumLength(pw)) {
                System.out.println("Length >= 8: Passed");
            } else {
                System.out.println("Length >= 8: Failed");
            }

            if (PasswordChecker.hasUppercase(pw)) {
                System.out.println("Uppercase letter: Passed");
            } else {
                System.out.println("Uppercase letter: Failed");
            }

            if (PasswordChecker.hasDigit(pw)) {
                System.out.println("Digit: Passed");
            } else {
                System.out.println("Digit: Failed");
            }

            if (PasswordChecker.hasSpecialCharacter(pw)) {
                System.out.println("Special character: Passed");
            } else {
                System.out.println("Special character: Failed");
            }

            System.out.println("Strength: " + PasswordChecker.strength(pw));
            System.out.println("----------------------------");
        }
    }
}