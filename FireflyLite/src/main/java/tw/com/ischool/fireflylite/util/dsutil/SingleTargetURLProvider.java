package tw.com.ischool.fireflylite.util.dsutil;

public class SingleTargetURLProvider implements ITargetURLProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _targetURL;

	public SingleTargetURLProvider(String url) {
		_targetURL = url;
	}

	@Override
	public String getTargetURL() {
		return _targetURL;
	}

	@Override
	public String getOriginal() {
		return _targetURL;
	}
}
