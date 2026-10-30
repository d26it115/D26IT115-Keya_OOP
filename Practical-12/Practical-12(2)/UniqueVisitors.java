
import java.util.*;

public class UniqueVisitors {
    public static void main(String[] args) {
        List<String> logins = Arrays.asList(
                "Keya", "Dhyanam", "Hardi", "Keya",
                "Dev", "Dhyanam", "Meera", "Hardi"
        );

        HashSet<String> uniqueUsers = new HashSet<>(logins);

        LinkedHashSet<String> orderedUsers =
                new LinkedHashSet<>(logins);

        System.out.println("Unique visitor count: "
                + uniqueUsers.size());

        System.out.println("First three distinct visitors:");

        int count = 0;

        for (String user : orderedUsers) {
            if (count == 3) {
                break;
            }

            System.out.println(user);
            count++;
        }
    }
}
