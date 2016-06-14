package tw.com.ischool.fireflylite.util;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * 這是一個補助類別，讓處理 Xml 較為方便。
 * 
 * @author Huang Yao Ming
 */
public class XmlHelper {

	/**
	 * 此類別處理的主要 Element。
	 */
	protected Element element;

	/**
	 * 主要 Element 的 Document。
	 */
	protected Document origindoc;

	/**
	 * 使用 Content 當作根節點名稱建立物件實體。
	 */
	public XmlHelper() {
		try {
			Element target = parseXml("<Content/>");
			setBaseXml(target);
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	/**
	 * 依據指定的「根節點」名稱建立 XmlHelper 物件，不需要加上「<」、「/>」等字元。
	 * 
	 * @param rootName
	 *            Xml 文件的「根節點」名稱。
	 * @throws SAXException
	 *             當指定的「根節點」名稱不合法時，會產生此例外。
	 */
	public XmlHelper(String rootName) {
		try {
			Element target = parseXml("<" + rootName + "/>");
			setBaseXml(target);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public XmlHelper(Element elm) {
		setBaseXml(elm);
	}

	public XmlHelper(InputStream input) {
		Element target = parseXml(input);
		setBaseXml(target);
	}

	public XmlHelper(File file) {
		Element target = parseXml(file);
		setBaseXml(target);
	}

	/**
	 * 設定內部封裝的 Element 物件。
	 * 
	 * @param target
	 *            要封裝的 Element 物件。
	 */
	public void setBaseXml(Element target) {
		element = target;

		if (target != null)
			origindoc = target.getOwnerDocument();
	}

	/**
	 * 取得內部封裝的 Element 物件。
	 * 
	 * @return 此物件封裝的 Element 物件。
	 */
	public Element getBaseXml() {
		return element;
	}

	/**
	 * 新增 Sub Element，並封裝成 XmlHelper 回傳。
	 * 
	 * @param newName
	 * @return
	 */
	public XmlHelper addAsHelper(String newName) {
		return new XmlHelper(addElement(newName));
	}

	public XmlHelper addAsHelper(Element elm) {
		return new XmlHelper(addElement(elm));
	}

	public XmlHelper addAsHelper(String xpath, Element elm) {
		return new XmlHelper(addElement(xpath, elm));
	}

	public XmlHelper addAsHelper(String xpath, String newName) {
		return new XmlHelper(addElement(xpath, newName));
	}

	/**
	 * 在目前的 Element 下新增新的 Element。
	 * 
	 * @param newName
	 *            新 Element 名稱。
	 * @return
	 */
	public Element addElement(String newName) {
		try {
			return addElement(".", newName);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Element addElement(Element elm) {
		try {
			return addElement(".", elm);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 在指定的 XPath 路徑上新增 Sub Element。
	 * 
	 * @param xpath
	 *            XPath 運算式，運算結果必需是一備「Element」。
	 * @param elm
	 *            要被新增的 Element。
	 * @return 已新增到 Xml 文件中的 Element。
	 * @throws XPathExpressionException
	 */
	public Element addElement(String xpath, Element elm) {
		Element result;
		Element target = selectMustElement(xpath);

		if (origindoc == elm.getOwnerDocument())
			result = (Element) target.appendChild(elm);
		else {
			Element imported = (Element) origindoc.importNode(elm, true);
			result = (Element) target.appendChild(imported);
		}
		return result;
	}

	public Element addElement(String xpath, String newName) {
		Element result, newelement;
		Element target = selectMustElement(xpath);

		newelement = origindoc.createElement(newName);
		result = (Element) target.appendChild(newelement);

		return result;
	}

	public Element addElement(String xpath, String newName, String value) {
		Element result = addElement(xpath, newName);
		result.setTextContent(value);

		return result;
	}

	public Element addElement(String xpath, String newName, String value,
			boolean isXmlContent) {
		Element result = addElement(xpath, newName);

		if (isXmlContent) {
			Element anthor = parseXml(value);
			Element notanthor = (Element) origindoc.importNode(anthor, true);
			result.appendChild(notanthor);
		} else {
			result.setTextContent(value);
		}

		return result;
	}

	/**
	 * 新增一個 Sub Element ，並在此 Element 加入 CDataSection 資料。
	 * 
	 * @param xpath
	 *            XPath 運算式，運算式結果必需是「Element」。
	 * @param newElmName
	 *            新 Element 名稱，必需是合法的 Xml Element 名稱。
	 * @param value
	 *            要加入的資料，可以是任何文字資料。
	 * @throws XPathExpressionException
	 */
	public void addElementWithCData(String xpath, String newElmName,
			String value) {
		Element result = addElement(xpath, newElmName);

		CDATASection section = origindoc.createCDATASection(value);
		result.appendChild(section);
	}

	/**
	 * 新增所有 elements 裡的 Element ，但會略過 Attribute 與 CDataSection。
	 * 
	 * @param elements
	 */
	public void addElements(NodeList elements) {
		for (int i = 0; i < elements.getLength(); i++) {
			Node n = elements.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				addElement((Element) n);
			}
		}
	}

	/**
	 * 在根 Element 上加入屬性資料。
	 * 
	 * @param attName
	 *            屬性名稱。
	 * @param value
	 */
	public void setAttribute(String attName, String value) {
		try {
			setAttribute(".", attName, value);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 在指定的 Element 上增加 Attribute，如果有同名的 Attribute，則會被取代。
	 * 
	 * @param xpath
	 *            XPath 運算式，運算結果必需是 Element。
	 * @param attName
	 *            屬性名稱。
	 * @param value
	 *            屬性值。
	 * @throws XPathExpressionException
	 */
	public void setAttribute(String xpath, String attName, String value) {
		Element result = selectMustElement(xpath);
		Attr att = origindoc.createAttribute(attName);

		att.setTextContent(value);
		result.setAttributeNode(att);
	}

	public String getAttribute(String attName) {
		return getAttribute(".", attName);
	}

	/**
	 * 取得指定 Element 的屬性值，如果該屬性不存在則回傳空字串。
	 * 
	 * @param xpath
	 *            XPath 運算式，運算結果必需是 Element。
	 * @param attName
	 *            屬性名稱。
	 * @return 指定的屬性值。
	 * @throws XPathExpressionException
	 */
	public String getAttribute(String xpath, String attName) {
		Element result = selectMustElement(xpath);

		return result.getAttribute(attName);
	}

	/**
	 * 取得根 Element 下所有屬性資料。
	 * 
	 * @return
	 */
	public List<Attr> getAttributes() {
		return getAttributes(".");
	}

	/**
	 * 取得指定 Element 下的所有屬性。
	 * 
	 * @param xpath
	 *            XPath 運算式，運算結果必須是 Element。
	 * @return 屬性 List。
	 */
	public List<Attr> getAttributes(String xpath) {
		Element result = selectMustElement(xpath);
		NamedNodeMap attmap = result.getAttributes();

		List<Attr> attlist = new ArrayList<Attr>();
		for (int i = 0; i < attmap.getLength(); i++) {
			attlist.add((Attr) attmap.item(i));
		}
		return attlist;
	}

	/**
	 * 在指定的 Element 下加入 CData 資料，如果 CDataSection 已存在，則會取代。
	 * 
	 * @param xpath
	 * @param value
	 * @throws XPathExpressionException
	 */
	public void setCDATASection(String xpath, String value) {
		Element result = selectMustElement(xpath);

		CDATASection section = origindoc.createCDATASection(value);
		CDATASection originSection = null;

		for (int i = 0; i < result.getChildNodes().getLength(); i++) {
			Node node = result.getChildNodes().item(i);
			if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
				originSection = (CDATASection) node;
				break;
			}
		}

		if (originSection == null)
			result.appendChild(section);
		else
			result.replaceChild(section, originSection);
	}

	/**
	 * 設定根 Element 內的文字資料。
	 * 
	 * @param value
	 */
	// TODO 這裡要成正確的。
	public void setText(String value) {
		Text txt = origindoc.createTextNode(value);
		element.appendChild(txt);

		// element.setTextContent(value);
	}

	/**
	 * 設定指定 Element 內的文字資料。
	 * 
	 * @param xpath
	 * @param value
	 */
	public void setText(String xpath, String value) {
		Element elm = selectMustElement(xpath);
		elm.setTextContent(value);
	}

	public Element getFirstElement() {
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			Node each = element.getChildNodes().item(i);

			if (each.getNodeType() == Node.ELEMENT_NODE)
				return (Element) each;
		}

		return null;
	}

	public Element getElement(String xpath) {
		try {

			if (xpath.equals("."))
				return element;

			return selectElement(element, xpath);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 根節點以下的所有 Sub Element。
	 * 
	 * @return Sub Element 的 List，如果沒有任何子節點則回傳空的List。
	 */
	public List<Element> getElements() {
		try {
			List<Element> elements = new ArrayList<Element>();
			NodeList nodes = element.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					elements.add((Element) node);
				}
			}
			return elements;
		} catch (Exception ex) {
			return new ArrayList<Element>();
		}
	}

	public List<Element> getElements(String xpath) {
		try {
			return selectElements(element, xpath);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 取得指定 Element 下的文字，如果 Element 不存在則回傳空字串「""」。
	 * 
	 * @param xpath
	 *            XPath 運算式，運算結果必需須是 Element。
	 * @return Element 的內含文字。
	 * @throws XPathExpressionException
	 */
	public String getElementText(String xpath) {
		Element result = selectElement(element, xpath);

		if (result == null)
			return "";
		else
			return result.getTextContent();
	}

	/**
	 * 取得指定的 Element，並封裝成 XmlHelper 物件。如果 Element 不存在則回傳 Null。
	 * 
	 * @return
	 */
	public XmlHelper getAsHelper(String xpath) {
		Element result = getElement(xpath);

		if (result != null)
			return new XmlHelper(result);
		else
			return null;
	}

	/**
	 * 取得根 Element 下的所有 Sub Element，並封裝成 XmlHelper 物件。如果 Element 不存在則回傳 Null。
	 * 
	 * @return
	 */
	public List<XmlHelper> getAsHelpers() {
		List<Element> list = getElements();
		List<XmlHelper> helpers = new ArrayList<XmlHelper>();

		for (Element elm : list) {
			helpers.add(new XmlHelper(elm));
		}

		return helpers;
	}

	public List<XmlHelper> getAsHelpers(String xpath) {
		List<Element> list = getElements(xpath);
		List<XmlHelper> helpers = new ArrayList<XmlHelper>();

		for (Element elm : list) {
			helpers.add(new XmlHelper(elm));
		}

		return helpers;
	}

	/**
	 * 判斷指定的 Element 是否存在。
	 * 
	 * @param xpath
	 *            XPath 運算式，運算式結果必須是 Element。
	 * @return
	 */
	public boolean hasElement(String xpath) {
		try {
			// 沒有錯誤的話就是存在。
			selectMustElement(xpath);

			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 判斷指定的 Attribute 是否存在。
	 * 
	 * @param attName
	 * @return
	 */
	public boolean hasAttribute(String attName) {
		return hasAttribute(".", attName);
	}

	/**
	 * 判斷指定的 Attribute 是否存在。
	 * 
	 * @param xpath
	 *            XPath 運算式，運算式結果必須是 Element。
	 * @param attName
	 *            屬性名稱。
	 * @return
	 */
	public boolean hasAttribute(String xpath, String attName) {
		Element target = selectMustElement(xpath);

		return (target.getAttributeNode(attName) != null);
	}

	public void removeAttribute(String attrName) {
		removeAttribute(".", attrName);
	}

	public void removeAttribute(String xpath, String attrName) {
		Element result = selectElement(element, xpath);

		result.removeAttribute(attrName);
	}

	public Element removeElement(Element elm) {
		if (elm.getParentNode() == null)
			return null;
		else if (elm.getOwnerDocument() != origindoc)
			return null;
		else {
			Node parent = elm.getParentNode();
			return (Element) parent.removeChild(elm);
		}
	}

	public Element removeElement(String elmName) {
		return removeElement(".", elmName);
	}

	public Element removeElement(String xpath, String elmName) {
		Element toberemove = null;
		Element parent = selectMustElement(xpath);
		XmlHelper objParent = new XmlHelper(parent);

		toberemove = objParent.getElement(elmName);

		return (Element) parent.removeChild(toberemove);
	}

	/**
	 * 移除指定 Element 的所有子 Element。
	 * 
	 * @param xpath
	 */
	public void removeElementChildren(String xpath) {
		Element oldChild = getElement(xpath);

		if (oldChild == null)
			throw new RuntimeException("指定的元素不存在。(XPath：" + xpath + ")");

		Node parent = oldChild.getParentNode();

		if (parent.getNodeType() == Node.ELEMENT_NODE) {
			Node newChild = oldChild.cloneNode(false);
			parent.replaceChild(newChild, oldChild);
		} else if (parent.getNodeType() == Node.DOCUMENT_NODE) {
			removeAll();
		} else
			throw new RuntimeException("無法移除子節點，因為沒有父節點。");

	}

	/**
	 * 移除所有子 Element ，不包含屬性。
	 * 
	 */
	public void removeAll() {
		for (Element elm : getElements()) {
			removeElement(elm);
		}
	}

	/**
	 * 取代指定的 Sub Element。
	 * 
	 * @param xpath
	 *            XPath 運算式，運算結果必須是 Element。
	 * @param oldElementName
	 *            要被取代的 Element 名稱。
	 * @param newElement
	 *            要取代的 Element。
	 * @throws IllegalArgumentException
	 *             如果 oldElementName 指定的 Element 不存在時產生。
	 */
	public void replaceElement(String xpath, String oldElementName,
			Element newElement) throws IllegalArgumentException {
		try {
			Element parent = selectMustElement(xpath);
			Element tobereplace = selectElement(parent, oldElementName);

			if (tobereplace == null) {
				throw new IllegalArgumentException(
						"oldElementName 參數錯誤，指定的 Element 不存在。");
			} else {
				Node node = origindoc.importNode(newElement, true);
				parent.replaceChild(node, tobereplace);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public void replaceElement(String xpath, Element oldElement,
			Element newElement) throws IllegalArgumentException {

		Element parent = selectMustElement(xpath);

		if (oldElement == null) {
			parent.removeChild(oldElement);

		} else if (oldElement.getOwnerDocument() != origindoc) {
			throw new IllegalArgumentException(
					"oldElement 參數所指定的 Element 存在於另一個 Xml 文件中。");
		} else {
			Node node = origindoc.importNode(newElement, true);
			parent.replaceChild(node, oldElement);
		}
	}

	public XmlHelper copy() {
		return new XmlHelper((Element) element.cloneNode(true));
	}

	public void serialTo(File target, boolean formatContent) {
		serialXmlTo(element, target, formatContent);
	}

	public void serialTo(OutputStream target, boolean formatContent) {
		serialXmlTo(element, target, formatContent);
	}

	/**
	 * 將內部的 Xml 物件轉換成 Xml String。
	 * 
	 * @param formatContent
	 *            指示輸出的 Xml 字串是否要進行格式化。
	 * @return Xml 字串。
	 */
	public String toString(boolean formatContent) {
		try {
			return convertToString(element, formatContent);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	/**
	 * Must的意思就是這個 Element 必需存在。
	 * 
	 * @throws XPathExpressionException
	 */
	private Element selectMustElement(String xpath) {
		try {
			Element target = getElement(xpath);

			if (target == null)
				throw new IllegalArgumentException("xpath 參數錯誤，找不到指定的 Element。");

			if (target.getNodeType() == Node.ATTRIBUTE_NODE)
				throw new IllegalArgumentException("xpath 參數錯誤，不可以是 Attribute。");

			return target;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	// protected static DocumentBuilder docbuilder;

	// protected static XPathFactory xpathfactory;

	protected static Hashtable<String, XPathExpression> xpathexpress;

	static {

		DocumentBuilderFactory builderfactory = DocumentBuilderFactory
				.newInstance();

		builderfactory.setNamespaceAware(true);
		builderfactory.setIgnoringElementContentWhitespace(false);

		// xpathfactory = XPathFactory.newInstance();
		// docbuilder = builderfactory.newDocumentBuilder();
		xpathexpress = new Hashtable<String, XPathExpression>();
	}

	public static String convertToString(Element source) {
		return convertToString(source, false);
	}

	public static String convertToString(Element source, boolean formatContent) {

		String result = "";
		DOMSource domSource = new DOMSource(source);
		Transformer transformer;

		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			StringWriter sw = new StringWriter();
			StreamResult sr = new StreamResult(sw);
			transformer.transform(domSource, sr);
			result = (sw.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

		/*
		 * 
		 * Element srcelm = source;
		 * 
		 * 
		 * 
		 * DocumentBuilder docbuilder = null; try { docbuilder =
		 * 
		 * DocumentBuilderFactory.newInstance() .newDocumentBuilder(); } catch
		 * 
		 * (ParserConfigurationException e) {
		 * 
		 * 
		 * 
		 * }
		 * 
		 * 
		 * 
		 * DOMImplementationLS ls = (DOMImplementationLS) docbuilder
		 * 
		 * .getDOMImplementation();
		 * 
		 * 
		 * 
		 * LSSerializer serial = ls.createLSSerializer(); DOMConfiguration
		 * 
		 * config = serial.getDomConfig();
		 * 
		 * 
		 * 
		 * if (formatContent) { // 格式化 XML 資料
		 * 
		 * //config.setParameter(Constants.DOM_FORMAT_PRETTY_PRINT, true);
		 * 
		 * 
		 * 
		 * }
		 * 
		 * 
		 * 
		 * // 不加第一行的 XML 宣告 //config.setParameter(Constants.DOM_XMLDECL, false);
		 * 
		 * config.setParameter(Constants.CONTENT_DIRECTORY, false);
		 * 
		 * 
		 * 
		 * return serial.writeToString(srcelm);
		 */

	}

	public static void serialXmlTo(Element source, File target,
			boolean formatContent) {
		try {
			OutputStream out = new FileOutputStream(target);
			serialXmlTo(source, out, formatContent);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void serialXmlTo(Element source, OutputStream target,
			boolean formatContent) {
		try {
			OutputStreamWriter output = new OutputStreamWriter(target, "utf-8");
			output.write(convertToString(source, formatContent));
			output.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Element parseXml(String xmlContent) {
		try {
			Element srcelm;
			InputSource is = new InputSource(new StringReader(xmlContent));

			DocumentBuilder docbuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			srcelm = docbuilder.parse(is).getDocumentElement();
			return srcelm;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static Element parseXmlWithException(String xmlContent)
			throws Exception {
		try {
			Element srcelm;
			// InputSource is = new InputSource(new StringReader(xmlContent));
			
			InputSource is = new InputSource(xmlContent);
			is.setCharacterStream(new StringReader(xmlContent));
			is.setEncoding("UTF-8");

			DocumentBuilder docbuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			srcelm = docbuilder.parse(is).getDocumentElement();

			return srcelm;

		} catch (Exception ex) {
			throw ex;
		}
	}

	public static Element parseXml(File file) {
		try {
			InputSource is = new InputSource(new FileInputStream(file));
			DocumentBuilder docbuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			return docbuilder.parse(is).getDocumentElement();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Element parseXml(InputStream in) {
		try {
			// InputStreamReader reader = new InputStreamReader(in, "utf-8");
			InputSource is = new InputSource(in);

			DocumentBuilder docbuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			return docbuilder.parse(is).getDocumentElement();
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public static Element selectElement(Element srcElement,
			String xpathExpression) {

		if (xpathExpression.equals(""))
			xpathExpression = ".";

		try {
			// XPathExpression xpath = null;

			// if (xpathexpress.containsKey(xpathExpression)) {
			// xpath = xpathexpress.get(xpathExpression);
			// } else {
			XPath xp = XPathFactory.newInstance().newXPath();
			// xpath = xp.compile(xpathExpression);

			// xpathexpress.put(xpathExpression, xpath);
			// }

			// Element result = (Element) xpath.evaluate(srcElement,
			// XPathConstants.NODE);
			Element result = (Element) xp.evaluate(xpathExpression, srcElement,
					XPathConstants.NODE);

			if (result == null)
				return null;

			short nodeType = result.getNodeType();
			if (nodeType == Node.ELEMENT_NODE)
				return result;
			else
				return null;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static List<Element> selectElements(Element srcElement,
			String xpathExpression) {
		try {
			XPathExpression xpath = null;

			if (xpathexpress.containsKey(xpathExpression)) {
				xpath = xpathexpress.get(xpathExpression);
			} else {
				XPath xp = XPathFactory.newInstance().newXPath();
				xpath = xp.compile(xpathExpression);
				xpathexpress.put(xpathExpression, xpath);
			}

			NodeList nodelist = (NodeList) xpath.evaluate(srcElement,
					XPathConstants.NODESET);

			return wrapToList(nodelist);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 將 Xml Element 中的空白 Node 移除。
	 * 
	 * @param target
	 */
	public static void removeWhitespace(Element target) {
		if (target.getChildNodes().getLength() > 1) {
			Node each;
			for (int i = target.getChildNodes().getLength() - 1; i >= 0; i--) {
				each = target.getChildNodes().item(i);

				if (each.getNodeType() == Node.TEXT_NODE) {

					// 清除所有空白後變成空字串，就將此 TextNode 移除。
					// 「\s」 是 Regulare Expression 語法。
					if (each.getTextContent().replaceAll("\\s", "").equals(""))
						target.removeChild(each);

				} else {
					if (each.getNodeType() == Node.ELEMENT_NODE)
						removeWhitespace((Element) each);
				}
			}
		}
	}

	public static List<Element> wrapToList(NodeList nodes) {

		List<Element> nodearray = new ArrayList<Element>();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			short nodeType = node.getNodeType();
			if (nodeType == Node.ELEMENT_NODE)
				nodearray.add((Element) node);
		}

		return nodearray;
	}
}
