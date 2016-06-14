package tw.com.ischool.fireflylite.exception.unhandledexception;

import android.app.Activity;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

import tw.com.ischool.fireflylite.Pupa;
import tw.com.ischool.fireflylite.credential.AccountInfo;

/**
 * 專門收集 programmer 未處理的例外狀況。
 * 使用方法：在每個 Activity 的 onCreate 事件中：
 * UnHandledExceptionHandler  uhHandler = new UnHandledExceptionHandler(MainActivity.this);
 * Created by kevinhuang on 2016/4/1.
 */
public class UnHandledExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static final String EXTRA_MY_EXCEPTION_HANDLER = "EXTRA_MY_EXCEPTION_HANDLER";
    private final Activity context;
    private final Thread.UncaughtExceptionHandler rootHandler;

    private String mTargetDSNS ="";
    private String mTargetUserID ="";
    private String mTargetUserName="" ;
    private String mModuleName="" ;

    public void setTargetDSNS(String mTargetDSNS) {
        this.mTargetDSNS = mTargetDSNS;
    }

    public void setTargetUserID(String mTargetUserID) {
        this.mTargetUserID = mTargetUserID;
    }

    public void setTargetUserName(String mTargetUserName) {
        this.mTargetUserName = mTargetUserName;
    }

    public void setModuleName(String mModuleName) {
        this.mModuleName = mModuleName;
    }




    public UnHandledExceptionHandler(Activity context) {

        this.context = context;
        // we should store the current exception handler -- to invoke it for all not handled exceptions ...
        rootHandler = Thread.getDefaultUncaughtExceptionHandler();
        // we replace the exception handler now with us -- we will properly dispatch the exceptions ...
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {

//        if (ex instanceof MyAuthException) {
        // note we can't just open in Android an dialog etc. we have to use Intents here
        // http://stackoverflow.com/questions/13416879/show-a-dialog-in-thread-setdefaultuncaughtexceptionhandler\

        StringBuilder sb = new StringBuilder();
        for(StackTraceElement elm : ex.getStackTrace()) {
            sb.append(elm.toString());
            sb.append("\n");
        }
//        this.mStackTraceString = sb.toString();



//        registerActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        registerActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
//
//        context.startActivity(registerActivity);
//        // make sure we die, otherwise the app will hang ...
//        final OauthTokenHandler caller = CallerPref.newTokenHandler(context);
        AccountInfo accInfo = Pupa.getInstance().getCurrentUser(context);
        String loginName = (accInfo == null) ? "" : accInfo.getLoginName();

        SendDSAExceptionTask task =  new SendDSAExceptionTask(loginName, mModuleName, context.getPackageName(), mTargetUserID, mTargetUserName , mTargetDSNS, context, ex, new SendDSAExceptionTask.AfterSendHandler() {
            @Override
            public void AfterSend() {

            }
        });

        task.execute();

        try {
            TimeUnit.SECONDS.sleep(1);  //delay for 1second

        }
        catch(Exception exTime) {

        }

        Intent registerActivity = new Intent(context, ExceptionActivity.class);
        registerActivity.putExtra(ExceptionActivity.STACKTRACE, sb.toString());
        registerActivity.putExtra(ExceptionActivity.EXCEPTIONMESSAGE, ex.getLocalizedMessage());

//        this.context.startActivity(registerActivity);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);

    }
}

