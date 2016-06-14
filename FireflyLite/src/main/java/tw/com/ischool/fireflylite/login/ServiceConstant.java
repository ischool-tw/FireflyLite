package tw.com.ischool.fireflylite.login;

/**
 * Created by kevinhuang on 2016/4/21.
 */
public class ServiceConstant {
    public static final String CONTRACT_PARENT = "1campus.mobile.parent";
    public static final String CONTRACT_TEACHER = "1campus.mobile.teacher";
    public static final String CONTRACT_STUDENT = "1campus.mobile.student";

    public static final String SERVICE_GET_MY_BABY = "main.GetMyChildren";
    public static final String SERVICE_BASE_CONNECT = "Base.Connect";
    public static final String SERVICE_TEACHER_GET_MY_INFO = "main.GetMyInfo";

    public static final String URL_FIND_APPINFO = "http://devg.ischool.com.tw/dsa/campusman.ischool.com.tw/config.public/GetSchoolList?content=<Request><Match>%s</Match><Pagination><PageSize>10</PageSize><StartPage>1</StartPage></Pagination></Request>";
}
