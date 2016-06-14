package tw.com.ischool.fireflylite.exception.unhandledexception;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kevinhuang on 2016/4/2.
 */
public class SendDSAExceptionTask extends AsyncTask<String, Void, String> {
    private String mUserID;
    private String mModuleName;
    private String mAppPackage ;
    private String mTargetUserID;
    private String mTargetUserName;
    private String mTargetDSNS ;
    private Context mContext ;
    private Throwable mEx ;
    private String mStackTraceString ;

    private AfterSendHandler _handler ;

    public SendDSAExceptionTask(String userID, String moduleName, String app_package, String target_userid, String target_username, String target_dsns,  Context context ,Throwable paramThrowable , AfterSendHandler handler) {
        this.mUserID = userID;
        this.mEx = paramThrowable ;
        this.mModuleName = moduleName;
        this.mContext = context ;
        this.mAppPackage = app_package;

        this.mTargetUserID = target_userid ;
        this.mTargetUserName = target_username ;
        this.mTargetDSNS = target_dsns ;
        this._handler = handler ;

        this.mStackTraceString = makeExceptionMsg(paramThrowable);
//        StringBuilder sb = new StringBuilder();
//        for(StackTraceElement elm : paramThrowable.getStackTrace()) {
//            sb.append(elm.toString());
//            sb.append("\n");
//        }
//        this.mStackTraceString = sb.toString();

    }

    private String makeExceptionMsg(Throwable e) {
        StackTraceElement[] arr = e.getStackTrace();
        final StringBuffer report = new StringBuffer(e.toString());
        final String lineSeperator = "-------------------------------\n\n";
        final String SINGLE_LINE_SEP = "\n";
        final String DOUBLE_LINE_SEP = "\n\n";

        report.append(DOUBLE_LINE_SEP);
        report.append("--------- Stack trace ---------\n\n");
        for (int i = 0; i < arr.length; i++) {
            report.append( "    ");
            report.append(arr[i].toString());
            report.append(SINGLE_LINE_SEP);
        }
        report.append(lineSeperator);
        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        report.append("--------- Cause ---------\n\n");
        Throwable cause = e.getCause();
        if (cause != null) {
            report.append(cause.toString());
            report.append(DOUBLE_LINE_SEP);
            arr = cause.getStackTrace();
            for (int i = 0; i < arr.length; i++) {
                report.append("    ");
                report.append(arr[i].toString());
                report.append(SINGLE_LINE_SEP);
            }
        }
        // Getting the Device brand,model and sdk verion details.
        report.append(lineSeperator);
        report.append("--------- Device ---------\n\n");
        report.append("Brand: ");
        report.append(Build.BRAND);
        report.append(SINGLE_LINE_SEP);
        report.append("Device: ");
        report.append(Build.DEVICE);
        report.append(SINGLE_LINE_SEP);
        report.append("Model: ");
        report.append(Build.MODEL);
        report.append(SINGLE_LINE_SEP);
        report.append("Id: ");
        report.append(Build.ID);
        report.append(SINGLE_LINE_SEP);
        report.append("Product: ");
        report.append(Build.PRODUCT);
        report.append(SINGLE_LINE_SEP);
        report.append(lineSeperator);
        report.append("--------- Firmware ---------\n\n");
        report.append("SDK: ");
        report.append(Build.VERSION.SDK);
        report.append(SINGLE_LINE_SEP);
        report.append("Release: ");
        report.append(Build.VERSION.RELEASE);
        report.append(SINGLE_LINE_SEP);
        report.append("Incremental: ");
        report.append(Build.VERSION.INCREMENTAL);
        report.append(SINGLE_LINE_SEP);
        report.append(lineSeperator);

        return report.toString();
    }

    @Override
    protected String doInBackground(String... urls) {
        String response = "";
        try {
            URL url = new URL("https://devg.ischool.com.tw/dsa/dev.sh_d");
            String urlParameters = "<Envelope>\n" +
                    "    <Header>\n" +
                    "        <TargetContract>1campus.mobile.debug</TargetContract>\n" +
                    "        <TargetService>_.SendMsg</TargetService> \n" +
                    "        <SecurityToken Type=\"PassportAccessToken\">\n" +
                    "\t\t\t<AccessToken>d7bda23cec22fdb4c34cd678cb45ffd7</AccessToken>\n" +
                    "\t\t</SecurityToken>\n" +
                    "    </Header>\n" +
                    "    <Body>\n" +
                    "        <Request>\n" +
                    "            <Trace>\n" +
                    "                <LocalizeMessage>%s</LocalizeMessage>\n" +
                    "                <CallStack>%s</CallStack>\n" +
                    "                <DeviceModel>%s</DeviceModel>\n" +
                    "                <Manufactory>%s</Manufactory>\n" +
                    "                <Platform>%s</Platform>\n" +
                    "                <Remark>%s</Remark>\n" +
                    "                <SDKNo>%s</SDKNo>\n" +
                    "                <UserID>%s</UserID>\n" +
                    "                <ModuleName>%s</ModuleName>\n" +
                    "                <AppPackage>%s</AppPackage>\n" +
                    "                <TargetUserID>%s</TargetUserID>\n" +
                    "                <TargetUserName>%s</TargetUserName>\n" +
                    "                <TargetDSNS>%s</TargetDSNS>\n" +
                    "            </Trace>\n" +
                    "        </Request>\n" +
                    "    </Body>\n" +
                    "</Envelope>";

            urlParameters = String.format(urlParameters, mEx.getLocalizedMessage(),  mStackTraceString,
                    Build.MODEL, Build.MANUFACTURER,"android","", Build.VERSION.SDK_INT,
                    mUserID , mModuleName , mAppPackage, mTargetUserID, mTargetUserName,  mTargetDSNS);

            Log.d("exception", mStackTraceString);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
            connection.setDoOutput(true);
            //寫入參數
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(urlParameters);
            bw.flush();
            bw.close();

            //送出 reqeust，並取得 response
            int responseCode = connection.getResponseCode(); // getting the response code
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder responseOutput = new StringBuilder();
            System.out.println("output===============" + br);
            while((line = br.readLine()) != null ) {
                responseOutput.append(line);
            }
            br.close();

            response = responseOutput.toString();

        }
        catch(Exception ex) {
            Log.d("err", ex.getLocalizedMessage());
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
//            textView.setText(result);
        if (_handler != null) {
            _handler.AfterSend();
        }
    }

    public interface AfterSendHandler {
        void AfterSend();
    }


}
