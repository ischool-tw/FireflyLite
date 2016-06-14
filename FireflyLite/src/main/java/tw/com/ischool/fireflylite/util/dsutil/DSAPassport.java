package tw.com.ischool.fireflylite.util.dsutil;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tw.com.ischool.fireflylite.util.XmlUtil;
import tw.com.ischool.fireflylite.util.Converter;
import tw.com.ischool.fireflylite.util.PropertyUtil;


public class DSAPassport {
	private Element securityToken;
	private Element passportToken;
	private String passportID;
	private String issuer;
	private Date issueInstant;
	private Date validTo;
	private String authMethod;
	private String requesterIP;
	private Set<String> roles;
	private HashMap<String, String> attributes;
	private String signature;
	private String subject;
	private String encoding;
	private DSApplicant applicant;
	private List<DSScope> authorizedScopes;
	private String version;

	/**
	 * <code>	 
	<DSAPassport Version="1.0">
	<Content>
		<PassportID>456</PassportID>
		<Issuer>tw.edu.tp</Issuer>
		<IssueInstant>cctt-mm-ddThh:mm:ss</IssueInstant>
		<ValidTo>cctt-mm-ddThh:mm:ss</ValidTo>
		<Subject>billyu</Subject>
		<AuthMethod>Basic|Enhanced</AuthMethod>
		<RequesterIP>203.72.205.11</RequesterIP>
		<Roles>
				<Role>Teacher</Role>
				<Role>Administrator</Role>
		</Roles>
		<Attributes>
			<Attribute1>Value1</Attribute1>
			<Attribute2>Value2</Attribute2>
		</Attributes>
		<Applicant>
			<ClientID></ClientID>
			<Namespace></Namespace>
			<DisplayName></DisplayName>
		</Applicant>
		<Authorized>
			<Scope>chvs.edu.tw:*</Scope>
		</Authorized>
	</Content>
	<ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
	</ds:Signature>
</DSAPassport>

	 * </code>
	 * **/
	private DSAPassport(Element source, boolean isSecurityToken) {
		if (isSecurityToken) {
			this.securityToken = source;
			this.passportToken = XmlUtil.selectFirstElement(source);
		} else {
			Node sttNode = source.getParentNode();
			if (sttNode != null)
				this.securityToken = (Element) sttNode;
			else
				this.securityToken = null;

			this.passportToken = source;
		}

		Element content = XmlUtil.selectElement(passportToken, "Content");
		this.encoding = passportToken.getAttribute("Encoding");
		this.passportID = XmlUtil.getElementText(content, "PassportID");
		this.issuer = XmlUtil.getElementText(content, "Issuer");
		this.authMethod = XmlUtil.getElementText(content, "AuthMethod");
		this.requesterIP = XmlUtil.getElementText(content, "RequesterIP");
		this.signature = XmlUtil.getElementText(passportToken, "ds:Signature");
		this.subject = XmlUtil.getElementText(content, "Subject");
		this.version = passportToken.getAttribute("Version");

		roles = new HashSet<String>();
		Element rolesElement = XmlUtil.selectElement(content, "Roles");
		for (Element e : XmlUtil.selectElements(rolesElement, "Role"))
			roles.add(e.getTextContent());

		attributes = new HashMap<String, String>();
		Element attrElement = XmlUtil.selectElement(content, "Attributes");
		for (Element e : XmlUtil.selectElements(attrElement))
			attributes.put(e.getNodeName(), e.getTextContent());

		this.issueInstant = Converter.toDate(XmlUtil.getElementText(content,
				"IssueInstant"));
		this.validTo = Converter.toDate(XmlUtil.getElementText(content,
				"ValidTo"));

		this.authorizedScopes = new ArrayList<DSScope>();

		Element ac = XmlUtil.selectElement(content, "Authorized");
		for (Element e : XmlUtil.selectElements(ac, "Scope")) {
			for (DSScope unit : DSScope.parse(e.getTextContent()))
				authorizedScopes.add(unit);
		}

		// DSA 5 版的規格相容支援
		String scope = XmlUtil.getElementText(content, "Scope");
		List<DSScope> scopes = DSScope.parse(scope);
		for (DSScope unit : scopes) {
			authorizedScopes.add(unit);
		}

		Element applicantElement = XmlUtil.selectElement(content, "Applicant");
		this.applicant = DSApplicant.load(applicantElement);
	}

	public static DSAPassport parseFromSecurityToken(Element securityToken) {
		return new DSAPassport(securityToken, true);
	}

	public static DSAPassport parseFromPassportToken(Element passportToken) {
		return new DSAPassport(passportToken, false);
	}

	public String getContentText() {
		Element content = XmlUtil.selectElement(this.passportToken, "Content");
		String str = PropertyUtil.toSimpleElementString(content);

		return str;
	}

	// public void setPassportID(String passportID) {
	// this.passportID = passportID;
	// }

	public String getPassportID() {
		return passportID;
	}

	// public void setIssuer(String issuer) {
	// this.issuer = issuer;
	// }

	public String getIssuer() {
		return issuer;
	}

	// public void setIssueInstant(Date issueInstant) {
	// this.issueInstant = issueInstant;
	// }

	public Date getIssueInstant() {
		return issueInstant;
	}

	// public void setValidTo(Date validTo) {
	// this.validTo = validTo;
	// }

	public Date getValidTo() {
		return validTo;
	}

	// public void setAuthMethod(String authMethod) {
	// this.authMethod = authMethod;
	// }

	public String getAuthMethod() {
		return authMethod;
	}

	// public void setRequesterIP(String requesterIP) {
	// this.requesterIP = requesterIP;
	// }

	public String getRequesterIP() {
		return requesterIP;
	}

	// public void setRoles(Set<String> roles) {
	// this.roles = roles;
	// }

	public Set<String> getRoles() {
		return roles;
	}

	// public void setAttributes(HashMap<String, String> attributes) {
	// this.attributes = attributes;
	// }

	public HashMap<String, String> getAttributes() {
		return attributes;
	}

	// public void setSignature(String signature) {
	// this.signature = signature;
	// }

	public String getSignature() {
		return this.signature;
	}

	// public void setSecurityToken(Element token) {
	// this.securityToken = token;
	// }

	public Element getSecurityToken() {
		return securityToken;
	}

	public Element getPassportTokenElement() {
		return this.passportToken;
	}

	// public void setSubject(String subject) {
	// this.subject = subject;
	// }

	public String getSubject() {
		return subject;
	}

	public String getEncoding() {
		return encoding;
	}

	public DSApplicant getApplicant() {
		return applicant;
	}

	public List<DSScope> getAuthorizedScopes() {
		return authorizedScopes;
	}

	public String getVersion() {
		return this.version;
	}
}
