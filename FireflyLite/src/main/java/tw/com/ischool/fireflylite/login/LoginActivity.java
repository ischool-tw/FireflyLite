package tw.com.ischool.fireflylite.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONObject;

import tw.com.ischool.fireflylite.Pupa;
import tw.com.ischool.fireflylite.R;
import tw.com.ischool.fireflylite.credential.AccountToken;
import tw.com.ischool.fireflylite.credential.CallerPref;
import tw.com.ischool.fireflylite.credential.ConnectException;
import tw.com.ischool.fireflylite.credential.OauthTokenHandler;
import tw.com.ischool.fireflylite.util.JSONUtil;
import tw.com.ischool.fireflylite.util.LogUtil;
import tw.com.ischool.fireflylite.util.dsutil.OnReceiveListener;
import tw.com.ischool.fireflylite.util.http.HttpUtil;
import tw.com.ischool.fireflylite.util.http.Cancelable;

public class LoginActivity extends AppCompatActivity {


    public String getURL_HOST(){
//        return OAuthConstant.OAuthHost;
        return Pupa.getInstance().getAuthHostUrl();
    }

    private String getURL_AUTH() {
        return OAuthConstant.getAuthorizationURL();
    }

    private String getURL_LOGIN() {
        return "%s?client_id=%s&response_type=code&state=%s&redirect_uri=%s&scope=%s";
    }

    private String getURL_CODE_LOGIN() {
        return OAuthConstant.getAccessTokenURL() + "?grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s&redirect_uri=%s";
    }

    private String getURL_GET_USER_UUID() {
        return OAuthConstant.getUserInfoURL() + "?access_token=%s";
    }
    private String getURL_LOGOUT() {
        return OAuthConstant.getSignOutURL();
    }



    private WebView mWebLogin;

    private String mClientId;
    private String mClientSec;
    private String mScope;
    private String mRedirectUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mWebLogin = (WebView) findViewById(R.id.webLogin);

        /* 取得相關資料 */
        mClientId = getIntent().getStringExtra(LoginHelper.PARAM_CLIENT_ID);
        mClientSec = getIntent().getStringExtra(LoginHelper.PARAM_CLIENT_SEC);
        mScope = getIntent().getStringExtra(LoginHelper.SCOPE);
        mRedirectUri = getIntent().getStringExtra(LoginHelper.REDIRECT_URI);

