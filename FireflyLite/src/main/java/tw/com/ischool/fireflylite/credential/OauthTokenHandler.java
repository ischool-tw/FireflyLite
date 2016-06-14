package tw.com.ischool.fireflylite.credential;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import tw.com.ischool.fireflylite.util.JSONUtil;
import tw.com.ischool.fireflylite.util.StringUtil;

import tw.com.ischool.fireflylite.login.OAuthConstant;
import tw.com.ischool.fireflylite.util.http.Cancelable;
import tw.com.ischool.fireflylite.util.http.HttpUtil;

/**
 * Created by jianwenlai on 15/6/8.
 */
public class OauthTokenHandler implements Serializable {

//    private static final String URL_REFRESH_TOKEN = "https://auth.ischool.com.tw/oauth/token.php?";
//    private static final String URL_REFRESH_TOKEN = OAuthConstant.getAccessTokenURL();
    private static final String URL_PARAM__REFRESH_TOKEN = "?grant_type=refresh_token&client_id=%s&client_secret=%s&refresh_token=%s";

    private String mOriginalToken;
    private String CLIENT_ID;
    private String CLIENT_SEC;
    private String mRefreshToken;
    private String mAccessToken;
    private int mExpiresIn;
    private long mCalcedExpiredMills;
    private String mLoginName;
    private String mUserUUID;
    private AccountToken mAccountToken;
    private String mRefreshTokenURL;

    private TokenHandlerListener mListener;


    public OauthTokenHandler(String refreshTokenURL, String clientid, String clientsec, AccountToken token) {
        CLIENT_ID = clientid;
        CLIENT_SEC = clientsec;
        if (!refreshTokenURL.endsWith("?"))
            refreshTokenURL = refreshTokenURL + "?";

        mRefreshTokenURL = refreshTokenURL + URL_PARAM__REFRESH_TOKEN;
        mLoginName = token.getLoginName();
        mUserUUID = token.getUserUUID();
        mAccountToken = token;
        init(token.getToken(), token.getTokenTime());
    }

    public OauthTokenHandler(String clientid, String clientsec, AccountToken token) {
        CLIENT_ID = clientid;
        CLIENT_SEC = clientsec;
//      mRefreshTokenURL = URL_REFRESH_TOKEN + URL_PARAM__REFRESH_TOKEN;
        mRefreshTokenURL = OAuthConstant.getAccessTokenURL() + URL_PARAM__REFRESH_TOKEN;
        mLoginName = token.getLoginName();
        mUserUUID = token.getUserUUID();
        mAccountToken = token;
        init(token.getToken(), token.getTokenTime());
    }

    public synchronized String getAccessToken() throws ConnectException {
        if (!isAccessTokenExpired())
            return mAccessToken;

        renewAccessToken();
        return mAccessToken;
    }

    public boolean isAccessTokenExpired() {
        long now = getNowMills();
        String nowString = displayTime(now);
        String expiredString = displayTime(mCalcedExpiredMills);
        Log.d("OauthTokenHandler", nowString + ":" + expiredString);
        if (mCalcedExpiredMills <= now + (1000 * 15 * 60))
            return true;
        return false;
    }

    private String displayTime(long mills) {
        Calendar calendar = Calendar.getInstance(AccountToken.DEFAULT_TIMEZONE);
        calendar.setTimeInMillis(mills);
        Date d = calendar.getTime();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        return formater.format(d);
    }

    public List<String> listAppRef(String contractName) {
        return listAppRef(mOriginalToken, contractName);
    }

    public AccountToken getAccountToken() {
        return mAccountToken;
    }

    private static List<String> AppRefList;
    private static Object lock = new Object();

    public static List<String> listAppRef(String rawTokenString, String contractName) {
        synchronized (lock) {


            AppRefList = new ArrayList<String>();

            //ArrayList<String> list = new ArrayList<String>();

            JSONObject token = JSONUtil.parseToJSONObject(rawTokenString);
            String scope = JSONUtil.getString(token, "scope");
            String[] scopes = scope.split(",");

            for (String s : scopes) {
                if (s.endsWith(":" + contractName)) {
                    int len = s.length() - (contractName.length() + 1);
                    String prefix = s.substring(0, len);
                    AppRefList.add(prefix);
                }
            }

            ArrayList<String> list = new ArrayList<String>();
            list.addAll(AppRefList);
            return list;
        }
    }

    public static void kickoutAppRef(String dsns) {
        if (AppRefList == null) return;

        synchronized (lock) {
            ArrayList<String> list = new ArrayList<String>();
            for (String school : AppRefList) {
                if (school.equals(dsns)) continue;
                list.add(school);
            }
            AppRefList.clear();
            AppRefList.addAll(list);
        }
    }

    public synchronized void renewAccessToken() throws ConnectException {
        String urlString = String.format(mRefreshTokenURL, CLIENT_ID, CLIENT_SEC, mRefreshToken);

        Cancelable cancelable = new Cancelable();

        String jsonString = HttpUtil.getString(urlString, cancelable);
        JSONObject json = JSONUtil.parseToJSONObject(jsonString);

        String error = JSONUtil.getString(json, "error");
        if (!StringUtil.isNullOrWhitespace(error)) {
            String desc = JSONUtil.getString(json, "error_description");
            throw new ConnectException(new Exception(error), desc);
        }

        init(json, getNowMills());

        if (mListener != null)
            mListener.onAccessTokenChanged(jsonString);
    }

    private void init(JSONObject token, long tokenCreateTime) {
        mAccessToken = JSONUtil.getString(token, "access_token");
        mRefreshToken = JSONUtil.getString(token, "refresh_token");
        mExpiresIn = JSONUtil.getInt(token, "expires_in");
        mCalcedExpiredMills = calcExpiredMills(tokenCreateTime);
        mOriginalToken = JSONUtil.toString(token, 1);
    }

    private long getNowMills() {
        return Calendar.getInstance(AccountToken.DEFAULT_TIMEZONE).getTimeInMillis();
    }

    private long calcExpiredMills(long tokenCreateTime) {
        long expired = tokenCreateTime + (mExpiresIn * 1000);

        String estring = displayTime(expired);
        Log.d("OauthTokenHandler", "expired : " + estring);

        return expired;
    }

    public void setTokenHandlerListener(TokenHandlerListener listener) {
        mListener = listener;
    }

    public interface TokenHandlerListener {
        void onAccessTokenChanged(String newTokenString);
    }

    public String getLoginName() {
        return mLoginName;
    }

    public String getUserUUID() {
        return mUserUUID;
    }

    public String showExpireTime() {
        return displayTime(mCalcedExpiredMills);
    }

}
