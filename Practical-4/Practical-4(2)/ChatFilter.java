public class ChatFilter {

    public static String filterLogs(String[] logs, String keyword) {

        StringBuilder report = new StringBuilder();
        int matches = 0;

        for (String line : logs) {

            String[] parts = line.split(" ", 3);

            // Skip malformed lines
            if (parts.length < 3) {
                continue;
            }

            String time = parts[0];
            String user = parts[1];
            String message = parts[2];

            // Case-insensitive keyword search
            if (message.toLowerCase().contains(keyword.toLowerCase())) {
                matches++;

                report.append(time)
                        .append(" ")
                        .append(user)
                        .append(": ")
                        .append(message)
                        .append("\n");
            }
        }

        return "Matches: " + matches + "\n" + report.toString();
    }
}