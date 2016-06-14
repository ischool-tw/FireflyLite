package tw.com.ischool.fireflylite.util.dsutil;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import tw.com.ischool.fireflylite.util.XmlHelper;
import tw.com.ischool.fireflylite.util.XmlUtil;

public abstract class DSEnvelope {

	public static final String HEADER = "Header";
	public static final String BODY = "Body";
	public static final String PARAMETERS = "Parameters";

	// protected XmlHelper envelope;
	protected Element envelope;

	// private Map<String, String> _parameters;

	public DSEnvelope() {
		envelope = XmlUtil.createElement("Envelope");
		XmlUtil.addElement(envelope, "Header");
		XmlUtil.addElement(envelope, "Body");

		// initParameter();
	}

	public DSEnvelope(String content) {

		try {
			envelope = XmlHelper.parseXmlWithException(content);
		} catch (Exception ex) {
			throw new IllegalArgumentException("解析 Xml  文件失敗。", ex);
		}

		// 檢查 stream 中的 Xml 資料格式是否正確。
//		SAXParseException result = ValidProvider.validateEnvelope(envelope);
//
//		if (result != null)
//			throw new IllegalArgumentException("不是合法的文件：" + result.getMessage());

		// initParameter();
	}

	public DSEnvelope(Element content) {

		envelope = content;

		// 檢查 stream 中的 Xml 資料格式是否正確。
//		SAXParseException result = ValidProvider.validateEnvelope(envelope);
//
//		if (result != null)
//			throw new IllegalArgumentException("不是合法的文件：" + result.getMessage());

		initParameter();
	}

	public DSEnvelope(InputStream stream) throws IOException, SAXException {
		envelope = XmlHelper.parseXml(stream);

		// 檢查 stream 中的 Xml 資料格式是否正確。
//		SAXParseException result = ValidProvider.validateEnvelope(envelope);
//
//		if (result != null)
//			throw new IllegalArgumentException("不是合法的文件：" + result.getMessage());

		initParameter();
	}

	public Element getHeader() {
		return XmlUtil.selectElement(envelope, HEADER);
	}

	public Element getBody() {
		return XmlUtil.selectElement(envelope, BODY);
	}

	public Element getContent() {
		Element body = getBody();
		return XmlUtil.selectFirstElement(body);
	}

	public synchronized void setContent(Element content) {

		Element body = getBody();
		XmlUtil.removeChilds(body);
		XmlUtil.appendElement(body, content);
	}

	public String getXML() {
		return XmlHelper.convertToString(envelope, false);
	}

	public void setParameter(String name, String value) {
		Element params = initParameter();
		XmlUtil.removeElements(params, name);

		XmlUtil.addCDATASection(params, name, value);
	}

	public void setParameters(Map<String, String> map) {
		Element params = initParameter();
		XmlUtil.removeChilds(params);

		for (String key : map.keySet()) {
			XmlUtil.addCDATASection(params, key, map.get(key));
		}
	}

	public String getParameter(String name) {
		Element p = this.getParamElement();
		return XmlUtil.getElementText(p, name);
	}

	public Map<String, String> getParameters() {
		Element paramsElement = this.getParamElement();

		HashMap<String, String> params = new HashMap<String, String>();
		for (Element p : XmlUtil.selectElements(paramsElement)) {
			params.put(p.getNodeName(), p.getTextContent());
		}

		return params;
	}

	public Element getParamElement() {
		Element h = this.getHeader();
		return XmlUtil.selectElement(h, PARAMETERS);
	}

	private Element initParameter() {
		Element header = this.getHeader();
		Element paramElement = this.getParamElement();

		if (paramElement == null)
			paramElement = XmlUtil.addElement(header, PARAMETERS);

		return paramElement;
	}

	public String toString() {
		return XmlHelper.convertToString(envelope, false);
	}
}
