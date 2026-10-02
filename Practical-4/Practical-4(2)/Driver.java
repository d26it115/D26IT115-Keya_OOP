import java.util.Scanner;

public class Driver {

    public static void main(String[] args) {

        String[] logs = {
                "10:05 alice Hello there",
                "10:10 bob How are you?",
                "10:15 charlie HELLO everyone",
                "10:20 malformed"
        };

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter keyword: ");
        String keyword = sc.nextLine();

        String result = ChatFilter.filterLogs(logs, keyword);

        System.out.println(result);

        sc.close();
    }
}