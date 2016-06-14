package tw.com.ischool.fireflylite.exception.unhandledexception;

import android.content.Context;

import tw.com.ischool.fireflylite.Pupa;
import tw.com.ischool.fireflylite.credential.AccountInfo;

/**
 * 收集已知的 Exception，送到 DSA，例如呼叫 service 失敗等。須由 programmer 呼叫。
 * Created by kevinhuang on 2016/4/2.
 */
public class CollectException {

    private Context mContext ;

    public void collect(Context context, String targetDsns, String moduleName, String targetUserID, String targetUserName, Throwable ex) {

//        final OauthTokenHandler caller = CallerPref.newTokenHandler(context);
        AccountInfo accInfo = Pupa.getInstance().getCurrentUser(context);
        String loginName = (accInfo == null) ? "" : accInfo.getLoginName();

        final String packageName = (context == null ? "" : context.getPackageName());
//        final String moduleName = context.getResources().getString(R.string.module_name);

        new SendDSAExceptionTask(loginName, moduleName, packageName, targetUserID, targetUserName, targetDsns, context, ex, new SendDSAExceptionTask.AfterSendHandler() {
            @Override
            public void AfterSend() {

            }
        }).execute();
    }
}
