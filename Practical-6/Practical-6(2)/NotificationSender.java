@FunctionalInterface
interface Notifier {
    void send(String message);
}


interface Urgent {
}

class UrgentNotifier implements Notifier, Urgent {

    private Notifier notifier;

    UrgentNotifier(Notifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public void send(String message) {
        notifier.send(message);
    }
}

public class NotificationSender {

    public static void main(String[] args) {

        //lambda
        Notifier emailSender = message ->
                System.out.println("Email: " + message);

        // SMS lambda
        Notifier smsSender = message ->
                System.out.println("SMS: " + message);


        Notifier urgentEmail = new UrgentNotifier(emailSender);


        Notifier[] senders = {
                urgentEmail,
                smsSender
        };

        String message = "Important meeting at 10 AM";

        System.out.println("=== Broadcasting Message ===");

        for (Notifier sender : senders) {


            if (sender instanceof Urgent) {
                sender.send(message);
                sender.send(message);
            } else {
                sender.send(message);
            }
        }
    }
}