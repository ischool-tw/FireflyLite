package tw.com.ischool.fireflylite.util.dsutil.accesspoint;

public class AccessPointException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String _code;

	public AccessPointException(String code, String message,
			Throwable innerException) {
		super(message, innerException);
		_code = code;
	}

	public AccessPointException(String code, String message) {
		super(message);
		_code = code;
	}

	public String getCode() {
		return _code;
	}
}
