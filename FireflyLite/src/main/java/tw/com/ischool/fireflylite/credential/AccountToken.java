package tw.com.ischool.fireflylite.credential;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

import tw.com.ischool.fireflylite.util.JSONUtil;
import tw.com.ischool.fireflylite.util.StringUtil;


/**
 * Created by jianwenlai on 15/6/8.
 */
public class AccountToken implements Serializable{
    public static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");

    private long tokenTime;
    private String tokenString;
    private String loginName;
    private String userUUID;
    private String myInfoString;
    private boolean isCurrentUser;
    private AccountInfo mAccountInfo;
    private String tokenScope;

    public static AccountToken createInstance(JSONObject token, JSONObject myInfo, String tokenScope){
        return new AccountToken(token, myInfo, tokenScope);
    }

    private AccountToken(JSONObject token, JSONObject myInfo, String tokenScope){
        this.loginName = JSONUtil.getString(myInfo, "userID");
        if(StringUtil.isNullOrWhitespace(loginName))
            this.loginName = JSONUtil.getString(myInfo, "userID");

        this.userUUID = JSONUtil.getString(myInfo, "uuid");
        this.tokenString = JSONUtil.toString(token, 1);
        this.myInfoString = JSONUtil.toString(myInfo, 1);
        this.isCurrentUser = true;
        this.tokenTime = Calendar.getInstance(AccountToken.DEFAULT_TIMEZONE).getTimeInMillis();
        this.tokenScope = tokenScope;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getTokenString(){
        return this.tokenString;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public String getMyInfoString(){
        return myInfoString;
    }

    public JSONObject getToken() {
        return JSONUtil.parseToJSONObject(this.tokenString);
    }

    public String getUserName(){
        if(mAccountInfo == null)
            mAccountInfo = new AccountInfo(this.myInfoString);

        return mAccountInfo.getUserName();
    }

    public boolean isCurrentUser(){
        return isCurrentUser;
    }

    public void setNewTokenString(String newTokenString){
        this.tokenString = newTokenString;
    }

    public long getTokenTime(){
        return this.tokenTime;
    }

    public String getTokenScope(){
        return this.tokenScope;
    }
}

