package tw.com.ischool.fireflylite.util.dsutil;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import tw.com.ischool.fireflylite.util.XmlHelper;

public class DSResponse extends DSEnvelope {

	public DSResponse() {
		super();
		InitialHeader();
	}

	public DSResponse(DSFault fault) {
		super();
		InitialHeader();
		setFault(fault);
	}

	public DSResponse(String responseString) {
		super(responseString);
	}

	public DSResponse(InputStream stream) throws IOException, SAXException {
		super(stream);
		InitialHeader();
	}

	public DSStatus getStatus() {
		XmlHelper header = new XmlHelper(getHeader());

		DSStatus status = new DSStatus(header.getElementText("Status/Code"),
				header.getElementText("Status/Message"));

		return status;
	}

	public void setStatus(DSStatus status) {
		XmlHelper header = new XmlHelper(getHeader());

		Element elmStatus = header.getElement("Status");

		XmlHelper newStatus = new XmlHelper(XmlHelper.parseXml("<Status/>"));
		newStatus.addElement("Code").setTextContent(status.code());
		newStatus.addElement("Message").setTextContent(status.message());

		if (elmStatus != null)
			header.replaceElement(".", elmStatus, newStatus.getBaseXml());
		else
			header.addElement(newStatus.getBaseXml());
	}

	public DSFault getDSFault() {
		XmlHelper header = new XmlHelper(getHeader());

		Element fault = header.getElement("DSFault/Fault");

		if (fault != null)
			return new DSFault(fault);
		else
			return null;

	}

	/**
	 * 
	 * @param dsfault
	 *            要設定的 DSFault 物件，傳入 Null 代表將所有的 Fault 資訊刪除。
	 * @return
	 */
	public void setFault(DSFault fault) {
		XmlHelper header = new XmlHelper(getHeader());

		header.getAsHelper("DSFault").removeAll();
		if (fault == null) {
			setStatus(DSStatus.Successful());
			return;
		}

		// 到這行表示一定有 Fault 發生，所以將 Status 設成UnhandledException。
		//上面這行理論是錯的, 因為 UDS 和 Service 的 Exception 都不會是 UnhandledException, 所以註解掉
		//setStatus(DSStatus.UnhandledException());

		XmlHelper dsfaultNode = header.getAsHelper("DSFault");

		DSFault next = fault;
		while (next != null) {

			Element faultElement = generateFaultElement(next);
			dsfaultNode.addElement(faultElement);

			next = next.getInnerFault();
		}
	}

	private Element generateFaultElement(DSFault fault) {
		XmlHelper result = new XmlHelper("Fault");

		result.addElement(".", "Source", fault.getSource());
		result.addElement(".", "Code", fault.getCode());
		result.addElement(".", "Message", fault.getMessage());
		result.addElement(".", "Detail", fault.getDetail());

		return result.getBaseXml();
	}

	private void InitialHeader() {
		XmlHelper header = new XmlHelper(getHeader());
		header.addElement("Status");
		header.addElement("Status", "Code", "0"); // 0 代表正常。
		header.addElement("Status", "Message");
		header.addElement("DSFault");
	}
}
