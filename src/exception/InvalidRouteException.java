package exception;

/**
 * CS23304 - Unit 3: User-defined Exception Handling
 * Thrown when an invalid route or stop is specified, or when route parameters are mismatched.
 */
public class InvalidRouteException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidRouteException(String message) {
        super(message);
    }

    public InvalidRouteException(String stopName, String routeName) {
        super(String.format("Specified stop '%s' does not exist along route '%s'.", stopName, routeName));
    }
}
