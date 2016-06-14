package tw.com.ischool.fireflylite.util.http;

public class Cancelable {

	private boolean _cancel = false;
	private CancelableListener _cancelListener;

	public boolean isCanceled(){
		return _cancel;
	}
	
	public void setCancel(boolean cancel){
		_cancel = cancel;

		if(_cancelListener != null) {
			_cancelListener.onCancelChanged(cancel);
		}
	}

	public void setCancelableListener(CancelableListener listener){
		_cancelListener = listener;
	}

	public interface CancelableListener {
		void onCancelChanged(boolean cancel);
	}
}
