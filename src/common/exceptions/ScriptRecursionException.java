package common.exceptions;

/**
 * Generated when a script tries to launch itself.
 */
public class ScriptRecursionException extends Exception {
    public ScriptRecursionException(String message) {
        super(message);
    }
}
