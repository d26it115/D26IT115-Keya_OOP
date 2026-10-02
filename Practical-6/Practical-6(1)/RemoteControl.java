interface Switchable {

    void on();

    void off();

    // Default method
    default void toggle() {
        off();
        on();
    }
}

class Fan implements Switchable {

    @Override
    public void on() {
        System.out.println("Fan is ON");
    }

    @Override
    public void off() {
        System.out.println("Fan is OFF");
    }
}

class Light implements Switchable {

    @Override
    public void on() {
        System.out.println("Light is ON");
    }

    @Override
    public void off() {
        System.out.println("Light is OFF");
    }
}

// Functional interface
@FunctionalInterface
interface SwitchPolicy {

    boolean maySwitchOn(Switchable device, int hour);
}

public class RemoteControl {

    public static void main(String[] args) {

        // Array containing different devices
        Switchable[] devices = {
                new Fan(),
                new Light()
        };

        System.out.println("=== Toggling Devices ===");

        // Loop over Switchable[] and toggle each device
        for (Switchable device : devices) {
            device.toggle();
            System.out.println();
        }

        // Anonymous class
        SwitchPolicy anonymousPolicy = new SwitchPolicy() {

            @Override
            public boolean maySwitchOn(Switchable device, int hour) {
                return hour >= 6 && hour <= 22;
            }
        };

        // Lambda expression
        SwitchPolicy lambdaPolicy =
                (device, hour) -> hour >= 6 && hour <= 22;

        int hour = 10;

        System.out.println("=== Anonymous Class Policy ===");

        for (Switchable device : devices) {

            if (anonymousPolicy.maySwitchOn(device, hour)) {
                device.on();
            } else {
                System.out.println("Device cannot be switched ON at " + hour + ":00");
            }
        }

        System.out.println();

        System.out.println("=== Lambda Policy ===");

        for (Switchable device : devices) {

            if (lambdaPolicy.maySwitchOn(device, hour)) {
                device.on();
            } else {
                System.out.println("Device cannot be switched ON at " + hour + ":00");
            }
        }
    }
}