package tw.com.ischool.fireflylite.login;

import tw.com.ischool.fireflylite.Pupa;

/**
 * Created by kevinhuang on 2016/3/15.
 */
public class OAuthConstant {
//    public static  String OAuthHost = "https://auth.ischoolcenter.com";
//    public static  String OAuthHost = "https://auth.ischool.com.tw";
//    public static final String AuthorizationURL = OAuthHost + "/oauth/authorize.php";
//    public static final String GetAccessTokenURL = OAuthHost + "/oauth/token.php";
//    public static final String GetUserInfo = OAuthHost + "/services/me.php";
//    public static final String RenewAccessTokenURL= OAuthHost + "";
//    public static final String SignOutURL= OAuthHost + "/logout.php";

//    public static  String DSNSURL = "https://dsns.ischoolcenter.com/dsns/api";
//    public static  String DSNSURL = "http://dsns.ischool.com.tw/dsns/dsns/dsns/";
//    public static  String DSNSURL = Pupa.getInstance().getDSNSUrl();

//    public static final String GreeningURL = "https://dsa.ischoolcenter.com/greening/api/";



    public static final String getAuthorizationURL() {
        return Pupa.getInstance().getAuthHostUrl() + "/oauth/authorize.php";
    }
//
//    public static final String getReAuthorizeURL() {
//        return Pupa.getInstance().getAuthHostUrl() + "https://auth.ischool.com.tw/oauth/authorize.php"
//    }

    public static final String getAccessTokenURL() {
        return Pupa.getInstance().getAuthHostUrl() + "/oauth/token.php";
    }

    public static final String getUserInfoURL(){
        return Pupa.getInstance().getAuthHostUrl() + "/services/me.php";
    }

    public static final String getRenewAccessTokenURL(){
        return Pupa.getInstance().getAuthHostUrl() + "";
    }

    public static final String getSignOutURL() {
        return Pupa.getInstance().getAuthHostUrl() + "/logout.php";
    }

}
