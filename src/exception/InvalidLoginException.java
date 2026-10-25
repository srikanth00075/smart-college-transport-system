package exception;

/**
 * CS23304 - Unit 3: User-defined Exception Handling
 * Thrown when user authentication fails due to incorrect credentials or role mismatch.
 */
public class InvalidLoginException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidLoginException(String message) {
        super(message);
    }

    public InvalidLoginException(String email, String role) {
        super(String.format("Authentication failed for '%s' with role '%s'. Please check your credentials.", email, role));
    }
}
