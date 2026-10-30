
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

// Custom annotation to check blank fields
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface NotBlank {
}

// Custom annotation to check maximum length
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface MaxLength {
    int value();
}

// Form class with validation annotations
class SignupForm {
    @NotBlank
    @MaxLength(20)
    private String username;

    @NotBlank
    @MaxLength(30)
    private String email;

    @NotBlank
    @MaxLength(15)
    private String password;

    public SignupForm(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}

// Validator using reflection
class FormChecker {
    public static List<String> validate(Object obj) {
        List<String> errors = new ArrayList<>();

        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                Object value = field.get(obj);

                // Check @NotBlank
                if (field.isAnnotationPresent(NotBlank.class)) {
                    if (value == null ||
                            value.toString().trim().isEmpty()) {
                        errors.add(field.getName()
                                + " must not be blank.");
                    }
                }

                // Check @MaxLength
                if (field.isAnnotationPresent(MaxLength.class)) {
                    MaxLength annotation =
                            field.getAnnotation(MaxLength.class);

                    if (value != null &&
                            value.toString().length() > annotation.value()) {
                        errors.add(field.getName()
                                + " must not exceed "
                                + annotation.value() + " characters.");
                    }
                }

            } catch (IllegalAccessException e) {
                errors.add("Unable to access field: "
                        + field.getName());
            }
        }

        return errors;
    }
}

// Main class
public class FormValidator {
    public static void main(String[] args) {
        SignupForm form = new SignupForm(
                " ",
                "verylongemailaddress@example.com",
                "thispasswordiswaytoolong"
        );

        List<String> errors = FormChecker.validate(form);

        if (errors.isEmpty()) {
            System.out.println("Form is valid.");
        } else {
            System.out.println("Validation errors:");

            for (String error : errors) {
                System.out.println("- " + error);
            }
        }
    }
}
