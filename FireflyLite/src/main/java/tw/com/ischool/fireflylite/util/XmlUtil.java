package tw.com.ischool.fireflylite.util;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import tw.com.ischool.fireflylite.util.http.HttpUtil;

public class XmlUtil {

	/***************************************************************************
	 * 選取出此節點中的子節點
	 * 
	 * @param source
	 *            : {@link Element} 目標節點
	 * @param elementName
	 *            : {@link String} 節點名稱
	 * @return {@link Element} 符合條件的第一個節點
	 **************************************************************************/
	public static Element selectElement(Element source, String elementName) {
		if (source == null)
			return null;

		if (elementName.equals("") || elementName.equals(".")
				|| elementName.equalsIgnoreCase("./"))
			return source;

		NodeList ns = source.getChildNodes();

		if (ns == null)
			return null;

		for (int i = 0; i < ns.getLength(); i++) {
			Node n = ns.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (n.getNodeName().equalsIgnoreCase(elementName)) {
				return (Element) n;
			}
		}
		return null;
	}

	/***************************************************************************
	 * 選取出此節點中的所有子節點
	 * 
	 * @param source
	 *            : {@link Element} 目標節點
	 * @param elementName
	 *            : {@link String} 節點名稱
	 * @return {@link Element} 符合條件的所有節點
	 **************************************************************************/
	public static List<Element> selectElements(Element source,
			String elementName) {
		ArrayList<Element> list = new ArrayList<Element>();

		if (source == null)
			return list;

		if (elementName.equals(".")) {
			list.add(source);
			return list;
		}

		for (int i = 0; i < source.getChildNodes().getLength(); i++) {
			Node n = source.getChildNodes().item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (n.getNodeName().equalsIgnoreCase(elementName)) {
				list.add((Element) n);
			}
		}
		return list;
	}

	/***************************************************************************
	 * 憑空建立建點
	 * 
	 * @param elementName
	 *            : {@link String} 節點名稱
	 * @return {@link Element} 建立好的節點
	 **************************************************************************/
	public static Element createElement(String elementName) {
		try {
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			Element e = doc.createElement(elementName);
			Node node = doc.appendChild(e);
			return (Element) node;
		} catch (Exception ex) {
			return null;
		}
	}

	/***************************************************************************
	 * 加入子節點
	 * 
	 * @param element
	 *            : {@link Element} 目標節點
	 * @param subElement
	 *            : {@link Element} 欲加入之子節點
	 * @return {@link Element} 加入後的子節點
	 **************************************************************************/
	public static Element appendElement(Element element, Element subElement) {
		if (subElement == null || element == null)
			return null;

		Document doc = element.getOwnerDocument();
		Document doc2 = subElement.getOwnerDocument();

		Node node;
		if (!doc.equals(doc2))
			node = doc.importNode(subElement.cloneNode(true), true);
		else
			node = subElement;

		return (Element) element.appendChild(node);
	}

	/***************************************************************************
	 * 判斷是否含有指定節點名稱之節點
	 * 
	 * @param source
	 *            : {@link Element} 目標節點
	 * @param elementName
	 *            : 節點名稱
	 * @return 是否含有指定名稱之節點
	 **************************************************************************/
	public static boolean hasElement(Element source, String elementName) {
		if (source == null)
			return false;

		if (elementName.equals("."))
			return true;

		for (int i = 0; i < source.getChildNodes().getLength(); i++) {
			Node n = source.getChildNodes().item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (n.getNodeName().equalsIgnoreCase(elementName)) {
				return true;
			}
		}
		return false;
	}

	/***************************************************************************
	 * 取出節點值
	 * 
	 * @param source
	 *            : {@link Element} 目標節點
	 * @param elementName
	 *            : 節點名稱
	 * @return 節點值, 若無該節點則傳回空字串""
	 **************************************************************************/
	public static String getElementText(Element source, String elementName) {
		Element element = selectElement(source, elementName);

		if (element == null)
			return "";

		return element.getTextContent();
	}

