package tw.com.ischool.fireflylite.util.dsutil;

import org.w3c.dom.Element;

import tw.com.ischool.fireflylite.util.XmlUtil;

public class DSApplicant {
	private String _clientid;
	private String _namespace;
	private String _displayName;
	private String _description;
	private String _contactEmail;

	public DSApplicant(String clientid, String namespace, String displayName, String description, String contactEmail) {
		this.init(clientid, namespace, displayName, description, contactEmail);
	}

	public DSApplicant(String clientid, String namespace, String displayName) {
		this.init(clientid, namespace, displayName, "", "");
	}

	private void init(String clientid, String namespace, String displayName, String description, String contactEmail) {
		_clientid = clientid;
		_namespace = namespace;
		_displayName = displayName;
		_description = description;
		_contactEmail = contactEmail;
	}

	public String getClientID() {
		return _clientid;
	}

	public String getNamespace() {
		return _namespace;
	}

	public String getDisplayName() {
		return _displayName;
	}

	public String getDescription() {
		return _description;
	}

	public String getContactEmail() {
		return _contactEmail;
	}

	public boolean isEmpty() {
		if (_clientid == null)
			return true;
		if (_clientid.isEmpty())
			return true;
		return false;
	}
	
	public static DSApplicant load(Element applicantElement){
		String clientid = XmlUtil.getElementText(applicantElement, "ClientID");
		String namespace = XmlUtil.getElementText(applicantElement, "Namespace");
		String displayName = XmlUtil.getElementText(applicantElement, "DisplayName");
		
		return new DSApplicant(clientid, namespace, displayName);
	}

	public static DSApplicant empty() {
		DSApplicant a = new DSApplicant("", "", "");
		return a;
	}
}
