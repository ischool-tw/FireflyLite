package tw.com.ischool.fireflylite.credential;

import org.w3c.dom.Element;

import java.util.HashMap;

import tw.com.ischool.fireflylite.util.dsutil.AutoSwitchURLProvider;
import tw.com.ischool.fireflylite.util.dsutil.DSAServerException;
import tw.com.ischool.fireflylite.util.dsutil.DSRequest;
import tw.com.ischool.fireflylite.util.dsutil.DSResponse;
import tw.com.ischool.fireflylite.util.dsutil.DSStatus;
import tw.com.ischool.fireflylite.util.dsutil.SessionExpiredException;
import tw.com.ischool.fireflylite.util.StringUtil;
import tw.com.ischool.fireflylite.util.XmlUtil;
import tw.com.ischool.fireflylite.util.http.Cancelable;


/**
 * Created by jianwenlai on 15/6/8.
 */
public class DSACaller {

    private static HashMap<String, String> sMaps = new HashMap<String, String>();

    private String mAccessPoint;
    private String mTargetContract;
    private OauthTokenHandler mTokenHandler;

    public DSACaller(OauthTokenHandler tokenHandler, String accesspoint, String contract) {
        mTokenHandler = tokenHandler;
        mAccessPoint = accesspoint;
        mTargetContract = contract;
    }

    public DSResponse call(String targetService) throws ConnectException {
        return call(targetService, new DSRequest(), new Cancelable());
    }

    public DSResponse call(String targetService, Element reqContent) throws ConnectException {
        DSRequest request = new DSRequest();
        request.setContent(reqContent);
        return call(targetService, request, new Cancelable());
    }

    public DSResponse call(String targetService, Element request, Cancelable cancelable) throws ConnectException, DSAServerException {
        DSRequest request1 = new DSRequest();
        request1.setContent(request);
        try {
            return callOnce(targetService, request1, cancelable);
        }catch(ConnectException ex) {
            mTokenHandler.renewAccessToken();
            return callOnce(targetService, request1, cancelable);
        }
    }

    public DSResponse call(String targetService, DSRequest request, Cancelable cancelable) throws ConnectException, DSAServerException {

        try {
            return callOnce(targetService, request, cancelable);
        }catch(ConnectException ex) {
            mTokenHandler.renewAccessToken();
            return callOnce(targetService, request, cancelable);
        }
    }

    private DSResponse callOnce(String targetService, DSRequest request, Cancelable cancelable) throws ConnectException, DSAServerException {

        String accessToken = mTokenHandler.getAccessToken();
        String expired = mTokenHandler.showExpireTime();

        Element stt = XmlUtil.createElement("SecurityToken");
        stt.setAttribute("Type", "PassportAccessToken");
        XmlUtil.addElement(stt, "AccessToken", accessToken);

        String url = StringUtil.EMPTY;
        if (sMaps.containsKey(mAccessPoint)) {
            url = sMaps.get(mAccessPoint);
        } else {
            AutoSwitchURLProvider provider = new AutoSwitchURLProvider(mAccessPoint);
            url = provider.getTargetURL();
            sMaps.put(mAccessPoint, url);
        }

        BasicContractCaller caller = new BasicContractCaller(url, mTargetContract, stt);
        try {
            return caller.request(targetService, request, cancelable);
        } catch (SessionExpiredException ex) {
            // never happen here
            return null;
        } catch (DSAServerException ex) {
            if (ex.getStatusCode().equals(DSStatus.CredentialInvalid().code())) {
                //有各式可能
                throw new ConnectException(ex, ex.getMessage());
            }
            throw ex;
        }
    }

    public OauthTokenHandler getTokenHandler() {
        return mTokenHandler;
    }
}
