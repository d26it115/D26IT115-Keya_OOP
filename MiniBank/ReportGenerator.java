
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ReportGenerator {

    public static void generate(Path logsFolder, Path reportFile)
            throws IOException {

        long totalDeposits = 0;
        long totalWithdrawals = 0;

        List<String> fileDetails = new ArrayList<>();

        if (Files.exists(logsFolder)) {

            try (Stream<Path> paths = Files.walk(logsFolder)) {

                for (Path path : paths
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".log"))
                        .toList()) {

                    BasicFileAttributes attributes =
                            Files.readAttributes(
                                    path,
                                    BasicFileAttributes.class
                            );

                    fileDetails.add(
                            path + " | Size: " + attributes.size()
                                    + " bytes | Last modified: "
                                    + attributes.lastModifiedTime()
                    );

                    try (BufferedReader reader =
                                 Files.newBufferedReader(path)) {

                        String line;

                        while ((line = reader.readLine()) != null) {

                            String[] parts = line.split("\\|");

                            if (parts.length != 3) {
                                continue;
                            }

                            try {
                                long amount =
                                        Long.parseLong(parts[2].trim());

                                if ("DEPOSIT".equals(parts[0].trim())) {
                                    totalDeposits += amount;
                                } else if ("WITHDRAW".equals(parts[0].trim())) {
                                    totalWithdrawals += amount;
                                }

                            } catch (NumberFormatException e) {
                                System.out.println(
                                        "Skipping invalid entry: " + line
                                );
                            }
                        }
                    }
                }
            }
        }

        StringBuilder report = new StringBuilder();

        report.append("========== END-OF-DAY REPORT ==========\n");
        report.append("Total deposits: Rs. ")
                .append(totalDeposits).append("\n");
        report.append("Total withdrawals: Rs. ")
                .append(totalWithdrawals).append("\n");
        report.append("Net transaction amount: Rs. ")
                .append(totalDeposits - totalWithdrawals)
                .append("\n\n");
        report.append("========== LOG FILE DETAILS ==========\n");

        if (fileDetails.isEmpty()) {
            report.append("No log files found.\n");
        } else {
            for (String detail : fileDetails) {
                report.append(detail).append("\n");
            }
        }

        Path parent = reportFile.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(reportFile, report.toString());

        System.out.println(report);
        System.out.println("Report saved to: " + reportFile);
    }
}