	/***************************************************************************
	 * 取出所有子節點
	 * 
	 * @param source
	 *            : {@link Element} 目標節點
	 * @return {@link List} 所有子節點
	 **************************************************************************/
	public static List<Element> selectElements(Element source) {
		ArrayList<Element> list = new ArrayList<Element>();

		if (source == null)
			return list;

		for (int i = 0; i < source.getChildNodes().getLength(); i++) {
			Node n = source.getChildNodes().item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE)
				continue;

			list.add((Element) n);
		}
		return list;
	}

	/**
	 * 取得第一個子項Element節點
	 * 
	 * @param source
	 *            : {@link Element} 來源節點
	 * **/
	public static Element selectFirstElement(Element source) {
		if (source == null)
			return null;

		for (int i = 0; i < source.getChildNodes().getLength(); i++) {
			Node n = source.getChildNodes().item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE)
				return (Element) n;
		}
		return null;
	}

	/**
	 * 依屬性名稱取得符合節點集合
	 * 
	 * @param source
	 *            : {@link Element} 來源節點
	 * @param elementName
	 *            : 節點名稱
	 * @param attributeName
	 *            : 判斷屬性名稱
	 * @param value
	 *            : 屬性期待值
	 * **/
	public static List<Element> selectElementsByAttribute(Element source,
			String elementName, String attributeName, String value) {
		List<Element> list = new ArrayList<Element>();

		if (source == null)
			return list;

		for (int i = 0; i < source.getChildNodes().getLength(); i++) {
			Node n = source.getChildNodes().item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (!n.getNodeName().equals(elementName))
				continue;

			Element e = (Element) n;
			if (!e.hasAttribute(attributeName))
				continue;

			if (!e.getAttribute(attributeName).equals(value))
				continue;

			list.add(e);
		}
		return list;
	}

	/**
	 * 依屬性名稱取得符合節點
	 * 
	 * @param source
	 *            : {@link Element} 來源節點
	 * @param elementName
	 *            : 節點名稱
	 * @param attributeName
	 *            : 判斷屬性名稱
	 * @param value
	 *            : 屬性期待值
	 * **/
	public static Element selectElementByAttribute(Element source,
			String elementName, String attributeName, String value) {
		return selectElementByAttribute(source, elementName, attributeName,
				value, false);
	}

	/**
	 * 依屬性名稱取得符合節點
	 * 
	 * @param source
	 *            : {@link Element} 來源節點
	 * @param elementName
	 *            : 節點名稱
	 * @param attributeName
	 *            : 判斷屬性名稱
	 * @param value
	 *            : 屬性期待值
	 * @param ignoreCase
	 *            : 不分大小寫
	 * **/
	public static Element selectElementByAttribute(Element source,
			String elementName, String attributeName, String value,
			boolean ignoreCase) {
		if (source == null)
			return null;

		if (value == null)
			return null;

		for (int i = 0; i < source.getChildNodes().getLength(); i++) {
			Node n = source.getChildNodes().item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (!n.getNodeName().equals(elementName))
				continue;

			Element e = (Element) n;
			if (!e.hasAttribute(attributeName))
				continue;

			String attrValue = e.getAttribute(attributeName);
			if (ignoreCase) {
				attrValue = attrValue.toLowerCase();
				value = value.toLowerCase();
			}

			if (!attrValue.equals(value))
				continue;
			return e;
		}
		return null;
	}

	public static Element addElement(Element source, String newElementName,
			String content) {
		Element element = addElement(source, newElementName);
		element.setTextContent(content);
		return element;
	}

	public static Element addElement(Element source, String newElementName) {
		Element newElement = source.getOwnerDocument().createElement(
				newElementName);
		Node node = source.appendChild(newElement);
		return (Element) node;
	}

	public static void removeElements(Element source, String targetElementName) {
		if (source == null)
			return;
		for (Element e : selectElements(source, targetElementName)) {
			source.removeChild(e);
		}
	}

	public static void removeChilds(Element source) {
		if (source == null)
			return;
		for (int i = 0; i < source.getChildNodes().getLength(); i++) {
			Node node = source.getChildNodes().item(i);
			source.removeChild(node);
		}
	}

	public static void setElementText(Element source, String targetElementName,
			String text) {
		if (source == null)
			return;

		Element targetElement = selectElement(source, targetElementName);

		if (targetElement == null) {
			targetElement = addElement(source, targetElementName);
		}

		targetElement.setTextContent(text);
	}

	public static void setElement(Element source, String targetElementName,
			Element newElement) {
		if (source == null)
			return;
		removeElements(source, targetElementName);
		appendElement(source, newElement);
	}

	public static Element addCDATASection(Element source, String elementName,
			String content) {
		Element element = addElement(source, elementName);
		CDATASection cdata = element.getOwnerDocument().createCDATASection(
				content);
		element.appendChild(cdata);
		return element;
	}

	public static Element loadURL(String urlString) throws IOException {
		// Element element;
		// URLConnection uc = new URL(urlString).openConnection();
		// uc.setConnectTimeout(100000);
		// uc.setDoOutput(true);
		// InputStream in = new BufferedInputStream(uc.getInputStream());
		//
		// element = XmlHelper.parseXml(in);
		//
		// in.close();

		// return element;

		return HttpUtil.getElement(urlString);
	}

	public static Element loadURL(String urlString, String username,
			String password) throws IOException {

		InputStream in = null;
		try {
			in = HttpUtil.getInputStream(urlString, username, password);
		} catch (KeyManagementException e) {
			throw new IOException("KeyManagementException", e);
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("NoSuchAlgorithmException", e);
		}
		Element element = XmlHelper.parseXml(in);
		in.close();
		return element;
	}

	/**
	 * 取得指定節點屬性, 若查無指定節點屬性則傳回空字串
	 *
	 * @param source
	 *            : 來源節點
	 * @param elementName
	 *            : 指定子節點名稱
	 * @param attributeName
	 *            : 指定屬性名稱
	 * **/
	public static String getAttribute(Element source, String elementName,
			String attributeName) {
		Element targetElement = selectElement(source, elementName);
		if (targetElement == null)
			return "";
		return targetElement.getAttribute(attributeName);
	}

	/**
	 * 複製一份節點, 置於一個乾淨的文件中
	 *
	 * @param source
	 *            : 來源節點
	 * @return 新文件的 DocumentElement
	 * **/
	public static Element copyElement(Element source) {
		Node clone = source.cloneNode(true);
		Document doc;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			clone = doc.importNode(clone, true);
			doc.appendChild(clone);
		} catch (ParserConfigurationException e) {
			return null;
		}
		return doc.getDocumentElement();
	}

	public static Element replaceElement(Element oldElement, Element newElement){
		Document oldDoc = oldElement.getOwnerDocument();
		Document newDoc = newElement.getOwnerDocument();

		if(!newDoc.equals(oldDoc)){
			newElement = (Element)newElement.cloneNode(true);
			newElement = (Element)oldDoc.importNode(newElement, true);
		}

		Node parent = oldElement.getParentNode();
		Node newNode;
		if(parent instanceof Document){
			oldDoc.removeChild(oldElement);
			newNode = oldDoc.appendChild(newElement);
		}else{
			parent.removeChild(oldElement);
			newNode = parent.appendChild(newElement);
		}
		return (Element)newNode;
	}

	public static void println(Element source) {
		if(source == null)
			System.out.println("source is null.");
		else
			System.out.println(XmlHelper.convertToString(source, true));
	}		

	public static void main(String[] args) {
		Element a = XmlUtil.createElement("aaa");
		//Element b = XmlUtil.addElement(a, "bbb","1234");
		Document d = a.getOwnerDocument();
		
		Element c = XmlUtil.createElement("ccc");
		XmlUtil.replaceElement(a, c);
		XmlUtil.println(d.getDocumentElement());
	}
}
