package tw.com.ischool.fireflylite.util.dsutil.token;

import org.w3c.dom.Element;

import tw.com.ischool.fireflylite.util.XmlUtil;


public class InternalTokenProvider implements ITokenProvider {

	@Override
	public Element getSecurityToken() {
		Element stt = XmlUtil.createElement("SecurityToken");
		stt.setAttribute("Type", "Internal");
		return stt;
	}

}
