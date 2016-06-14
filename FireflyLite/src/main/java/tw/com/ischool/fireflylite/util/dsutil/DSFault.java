package tw.com.ischool.fireflylite.util.dsutil;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tw.com.ischool.fireflylite.util.XmlHelper;


public class DSFault {

	private String source, code, msg, detail;

	private DSFault innerFault;

	public DSFault(String source, String code, String msg, String detail) {
		this.source = source;
		this.code = code;
		this.msg = msg;
		this.detail = detail;

		this.innerFault = null;
	}

	public DSFault(String source, String code, String msg, String detail,
			DSFault innerFault) {
		this(source, code, msg, detail);

		this.innerFault = innerFault;
	}

	public DSFault(DSAServiceException exp) {
		this.source = exp.getSource();
		this.code = exp.getCode();
		this.msg = exp.getMessage();
		this.detail = exp.getDetail();

		if (exp.getCause() instanceof DSAServiceException) {
			this.innerFault = new DSFault((DSAServiceException) exp.getCause());
		} else
			this.innerFault = null;
	}

	public DSFault(Element faultElement) {
		XmlHelper hlpFault = new XmlHelper(faultElement);

		this.source = hlpFault.getElementText("Source");
		this.code = hlpFault.getElementText("Code");
		this.msg = hlpFault.getElementText("Message");
		this.detail = hlpFault.getElementText("Detail");

		Node nextsibling = faultElement.getNextSibling();

		if (nextsibling != null
				&& nextsibling.getNodeType() == Node.ELEMENT_NODE) {

			innerFault = new DSFault((Element) nextsibling);
		}
	}

	public String getSource() {
		return source;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return msg;
	}

	public String getDetail() {
		return detail;
	}

	public DSFault getInnerFault() {
		return innerFault;
	}
}
