package exceptions;

/**
 * Generated when user doesn't have enough rights to access the file.
 */
public class AccessDeniedException extends Exception {
    private String message;

    public AccessDeniedException(String message) {
        super(message);
        this.message = message;
    }

    public void printMessage() {
        System.out.println(message);
    }
}

