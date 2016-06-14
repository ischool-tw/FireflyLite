package tw.com.ischool.fireflylite.util.dsutil;

public interface OnReceiveListener<T> {
	void onReceive(T result);

	void onError(Exception ex);
}