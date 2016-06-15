package tw.com.ischool.fireflylite;

import android.content.Context;

import java.util.List;

import tw.com.ischool.fireflylite.credential.AccountInfo;
import tw.com.ischool.fireflylite.credential.AccountToken;
import tw.com.ischool.fireflylite.credential.CallerPref;
import tw.com.ischool.fireflylite.credential.OauthTokenHandler;


/**
 * Created by kevinhuang on 2016/4/19.
 */
public class Pupa {

    private static Pupa instance;

    public static Pupa getInstance() {
        if (instance == null) {
            instance = new Pupa();
        }

        return instance ;
    }

    private String mClientID;
    private String mClientSecret;
    private String mScope;
    private String mRedirectUri = "http://_blank" ;     //default value

    private String mAuthHostUrl = "https://auth.ischool.com.tw";    //default value
    private String mDSNSUrl     = "https://dsns.ischool.com.tw";    //default value
    private String mGreeningUrl ="https://auth.ischool.com.tw:8443/dsa/greening";  //default value


    private Pupa() {
    }


    /**
     * Client ID
     * @return
     */
    public String getClientID() {
        return mClientID;
    }

    public void setClientID(String ClientID) {
        this.mClientID = ClientID;
    }

    /**
     * Client Secret
     * @return
     */
    public String getClientSecret() {
        return mClientSecret;
    }

    public void setClientSecret(String ClientSecret) {
        this.mClientSecret = ClientSecret;
    }

    /**
     * Client ID
     * @return
     */
    public String getScope() {
        return mScope;
    }

    public void setScope(String scope) {
        this.mScope = scope;
    }

    /**
     * Redirect Uri
     * @return
     */
    public String getRedirectUri() {
        return mRedirectUri;
    }

    public void setRedirectUri(String scope) {
        this.mRedirectUri = scope;
    }



    /**
     * Auth Server 位置，預設為 https://auth.ischool.com.tw
     * @return
     */
    public String getAuthHostUrl() {
        return mAuthHostUrl;
    }

    public void setAuthHostUrl(String AuthHostUrl) {
        this.mAuthHostUrl = AuthHostUrl;
    }

    /**
     * DSNS 位置
     * @return
     */
    public String getDSNSUrl() {
        return mDSNSUrl;
    }

    public void setDSNSUrl(String DSNSUrl) {
        this.mDSNSUrl = DSNSUrl;
//        this.mIdentityType = IdentityEnum.TEACHER;
    }

    /**
     * Greening Server 位置，預設為 https://auth.ischool.com.tw:8443
     * @return
     */
    public String getGreeningUrl() {
        return mGreeningUrl;
    }

    public void setGreeningUrl(String greeningUrl) {
        this.mGreeningUrl = greeningUrl;
    }


    /**
     * 取得目前使用者資訊
     */
    public AccountInfo getCurrentUser(Context context) {
        AccountToken token =  CallerPref.get(context);
        AccountInfo result = null;
        if (token != null) {
            result =  new AccountInfo(token.getMyInfoString());
        }

        return result ;
    }

    /**
     * 針對目前使用者對於某個 contract，已授權的學校清單。
     */
    public List<String> getPossibleDSNS(Context context, String contract_name) {

        OauthTokenHandler handler = CallerPref.newTokenHandler(context);
        List<String> apps = handler.listAppRef(contract_name);  //從 scope 中解析已經授權指定 contract 的 app 清單。

        return apps ;
    }

    public void clearAll() {

    }
}
