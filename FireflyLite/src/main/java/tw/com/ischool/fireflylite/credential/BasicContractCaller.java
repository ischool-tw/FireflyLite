package tw.com.ischool.fireflylite.credential;

import org.w3c.dom.Element;

import tw.com.ischool.fireflylite.util.dsutil.DSAServerException;
import tw.com.ischool.fireflylite.util.dsutil.DSAServiceException;
import tw.com.ischool.fireflylite.util.dsutil.DSRequest;
import tw.com.ischool.fireflylite.util.dsutil.DSResponse;
import tw.com.ischool.fireflylite.util.dsutil.DSStatus;
import tw.com.ischool.fireflylite.util.dsutil.SessionExpiredException;
import tw.com.ischool.fireflylite.util.XmlHelper;
import tw.com.ischool.fireflylite.util.XmlUtil;
import tw.com.ischool.fireflylite.util.http.Cancelable;
import tw.com.ischool.fireflylite.util.http.HttpUtil;


/**
 * Created by 西華 on 2015/1/22.
 */
public class BasicContractCaller implements IContractCaller {
    public static final String SERVICE_CONNECT = "DS.Base.Connect";
    public static final int DEFAULT_TIMEOUT = 60000;

    private String _sttString;
    private String _url;
    private String _contract;

    public BasicContractCaller(){

    }

    public BasicContractCaller(String url, String contract, Element stt) {
        _url = url;
        _contract = contract;
        _sttString = XmlHelper.convertToString(stt);
    }

    @Override
    public Element connect(String url, String contract, Element securityToken, Cancelable cancelable) throws ConnectException{
        _url = url;
        _contract = contract;
        _sttString = XmlHelper.convertToString(securityToken);

        DSRequest req = new DSRequest();
        req.setTargetContract(_contract);
        req.setSecurityToken(securityToken);
        req.setTargetService(SERVICE_CONNECT);


        try {
            DSResponse response = this.sendRequest(req, cancelable);
            Element header = response.getHeader();
            Element userInfoElement = XmlUtil.selectElement(header, "UserInfo");
            return userInfoElement;
        } catch (SessionExpiredException ex) {
            throw new ConnectException(ex, "Session Expired");
        } catch (DSAServiceException ex) {
            throw new ConnectException(ex, ex.getMessage());
        }


    }

    @Override
    public DSResponse request(String targetService, DSRequest request, Cancelable cancelable) throws SessionExpiredException {

        Element stt = XmlHelper.parseXml(_sttString);

        request.setTargetContract(_contract);
        request.setSecurityToken(stt);
        request.setTargetService(targetService);

        return sendRequest(request, cancelable);
    }

    @Override
    public DSResponse request(String targetService, Element requestContent, Cancelable cancelable) throws SessionExpiredException{
        DSRequest request = new DSRequest();
        request.setContent(requestContent);
        return request(targetService, request, cancelable);
    }

    @Override
    public DSResponse request(String targetService, Cancelable cancelable) throws SessionExpiredException{
        return request(targetService, new DSRequest(), cancelable);
    }

    private DSResponse sendRequest(DSRequest request,
                                   Cancelable cancelable) throws SessionExpiredException {

        String reqString = request.getXML();
        String result = HttpUtil.postDataForString(_url, reqString,
                DEFAULT_TIMEOUT, cancelable);

        if (cancelable != null && cancelable.isCanceled()) {
            return null;
        }

        DSResponse rsp = new DSResponse(result);

        handleResponse(rsp);
        return rsp;
    }

    private void handleResponse(DSResponse response) throws SessionExpiredException{
        if (response.getStatus().equals(DSStatus.Successful()))
            return;

        if (response.getStatus().code().equalsIgnoreCase(DSStatus.SessionInvalid().code()))
            throw new SessionExpiredException();

        if (response.getStatus().code()
                .equals(DSStatus.ServiceExecutionError().code())) {
            throw new DSAServiceException(response);
        }

        DSAServerException exception = new DSAServerException(
                response.getStatus());

        throw exception;
    }
}
