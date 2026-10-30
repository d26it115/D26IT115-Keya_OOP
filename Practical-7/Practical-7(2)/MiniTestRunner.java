
import java.lang.annotation.*;
import java.lang.reflect.Method;

// Define custom Run annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Run {
}

// Class containing test methods
class MyTests {

    @Run
    public void testAddition() {
        int result = 10 + 20;
        System.out.println("Addition Test: "
                + (result == 30 ? "PASSED" : "FAILED"));
    }

    @Run
    public void testSubtraction() {
        int result = 20 - 10;
        System.out.println("Subtraction Test: "
                + (result == 10 ? "PASSED" : "FAILED"));
    }

    public void normalMethod() {
        System.out.println("This method should not run.");
    }

    @Run
    public void testMultiplication() {
        int result = 5 * 4;
        System.out.println("Multiplication Test: "
                + (result == 20 ? "PASSED" : "FAILED"));
    }
}

// Mini test runner
public class MiniTestRunner {
    public static void main(String[] args) {
        MyTests tests = new MyTests();
        int count = 0;

        Method[] methods = tests.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Run.class)
                    && method.getParameterCount() == 0) {
                try {
                    System.out.println("Running: "
                            + method.getName());

                    method.invoke(tests);
                    count++;

                } catch (Exception e) {
                    System.out.println("Test failed: "
                            + method.getName());
                }
            }
        }

        System.out.println("\nTotal tests run: " + count);
    }
}
