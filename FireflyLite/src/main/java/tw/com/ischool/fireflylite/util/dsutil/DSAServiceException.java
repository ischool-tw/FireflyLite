package tw.com.ischool.fireflylite.util.dsutil;

import org.w3c.dom.Element;

import tw.com.ischool.fireflylite.util.XmlUtil;

public class DSAServiceException extends RuntimeException {
	private static final long serialVersionUID = 2990934648382042120L;

	private String source, code, detail, msg;

	public DSAServiceException(DSResponse response) {
		Element header = response.getHeader();
		Element dsfault = XmlUtil.selectElement(header, "DSFault");
		Element fault = XmlUtil.selectElement(dsfault, "Fault");

		source = XmlUtil.getElementText(fault, "Source");
		code = XmlUtil.getElementText(fault, "Code");
		msg = XmlUtil.getElementText(fault, "Message");
		detail = XmlUtil.getElementText(fault, "Detail");

		Element content = response.getContent();
		String inner = XmlUtil.getElementText(content, "InnerException");

		if (detail.isEmpty())
			detail = inner;
	}

	public DSAServiceException(Object source, String code, String msg) {
		this(source.toString(), code, msg, "");
	}

	public DSAServiceException(Object source, String code, String msg,
			Throwable cause) {
		this(source.toString(), code, msg, "", cause);

		StringBuffer buffer = new StringBuffer();
		StackTraceElement[] traces = cause.getStackTrace();

		getCauseMessage(cause, buffer); // 取得所有 Exception 訊息。
		buffer.append("\n呼叫堆疊：\n");
		for (int i = 0; i < traces.length; i++)
			buffer.append(traces[i].toString() + "\n");

		this.detail = buffer.toString();
	}

	private void getCauseMessage(Throwable cause, StringBuffer buffer) {
		buffer.append("底層訊息：" + cause.toString() + "\n");

		if (cause.getCause() != null)
			getCauseMessage(cause.getCause(), buffer);
	}

	public DSAServiceException(String source, String code, String msg,
			String detail) {
		super(msg);

		this.source = source;
		this.code = code;
		this.detail = detail;
	}

	public DSAServiceException(String source, String code, String msg,
			String detail, Throwable cause) {
		super(msg, cause);

		this.source = source;
		this.code = code;
		this.detail = detail;
	}

	/**
	 * 取得例外的來源。
	 * 
	 * @return
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * 取得例外的代碼。
	 * 
	 * @return
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * 取得例外的詳細資訊。
	 * 
	 * @return
	 */
	public String getDetail() {
		return this.detail;
	}

	@Override
	public String getMessage() {
		if (super.getMessage() == null)
			return this.msg;
		if (super.getMessage().isEmpty())
			return this.msg;
		return super.getMessage();
	}
}
