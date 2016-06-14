package tw.com.ischool.fireflylite.credential;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import tw.com.ischool.fireflylite.util.Converter;
import tw.com.ischool.fireflylite.util.JSONUtil;
import tw.com.ischool.fireflylite.util.StringUtil;

/**
 * Created by jianwenlai on 15/5/22.
 */
public class CallerPref {

    private static AccountToken sToken;
    private static OauthTokenHandler sTokenHandler;
    private static String sClientId;
    private static String sClientSec;

//    private static String sTokenPref = "teacher_token_pref";    //如果是編譯老師的 app，則設定此值
    private static String sTokenPref = "parent_token_pref";    //如果是編譯家長的 app，則設定此值

    /**
     * 設定身份別，這會影響儲存 user preference 的位置！
     * @param identity
     */
    public static void setIdentity(IdentityEnum identity) {
        switch(identity) {
            case PARENT:
                sTokenPref = "parent_token_pref";
                break;
            case TEACHER:
                sTokenPref = "teacher_token_pref";
                break;
            case STUDENT:
                sTokenPref = "student_token_pref";
                break;
            case PRINCIPAL:
                sTokenPref = "principal_token_pref";
                break;
            default:
                sTokenPref = "token_pref";
                break;
        }
    }

    public static void setIdentity(String key){
        sTokenPref = key;
    }


    public static void set(Context context, AccountToken token, String clientId, String clientSec) {
        sToken = token;
        sClientId = clientId;
        sClientSec = clientSec;
        backup(context, token);
    }

    public static AccountToken get(Context context) {
        if (sToken == null) {
            restore(context);
        }
        return sToken;
    }

    public synchronized static OauthTokenHandler newTokenHandler(final Context context) {

        if(sTokenHandler != null)
            return sTokenHandler;

        final AccountToken token = CallerPref.get(context);
        sTokenHandler = new OauthTokenHandler(sClientId, sClientSec, token);
        sTokenHandler.setTokenHandlerListener(new OauthTokenHandler.TokenHandlerListener() {
            @Override
            public void onAccessTokenChanged(String newTokenString) {
                restoreNewTokenString(context, newTokenString);
            }
        });
        return sTokenHandler;
    }

    private synchronized static void restoreNewTokenString(Context context, String newTokenString) {
        sToken.setNewTokenString(newTokenString);
        backup(context, sToken);
    }

    private synchronized static void backup(Context context, AccountToken token) {
        String base64 = Converter.toBase64String(token);
        JSONObject json = new JSONObject();
        String content = StringUtil.EMPTY;
        try {
            json.put("client_id", sClientId);
            json.put("client_secret", sClientSec);
            json.put("account_token", base64);

            content = json.toString(1);
        }catch(JSONException e) {

        }
//        SharedPreferences.Editor editor = context.getSharedPreferences("token_pref", 0).edit();
        SharedPreferences.Editor editor = context.getSharedPreferences(sTokenPref, 0).edit();
        editor.putString("account_token_content", content);
        editor.commit();
    }

    public synchronized static AccountToken restore(Context context) {
        SharedPreferences pref = context.getSharedPreferences(sTokenPref, 0);
//        SharedPreferences pref = context.getSharedPreferences("token_pref", 0);
        String accountTokenContent = pref.getString("account_token_content", "");
        if(StringUtil.isNullOrWhitespace(accountTokenContent))
            return null;

        JSONObject json = JSONUtil.parseToJSONObject(accountTokenContent);
        sClientId = JSONUtil.getString(json, "client_id");
        sClientSec = JSONUtil.getString(json, "client_secret");
        String content = JSONUtil.getString(json, "account_token");
        sToken = Converter.fromBase64String(content, AccountToken.class);

        return sToken;
    }

    public static void resetToken(Context context, String accessToken) {
        AccountToken token = get(context);
        JSONObject json = JSONUtil.parseToJSONObject(accessToken);
        String tokenScope = JSONUtil.getString(json, "scope");

        JSONObject myInfo = JSONUtil.parseToJSONObject(token.getMyInfoString());
        token = AccountToken.createInstance(json, myInfo, tokenScope);

        backup(context, token);
        sToken = token;
        sTokenHandler = null;
    }

    public synchronized static void clear(Context context){
        SharedPreferences pref = context.getSharedPreferences(sTokenPref, 0);
//        SharedPreferences pref = context.getSharedPreferences("token_pref", 0);

        SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        edit.commit();

        sTokenHandler = null;
        sToken = null ;
    }

    public static DSACaller newDSACaller(Context context, String accesspoint, String contract) {
        OauthTokenHandler handler = newTokenHandler(context);
        DSACaller caller = new DSACaller(handler, accesspoint, contract);
        return caller;
    }



}
