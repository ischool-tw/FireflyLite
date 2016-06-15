package tw.com.ischool.fireflylite.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import tw.com.ischool.fireflylite.Pupa;
import tw.com.ischool.fireflylite.credential.CallerPref;

/**
 * Created by jianwenlai on 2016/2/2.
 */
public class LoginHelper {
    public static final int RESULT_LOGIN_OK = 31215;

    public static final String PARAM_TOKEN_STRING = "tokenstring";
    public static final String PARAM_CLIENT_ID = "clientId";
    public static final String PARAM_CLIENT_SEC = "clientSec";
    public static final String SCOPE = "scope";
    public static final String REDIRECT_URI = "redirect";


    public static void startup(Activity activity, String clientId, String clientSec, String scope, String redirect, int requestCode){
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtra(PARAM_CLIENT_ID, clientId);
        intent.putExtra(PARAM_CLIENT_SEC, clientSec);
        intent.putExtra(SCOPE, scope);
        intent.putExtra(REDIRECT_URI, redirect);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void signOut(Context context, SignoutHandler signoutHandler ) {
        //清除 local 個人資訊
        CallerPref.clear(context);
        Pupa.getInstance().clearAll();

        //觸發 logout 事件
        if (signoutHandler != null) {
            signoutHandler.afterSignOut();
        }
    }

    public interface SignoutHandler {
        void afterSignOut() ;
    }
}
