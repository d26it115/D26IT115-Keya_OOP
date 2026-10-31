package util;
import java.util.regex.Pattern;

public class Validator {

    // Mobile: exactly 10 digits, starting from 6-9
    private static final Pattern MOBILE_PATTERN =
            Pattern.compile("^[6-9][0-9]{9}$");

    // Email: basic valid email format
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // PAN: 5 letters + 4 digits + 1 letter
    private static final Pattern PAN_PATTERN =
            Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");

    // IFSC: 4 letters + 0 + 6 alphanumeric characters
    private static final Pattern IFSC_PATTERN =
            Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");

    // Validate mobile number
    public static boolean isValidMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile).matches();
    }

    // Validate email
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    // Validate PAN
    public static boolean isValidPan(String pan) {
        return pan != null && PAN_PATTERN.matcher(pan).matches();
    }

    // Validate IFSC
    public static boolean isValidIfsc(String ifsc) {
        return ifsc != null && IFSC_PATTERN.matcher(ifsc).matches();
    }
}

