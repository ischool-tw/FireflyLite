package tw.com.ischool.fireflylite.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import tw.com.ischool.fireflylite.R;
import tw.com.ischool.fireflylite.credential.CallerPref;
import tw.com.ischool.fireflylite.credential.ConnectException;
import tw.com.ischool.fireflylite.credential.OauthTokenHandler;
import tw.com.ischool.fireflylite.util.JSONUtil;

public class ReAuthActivity extends AppCompatActivity {


    public static final String PARAM_CLIENT_ID = "clientId";
    public static final String PARAM_REDIRECT_URL = "rediret_url";
    public static final String PARAM_SCPOE = "scope";

    public static final String RESULT_TOKEN = "access_token";

    private static final String getReAuthURL() {
        return OAuthConstant.getAuthorizationURL() +"?client_id=%s&response_type=token&redirect_uri=%s&scope=%s&access_token=%s";
    }

    private String mClientId;
    private String mClientSec;
    private String mScope;
    private String mRedirectUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_auth);

         mClientId = getIntent().getStringExtra(PARAM_CLIENT_ID);
         mScope = getIntent().getStringExtra(PARAM_SCPOE);
         mRedirectUri = getIntent().getStringExtra(PARAM_REDIRECT_URL);


        WebView mWebView = (WebView)findViewById(R.id.webView);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.requestFocus();
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setVisibility(View.VISIBLE);

//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.requestFocus();
//        mWebView.setWebViewClient(new MyWebViewClient());

        OauthTokenHandler tokenHandler = CallerPref.newTokenHandler(this);
        String accessToken = "";
        try {
            accessToken = tokenHandler.getAccessToken();
        } catch (ConnectException e) {
            try {
                tokenHandler.renewAccessToken();
                accessToken = tokenHandler.getAccessToken();
            }catch(Exception ex) {

            }
        }
        String url = String.format(getReAuthURL(), mClientId, mRedirectUri ,mScope, accessToken);
        mWebView.loadUrl(url);

    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith("http://_blank")) {

                String[] arg = url.split("#");
                String info = arg[1];
                String[] params = info.split("&");

                JSONObject token = new JSONObject();
                for(String param : params) {
                    String[] s = param.split("=");
                    String paramName = s[0];
                    String paramValue = s[1];

                    try {
                        paramValue = URLDecoder.decode(paramValue, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                    }

                    JSONUtil.put(token, paramName, paramValue);
                }

                String tokenString =  JSONUtil.toString(token, 1);

                Intent data = new Intent();
                data.putExtra(RESULT_TOKEN, tokenString);
                setResult(RESULT_OK, data);
                finish();

                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

    }
}