        //啟動登入流程
        openLogin();
    }


    private void openLogin() {

        //先判斷 Local 有沒有儲存 access token，也就是是否登入，且尚未登出！
        LogUtil.log("LoginActivity.openLogin(), 檢查 local 是否有 token");
        AccountToken token = CallerPref.restore(this);

        if(token != null) {
            LogUtil.log("local 有 token 存在, token string : " + token.getTokenString());
            LogUtil.log("而且  myInfoString : " + token.getMyInfoString());

            LogUtil.log("接下來要 renew 一組 access token. ");
            // 如果 local 有 access token，就拿去 renew 一組新的 access token.
            new AsyncTask<Void, Void, ConnectException>(){
                @Override
                protected ConnectException doInBackground(Void... params) {
                    LogUtil.log("在 AsyncTask 的背景中，準備從 shared preference 中讀取 token 資訊，呼叫 CallerPref.restore() ");
                    AccountToken token1 = CallerPref.restore(LoginActivity.this);
                    LogUtil.log("從 shared preference 中讀取 token 的結果， token string : " + token1.getTokenString());
                    LogUtil.log("而且  myInfoString : " + token1.getMyInfoString());

                    OauthTokenHandler handler = CallerPref.newTokenHandler(LoginActivity.this);
                    try {
                        //重換一組 token，並寫入 shared preference
                        handler.renewAccessToken();
                    }catch(ConnectException ex){
                        return ex;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(ConnectException ex) {
                    if(ex != null) {
                        startLogin();
                    } else {
                        AccountToken token = CallerPref.get(LoginActivity.this);
                        Intent intent = new Intent();
                        intent.putExtra(LoginHelper.PARAM_TOKEN_STRING, token);
                        setResult(LoginHelper.RESULT_LOGIN_OK, intent);
                        finish();
                    }
                }
            }.execute();

        } else {
            startLogin();
        }

    }

    private void startLogin(){
        CookieManager cm = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().removeAllCookies(null);
        } else {
            CookieManager.getInstance().removeAllCookie();
        }
        cm.getCookie(getURL_AUTH());

        mWebLogin.setVisibility(View.VISIBLE);
        mWebLogin.getSettings().setJavaScriptEnabled(true);
        mWebLogin.requestFocus();
        mWebLogin.setWebViewClient(new MyWebViewClient());

        String url = String.format(getURL_LOGIN(), getURL_AUTH(), mClientId, "1Campus Mobile", mRedirectUri, mScope);
        mWebLogin.loadUrl(url);
    }



    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            //如果 url 是 redirect_url，則用 code 換 token
            if (url.startsWith(mRedirectUri)) {
                String[] arg = url.split("&code=");
                String code = arg[1];
                String redirect = getURL_CODE_LOGIN();
                redirect = String.format(redirect, mClientId, mClientSec, code, mRedirectUri);
//                String cookie = CookieManager.getInstance().getCookie("https://auth.ischool.com.tw");
                String cookie = CookieManager.getInstance().getCookie(getURL_HOST());

                //用 code 換 token
                getToken(redirect, cookie);

//                String home = String.format(URL_LOGIN, URL_AUTH, mClientId, REDIRECT_URI, REDIRECT_URI);
//                view.loadUrl(home);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (url.equals(getURL_LOGOUT())) {
                url = String.format(getURL_LOGIN(), getURL_AUTH(), mClientId, mRedirectUri, mRedirectUri);
                mWebLogin.loadUrl(url);
            }
        }


    }

    //拿 code 換 token
    private void getToken(String url, String cookie) {
        mWebLogin.setVisibility(View.GONE);

        TokenTask task = new TokenTask(cookie);

        task.setListener(new OnReceiveListener<AccountToken>() {
            @Override
            public void onReceive(final AccountToken tokenString) {
                Intent intent = new Intent();
                intent.putExtra(LoginHelper.PARAM_TOKEN_STRING, tokenString);

                CallerPref.set(LoginActivity.this, tokenString, mClientId, mClientSec);

                setResult(LoginHelper.RESULT_LOGIN_OK, intent);
                finish();
            }

            @Override
            public void onError(Exception ex) {

                Toast.makeText(LoginActivity.this, "登入失敗", Toast.LENGTH_SHORT).show();
                String url = String.format(getURL_LOGIN(), getURL_AUTH(), mClientId, mClientSec, mRedirectUri, mScope);
                mWebLogin.loadUrl(url);
            }
        });
        task.execute(url, getURL_GET_USER_UUID());
    }


    private class TokenTask extends AsyncTask<String, Void, AccountToken> {

        private OnReceiveListener<AccountToken> _listener;
        private Exception _exception;
        private String _cookie;

        public TokenTask(String cookie){
            _cookie = cookie;
        }

        public void setListener(OnReceiveListener<AccountToken> listener) {
            _listener = listener;
        }

        @Override
        protected AccountToken doInBackground(String... params) {
            try {
                String url = params[0];
                String tokenString = HttpUtil.getString(url, new Cancelable());
                JSONObject json = JSONUtil.parseToJSONObject(tokenString);
                String accessToken = JSONUtil.getString(json, "access_token");

                String urlUUID = params[1];
                urlUUID = String.format(urlUUID, accessToken);
                String myInfo = HttpUtil.getString(urlUUID, new Cancelable());

                JSONObject myInfoJSON = JSONUtil.parseToJSONObject(myInfo);
                AccountToken token = AccountToken.createInstance(json, myInfoJSON, mScope);
                return token;
            } catch (Exception e) {
                _exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(AccountToken tokenString) {
            if (tokenString == null) {
                _listener.onError(_exception);
            } else {
                _listener.onReceive(tokenString);
            }

        }
    }
}
