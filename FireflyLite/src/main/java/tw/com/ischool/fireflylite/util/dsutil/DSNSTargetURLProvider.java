package tw.com.ischool.fireflylite.util.dsutil;

import java.util.Locale;

import tw.com.ischool.fireflylite.util.dsutil.accesspoint.AccessPoint;


public class DSNSTargetURLProvider implements ITargetURLProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _targetURL;
	private String _original;
	
	public DSNSTargetURLProvider(String dsnsName) {
		_original = dsnsName;
		
		String lowName = dsnsName.toLowerCase(Locale.getDefault());
		if(lowName.startsWith("http://") || lowName.startsWith("https://"))
			_targetURL = dsnsName;
		else
			_targetURL = AccessPoint.getDoorwayURL(dsnsName);
	}

	@Override
	public String getTargetURL() {
		return _targetURL;
	}

	@Override
	public String getOriginal() {
		return _original;
	}
}
