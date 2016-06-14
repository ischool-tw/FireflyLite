package tw.com.ischool.fireflylite.exception.unhandledexception;

import android.app.Activity;
import android.os.Bundle;

public class ExceptionActivity extends Activity {

    public final static String STACKTRACE = "ExceptionActivity.stacktrace";
    public final static String EXCEPTIONMESSAGE = "ExceptionActivity.exception_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_exception);

//        Intent  intent = getIntent();
//
//        String exMessage = intent.getStringExtra(EXCEPTIONMESSAGE);
//        String stackTraceString = intent.getStringExtra(STACKTRACE);
//
//
//        CardView cardView = (CardView)findViewById(R.id.cardExceptionMsg);
//        TextView txt = (TextView)findViewById(R.id.txtExceptionMsg);
//
//        txt.setText(String.format("%s \n  %s", exMessage, stackTraceString ));
//
//        Button btnOK = (Button)findViewById(R.id.btnOK);
//        btnOK.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

    }
}
