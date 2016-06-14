package tw.com.ischool.fireflylite.credential;

import org.w3c.dom.Element;

import java.io.Serializable;

import tw.com.ischool.fireflylite.util.dsutil.DSRequest;
import tw.com.ischool.fireflylite.util.dsutil.DSResponse;
import tw.com.ischool.fireflylite.util.http.Cancelable;


/**
 * Created by 西華 on 2015/1/22.
 */
public interface IContractCaller extends Serializable{

    /**
     * 建立連線
     * @param securityToken
     * @param cancelable
     * @return userInfo Element
     */
    Element connect(String url, String contract, Element securityToken, Cancelable cancelable) throws ConnectException;

    /**
     * 呼叫服務
     * @param targetService : 服務名稱
     * @param request : 需求文件
     * @param cancelable : 取消
     * @return
     * @throws SessionExpiredException
     */
    DSResponse request(String targetService, DSRequest request, Cancelable cancelable) throws CallerException;

    /**
     * 呼叫服務
     * @param targetService : 服務名稱
     * @param requestContent : 需求文件
     * @param cancelable : 取消
     * @return
     * @throws SessionExpiredException
     */
    DSResponse request(String targetService, Element requestContent, Cancelable cancelable) throws CallerException;

    /**
     * 呼叫服務
     * @param targetService : 服務名稱
     * @param cancelable : 取消
     * @return
     * @throws SessionExpiredException
     */
    DSResponse request(String targetService, Cancelable cancelable) throws CallerException;
}
