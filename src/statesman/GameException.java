package statesman;

public class GameException extends Exception {

    private static final long serialVersionUID = -8669659145442902322L;

    public GameException() {
        super();
    }

    public GameException(String message) {
        super(message);
    }

    public GameException(Throwable cause) {
        super(cause);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
