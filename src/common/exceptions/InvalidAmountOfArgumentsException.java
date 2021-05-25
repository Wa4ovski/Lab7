package common.exceptions;


/**
 * Generated when user types wrong amount of arguments for a command.
 */
public class InvalidAmountOfArgumentsException extends Exception {

    private int requiredAmount;
    public InvalidAmountOfArgumentsException(int requiredAmount) {
        super("Expected " + requiredAmount + " arguments");
        this.requiredAmount = requiredAmount;
    }

    public int getRequiredAmount() {
        return requiredAmount;
    }


    public void printMessage() {
        System.out.println("Error. Expected " + getRequiredAmount() + " arguments");
    }
}
