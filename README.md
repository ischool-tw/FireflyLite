# FireflyLite
An Android library you can use to sign in ischool account center.

## Installation :
### Gradle :
compile 'tw.com.ischool:FireflyLite:0.1.0'

## Usage :
        public class MainActivity extends AppCompatActivity {
        
            public static final String CLIENT_ID = "9403ec217a19a849d498a5c18909bf38";
            public static final String CLIENT_SECRET = "40654f9b8d2ddbf54d8f3059c2d70cd80d4e7e0fa3094d5b19305f945a38f025";
            public static final String REDIRECT_URI = "http://_blank";
            public static final String SCOPE = "User.Mail,User.BasicInfo";
        
            private static final int REQUEST_CODE = 51234;  //呼叫 LoginActivity 時要傳過去的 Request Code
        
            private Pupa mPupa;     //處理 組態設定的 Singleton 物件
        
            private Button btnSignIn;
            private Button btnSignOut;
        
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
        
                /* 啟動 OAuth 登入的組態設定 */
                mPupa = Pupa.getInstance() ;
                Pupa.getInstance().setClientID(CLIENT_ID);
                Pupa.getInstance().setClientSecret(CLIENT_SECRET);
                Pupa.getInstance().setRedirectUri(REDIRECT_URI);
        
                /* 登入按鈕按下後，會啟動 OAuth 的登入動作 */
                this.btnSignIn = (Button)findViewById(R.id.btnSignIn);
                this.btnSignIn.setOnClickListener(new View.OnClickListener() {
        
                    @Override
                    public void onClick(View v) {
                        //啟動登入動作
                        startLogin();
                    }
                });
        
                /* 登出按鈕按下後，會清除 local 使用者資訊。 */
                this.btnSignOut = (Button)findViewById(R.id.btnSignout);
                this.btnSignOut.setOnClickListener(new View.OnClickListener() {
        
                    @Override
                    public void onClick(View v) {
                        //啟動登入動作
                        startLogout();
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
        
                    AccountInfo accountInfo = mPupa.getCurrentUser(MainActivity.this);
        
                    Log.d("DEBUG", accountInfo.getFirstName());
                    Log.d("DEBUG", accountInfo.getLoginName());
                    Log.d("DEBUG", accountInfo.getUserName());
        
                    TextView txt = (TextView)findViewById(R.id.textView);
                    txt.setText("Hello," + accountInfo.getUserName());
        
                    btnSignIn.setEnabled(false);
                    btnSignOut.setEnabled(true);
        
                } else if (REQUEST_CODE == requestCode && resultCode == RESULT_CANCELED) {
                    finish();   //使用者在未登入動作時，就按取消。
                }
            }
        
        
            /**
             * 呼叫 LoginHelper.signOut 方法執行登出動作，並指定登出後要執行的事項。
             */
            private void startLogout() {
                LoginHelper.signOut(MainActivity.this, new LoginHelper.SignoutHandler() {
                    @Override
                    public void afterSignOut() {
                        TextView txt = (TextView)findViewById(R.id.textView);
                        txt.setText("您已經登出系統~");
        
                        btnSignIn.setEnabled(true);
                        btnSignOut.setEnabled(false);
                    }
                });
            }
        }
