package tw.com.ischool.fireflylite.util.dsutil;

public class DSAServerException extends RuntimeException {

	private static final long serialVersionUID = 2990934648382042120L;

	private String statusCode;

	private String statusMsg;

	public DSAServerException(DSStatus status) {
		super(status.message());

		this.statusCode = status.code();
		this.statusMsg = status.message();
	}

	/**
	 * 建立 DSAServerException 的實體。
	 * 
	 * @param status
	 *            例外狀態代碼。
	 * @param msg
	 *            例外訊息。
	 */
	public DSAServerException(DSStatus status, String msg) {
		super(msg);
		this.statusCode = status.code();
		this.statusMsg = status.message();
	}

	/**
	 * 建立 DSAServerException 的實體。
	 * 
	 * @param status
	 *            例外狀態代碼。
	 * @param msg
	 *            例外訊息。
	 * @param cause
	 *            產生此例外的原因。
	 */
	public DSAServerException(DSStatus status, Throwable cause) {
		super(status.message(), cause);

		StringBuffer buffer = new StringBuffer();
		StackTraceElement[] traces = cause.getStackTrace();

		buffer.append("訊息：" + status.message() + "\n\n");
		getCauseMessage(cause, buffer); // 取得所有 Exception 訊息。
		buffer.append("\n呼叫堆疊：\n");
		for (int i = 0; i < (traces.length > 5 ? 5 : traces.length); i++)
			buffer.append(traces[i].toString() + "\n");

		if (traces.length > 5)
			buffer.append("其他省略...(總共" + traces.length + "條)\n");

		this.statusMsg = buffer.toString();
		this.statusCode = status.code();
	}

	private void getCauseMessage(Throwable cause, StringBuffer buffer) {
		buffer.append("底層訊息：" + cause.toString() + "\n");

		if (cause.getCause() != null)
			getCauseMessage(cause.getCause(), buffer);
	}

	public DSAServerException(DSStatus status, DSAServiceException cause) {
		super(status.message(), cause);

		this.statusCode = status.code();
		this.statusMsg = status.message();
	}

	/**
	 * 取得例外的狀態代碼。
	 * 
	 * @return
	 */
	public String getStatusCode() {
		return this.statusCode;
	}

	public String getStatusMessage() {
		return this.statusMsg;
	}

	public DSStatus getStatus() {
		return new DSStatus(this.statusCode, this.statusMsg);
	}
}
