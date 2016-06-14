package tw.com.ischool.fireflylite.util.dsutil.accesspoint;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tw.com.ischool.fireflylite.util.dsutil.ContractConnection;
import tw.com.ischool.fireflylite.util.dsutil.DSRequest;
import tw.com.ischool.fireflylite.util.dsutil.DSResponse;
import tw.com.ischool.fireflylite.util.dsutil.DSAServiceException;
import tw.com.ischool.fireflylite.util.XmlUtil;
import tw.com.ischool.fireflylite.util.Converter;
import tw.com.ischool.fireflylite.util.http.Cancelable;


public class AccessPoint extends AbstractAccessPoint {
	public static final String DEFAULT_USER_NAME = "dad";
	public static final String DEFAULT_APPLICATION_PUBLIC_KEY_URL = "http://dsns.ischool.com.tw/dsns/dsns/info/Public.GetApplicationPublicKey";
	public static final String DEFAULT_DSNS_URL = "http://dsns.ischool.com.tw/dsns/dsns/dsns/";
	// public static final String DEFAULT_DSNS_URL =
	// "http://127.0.0.1/dsns/shared";
	public static final String SERVICE_GET_ACCESSPOINT = "AccessPoint.Get";
	public static final String SERVICE_REVERSE = "AccessPoint.Reverse";

	public static final String SERVICE_GET_DOORWAY_URL = "DS.NameService.GetDoorwayURL";

	public static final String CODE_REQUEST_ERROR = "401";
	public static final String CODE_DISABLED = "402";
	public static final String CODE_SQL_ERROR = "403";
	public static final String CODE_NOT_FOUND = "404";
	public static final String CODE_MISSING_PARAM = "405";

	private AccessPoint() {
		super();
	}

	public static IAccessPoint load(Element source) {
		AccessPoint ap = new AccessPoint();
		ap._name = source.getAttribute("Name").toLowerCase(Locale.getDefault()); 
		ap._active = Converter.toBoolean(source.getAttribute("Active"), true);
		ap._caption = XmlUtil.getElementText(source, "Caption");
		ap._memo = XmlUtil.getElementText(source, "Memo");
		ap._catalog = XmlUtil.getElementText(source, "Catalog");

		if (ap.getName().isEmpty())
			throw new AccessPointException(CODE_MISSING_PARAM,
					"Missing name attribute.");

		Element urlsElement = XmlUtil.selectElement(source, "RelativeURLs");
		for (Element url : XmlUtil.selectElements(urlsElement, "URL")) {
			ap._relativeURLs.add(url.getTextContent());
		}

		return ap;
	}

	public synchronized static IAccessPoint lookup(String accesspoint) {
		return lookup(DEFAULT_DSNS_URL, accesspoint);
	}

	public synchronized static IAccessPoint lookup(String dsns,
			String accesspoint) {
		return lookup(DEFAULT_DSNS_URL, SERVICE_GET_ACCESSPOINT, accesspoint);
	}

	public synchronized static IAccessPoint lookup(String dsns, String service,
			String accesspoint) {
		ContractConnection cc = new ContractConnection(dsns, "dsns");
		// cc.connect();
		Element req = XmlUtil.createElement("AccessPoint");
		req.setTextContent(accesspoint.toLowerCase(Locale.getDefault()));

		DSRequest request = new DSRequest();
		request.setContent(req);

		DSResponse response;
		try {
			response = cc.sendRequest(service, request, new Cancelable());
		} catch (DSAServiceException ex) {
			String error = handleError(ex);
			throw new RuntimeException(error, ex);
		}
		Element rsp = response.getContent();
		return load(rsp);
	}

	public synchronized static String getDoorwayURL(String accesspoint) {
		Element e = XmlUtil.createElement("a");
		e.setTextContent(accesspoint);
		DSRequest req = new DSRequest();
		req.setContent(e);

		ContractConnection cc = new ContractConnection(DEFAULT_DSNS_URL, "dsns");
		DSResponse rsp = cc.sendRequest(SERVICE_GET_DOORWAY_URL, req,
				new Cancelable());
		e = rsp.getContent();
		return e.getTextContent();
	}

