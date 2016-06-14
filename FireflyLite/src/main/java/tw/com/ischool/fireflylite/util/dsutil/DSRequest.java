package tw.com.ischool.fireflylite.util.dsutil;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import tw.com.ischool.fireflylite.util.XmlUtil;

public class DSRequest extends DSEnvelope {

	public DSRequest() {
		super();

		Element header = this.getHeader();
		XmlUtil.addElement(header, "SecurityToken");
		XmlUtil.addElement(header, "TargetContract");
		XmlUtil.addElement(header, "TargetService");
	}

	public DSRequest(String content) {
		super(content);
	}

	public DSRequest(InputStream stream) throws SAXException, IOException {
		super(stream);
	}

	public String getTargetService() {
		Element header = this.getHeader();
		return XmlUtil.getElementText(header, "TargetService");
	}

	public String getTargetContract() {
		Element header = this.getHeader();
		return XmlUtil.getElementText(header, "TargetContract");
	}

	public void setTargetService(String targetService) {
		Element header = this.getHeader();
		XmlUtil.setElementText(header, "TargetService", targetService);
	}

	public void setTargetContract(String targetContract) {
		Element header = this.getHeader();
		XmlUtil.setElementText(header, "TargetContract", targetContract);
	}

	public Element getSecurityToken() {
		Element header = this.getHeader();
		return XmlUtil.selectElement(header, "SecurityToken");
	}

	public void setSecurityToken(Element securityToken) {
		Element header = this.getHeader();
		XmlUtil.setElement(header, "SecurityToken", securityToken);
	}

	public void setUserAgent(Element userAgentElement) {
		Element header = this.getHeader();
		XmlUtil.setElement(header, "UserAgent", userAgentElement);
	}

	public Element getUserAgent() {
		Element header = this.getHeader();
		return XmlUtil.selectElement(header, "UserAgent");
	}

	public String getUserAgentName() {
		Element e = getUserAgent();
		if (e == null)
			return null;

		return e.getAttribute("Name");
	}
}
