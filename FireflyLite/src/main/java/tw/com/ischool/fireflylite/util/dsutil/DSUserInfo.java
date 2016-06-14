package tw.com.ischool.fireflylite.util.dsutil;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import tw.com.ischool.fireflylite.util.Converter;
import tw.com.ischool.fireflylite.util.XmlUtil;

public class DSUserInfo{

	private String userName;
	private String serverName;
	private String applicationName;
	private String contractName;
	private Calendar expired;
	private int timeout;
	private List<String> roles;
	private Properties properties;
	private boolean extendable;

	public static DSUserInfo load(Element source) {
		DSUserInfo user = new DSUserInfo();
		user.applicationName = source.getAttribute("Application");
		user.contractName = source.getAttribute("Contract");
		user.serverName = source.getAttribute("Server");
		user.userName = source.getAttribute("UserName");
		user.timeout = Converter.toInteger(source.getAttribute("Timeout"), -1);
		user.extendable = Converter.toBoolean(source.getAttribute("Extendable"), true);
		
		Date d = Converter.toDate(source.getAttribute("Expired"));
		user.expired.setTime(d);

		for (Element r : XmlUtil.selectElements(source, "Role")) {
			user.roles.add(r.getAttribute("Name"));
		}

		for (Element p : XmlUtil.selectElements(source, "Property")) {
			String pname = p.getAttribute("Name");
			String pvalue = p.getTextContent();
			user.properties.setProperty(pname, pvalue);
		}

		return user;
	}
	
	private DSUserInfo() {
		roles = new ArrayList<String>();
		properties = new Properties();
		expired = Calendar.getInstance();
	}

	public String getUserName() {
		return userName;
	}

	public String getServerName() {
		return serverName;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getContractName() {
		return contractName;
	}

	public Calendar getExpired() {
		return expired;
	}

	public int getTimeout() {
		return timeout;
	}

	public List<String> getRoles() {
		return roles;
	}

	public Properties getProperties() {
		return properties;
	}

	public boolean isExtendable(){
		return extendable;
	}
}
