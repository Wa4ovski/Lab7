package exceptions;

/**
 * Generated when a user tries to input disallowed value.
 */
public class DomainViolationException extends Exception {
    private String message;

    public DomainViolationException(String message) {
        super(message);
        this.message = message;
    }

    public void printMessage() {
        System.out.println(message);
    }
}