	public static String reverseURL(String dsnsURL, String url) {
		List<String> list = reverseAllURL(dsnsURL, url);
		if (list.size() == 0)
			return "";
		return list.get(0);
	}

	public static List<String> reverseAllURL(String dsnsURL, String url) {
		ArrayList<String> list = new ArrayList<String>();

		Element rsp = reverse(dsnsURL, url, false);
		for (Element apElement : XmlUtil.selectElements(rsp, "AccessPoint")) {
			list.add(apElement.getTextContent());
		}

		return list;
	}

	public static IAccessPoint reverseDetail(String dsnsURL, String url) {
		List<IAccessPoint> aps = reverseAllDetails(dsnsURL, url);
		if (aps.size() == 0)
			return null;

		return aps.get(0);
	}

	public static List<IAccessPoint> reverseAllDetails(String dsnsURL,
			String url) {
		ArrayList<IAccessPoint> list = new ArrayList<IAccessPoint>();

		Element rsp = reverse(dsnsURL, url, true);
		for (Element apElement : XmlUtil.selectElements(rsp, "AccessPoint")) {
			IAccessPoint ap = load(apElement);
			list.add(ap);
		}

		return list;
	}

	public static Element toRawXML(IAccessPoint accesspoint) {
		Element e = XmlUtil.createElement("AccessPoint");
		e.setAttribute("Name", accesspoint.getName());
		e.setAttribute("Active", String.valueOf(accesspoint.isActive()));

		XmlUtil.addElement(e, "Caption", accesspoint.getCaption());
		XmlUtil.addElement(e, "Catalog", accesspoint.getCatalog());
		XmlUtil.addCDATASection(e, "Memo", accesspoint.getMemo());
		Element ruElement = XmlUtil.addElement(e, "RelativeURLs");

		for (String url : accesspoint.getRelativeURLs())
			XmlUtil.addElement(ruElement, "URL", url);

		return e;
	}

	public static Element toXML(IAccessPoint accesspoint) {
		Element e = XmlUtil.createElement("AccessPoint");
		e.setAttribute("Name", accesspoint.getName());
		e.setAttribute("Active", String.valueOf(accesspoint.isActive()));

		XmlUtil.addElement(e, "Caption", accesspoint.getCaption());
		XmlUtil.addCDATASection(e, "Memo", accesspoint.getMemo());

		Element ruElement = XmlUtil.addElement(e, "RelativeURLs");

		for (String url : accesspoint.getRelativeURLs())
			XmlUtil.addElement(ruElement, "URL", url);

		return e;
	}

	private static String handleError(DSAServiceException ex) {
		String error = "";
		if (ex.getCode().equalsIgnoreCase(CODE_DISABLED))
			error = "The Accesspoint was disabled.";
		else if (ex.getCode().equalsIgnoreCase(CODE_NOT_FOUND))
			error = "Accesspoint not found.";
		else if (ex.getCode().equalsIgnoreCase(CODE_REQUEST_ERROR))
			error = "Accesspoint request document was invalid.";
		else if (ex.getCode().equalsIgnoreCase(CODE_SQL_ERROR))
			error = "Accesspoint occured a sql exception.";
		else
			error = ex.getMessage();
		return error;
	}

	private static Element reverse(String dsnsURL, String url, boolean detail) {
		ContractConnection cc = new ContractConnection(dsnsURL, "dsns");
		Element req = XmlUtil.createElement("Request");
		XmlUtil.addElement(req, "URL", url);
		XmlUtil.addElement(req, "Detail", String.valueOf(detail));

		DSRequest request = new DSRequest();
		request.setContent(req);

		DSResponse response;
		try {
			response = cc.sendRequest(SERVICE_REVERSE, request,
					new Cancelable());
		} catch (DSAServiceException ex) {
			String error = handleError(ex);
			throw new RuntimeException(error, ex);
		}
		return response.getContent();
	}
}
