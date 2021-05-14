package statesman;

public class MalformedResourceException extends Exception {

	private static final long serialVersionUID = -8669659145442902322L;

	public MalformedResourceException() {
		super();
	}

	public MalformedResourceException(String message) {
		super(message);
	}

	public MalformedResourceException(Throwable cause) {
		super(cause);
	}

	public MalformedResourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public MalformedResourceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
