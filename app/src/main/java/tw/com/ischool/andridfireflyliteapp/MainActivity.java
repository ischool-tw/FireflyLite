package tw.com.ischool.andridfireflyliteapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import tw.com.ischool.fireflylite.Pupa;
import tw.com.ischool.fireflylite.credential.AccountInfo;
import tw.com.ischool.fireflylite.credential.AccountToken;
import tw.com.ischool.fireflylite.credential.CallerPref;
import tw.com.ischool.fireflylite.login.LoginHelper;

public class MainActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "9403ec217a19a849d498a5c18909bf38";
    public static final String CLIENT_SECRET = "40654f9b8d2ddbf54d8f3059c2d70cd80d4e7e0fa3094d5b19305f945a38f025";
    public static final String REDIRECT_URI = "http://_blank";
    public static final String SCOPE = "User.Mail,User.BasicInfo";

    private static final int REQUEST_CODE = 51234;  //呼叫 LoginActivity 時要傳過去的 Request Code

    private Pupa mPupa;     //處理 組態設定的 Singleton 物件

    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 啟動 OAuth 登入的組態設定 */
        mPupa = Pupa.getInstance() ;
        Pupa.getInstance().setClientID(CLIENT_ID);
        Pupa.getInstance().setClientSecret(CLIENT_SECRET);
        Pupa.getInstance().setRedirectUri(REDIRECT_URI);

        /* 按鈕按下後，會啟動 OAuth 的登入動作 */
        this.btnLogin = (Button)findViewById(R.id.button);
        this.btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //啟動登入動作
                startLogin();
            }
        });
    }

    /**
     * 啟動登入動作。
     * 如果曾登入過 ( Local 有 AccessToken)，則換一個新的，不需要出現登入畫面。
     * 如果狀態為已登出 (Local 沒有 AccessToken)，則出現登入畫面。
     */
    private void startLogin() {

        //開啟 Login Activity。這個 Activity 會自動判斷是否曾登入過。如果尚未登入，才會出現登入畫面。
        LoginHelper.startup(this, mPupa.getClientID() , mPupa.getClientSecret(), SCOPE, mPupa.getRedirectUri(), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //登入成功，不管是初次登入，或是再次開啟。
        if (REQUEST_CODE == requestCode && resultCode == LoginHelper.RESULT_LOGIN_OK) {

            AccountInfo accountInfo = getCurrentUser(MainActivity.this);

            Log.d("DEBUG", accountInfo.getFirstName());
            Log.d("DEBUG", accountInfo.getLoginName());
            Log.d("DEBUG", accountInfo.getUserName());

            TextView txt = (TextView)findViewById(R.id.textView);
            txt.setText("Hello," + accountInfo.getUserName());

        } else if (REQUEST_CODE == requestCode && resultCode == RESULT_CANCELED) {
            finish();   //使用者在未登入動作時，就按取消。
        }
    }

    /* 解析取得目前登入的使用者資訊 */
    private AccountInfo getCurrentUser(Context context) {
        AccountToken token =   CallerPref.get(context);
        AccountInfo result = null;
        if (token != null) {
            result =  new AccountInfo(token.getMyInfoString());
        }
        return result ;
    }
}
