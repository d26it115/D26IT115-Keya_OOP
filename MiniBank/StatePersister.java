
import model.Account;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class StatePersister {

    public static void save(Account[] accounts, Path file)
            throws IOException {

        Path parent = file.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             Files.newOutputStream(file))) {

            out.writeObject(accounts);
        }

        System.out.println("Accounts saved to: " + file);
    }

    public static Account[] load(Path file)
            throws IOException, ClassNotFoundException {

        try (ObjectInputStream in =
                     new ObjectInputStream(
                             Files.newInputStream(file))) {

            return (Account[]) in.readObject();
        }
    }
}
