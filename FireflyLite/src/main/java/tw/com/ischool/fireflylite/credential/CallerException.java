package tw.com.ischool.fireflylite.credential;

/**
 * Created by 西華 on 2015/1/22.
 */
public class CallerException extends Exception {
    public CallerException(){
        super();
    }

    public CallerException(String message){
        super(message);
    }

    public CallerException(String message, Throwable cause){
        super(message,cause);
    }
}
