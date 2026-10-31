public class BankResource
        implements AutoCloseable {

    public BankResource() {

        System.out.println(
                "Bank resource opened."
        );
    }

    public void use() {

        System.out.println(
                "Bank resource is being used."
        );
    }

    @Override
    public void close() {

        System.out.println(
                "Bank resource closed automatically."
        );
    }
}