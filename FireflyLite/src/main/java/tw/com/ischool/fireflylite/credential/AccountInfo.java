package tw.com.ischool.fireflylite.credential;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

import tw.com.ischool.fireflylite.util.JSONUtil;
import tw.com.ischool.fireflylite.util.StringUtil;


/**
 * Created by jianwenlai on 15/6/17.
 */
public class AccountInfo implements Serializable {
    private String userInfoString;
    private String lastName;
    private String firstName;
    private JSONObject objJson ;
    private String uuid;

    public AccountInfo(String userInfoString) {
        this.userInfoString = userInfoString;
        this.objJson = JSONUtil.parseToJSONObject(this.userInfoString);
        this.firstName = JSONUtil.getString(this.objJson, "firstName");
        this.lastName = JSONUtil.getString(this.objJson, "lastName");
        this.uuid = JSONUtil.getString(this.objJson, "uuid");

    }

    public String getUserUUID() {
//        JSONObject json = JSONUtil.parseToJSONObject(this.userInfoString);
//        String uuid = JSONUtil.getString(this.objJson, "uuid");
        return uuid; 
    }

    public String getLoginName() {
//        JSONObject json = JSONUtil.parseToJSONObject(this.userInfoString);
        String mail = JSONUtil.getString(this.objJson, "mail");
        if(StringUtil.isNullOrWhitespace(mail))
            mail = JSONUtil.getString(this.objJson, "userID");
        return mail;
    }

    public String getFirstName() {
//        return JSONUtil.getString(this.objJson, "firstName");
        return firstName ;
    }

    public String getLastName() {
//        return JSONUtil.getString(this.objJson, "lastName");
        return lastName ;
    }

    public String getUserName() {

        Locale locale = Locale.getDefault();
        if (locale.getLanguage().equals(Locale.CHINESE.getLanguage())) {
            return this.lastName + this.firstName;
        } else {
            return this.firstName + " " + this.lastName;
        }
    }
}
