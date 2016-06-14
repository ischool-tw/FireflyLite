package tw.com.ischool.fireflylite.util.dsutil.accesspoint;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractAccessPoint implements IAccessPoint {

	protected String _name;
	protected boolean _active;
	protected String _memo;
	protected Set<String> _relativeURLs;
	protected String _caption;
	protected String _catalog;

	protected AbstractAccessPoint() {
		_relativeURLs = new HashSet<String>();
	}

	@Override
	public synchronized String getName() {
		return _name.toLowerCase();
	}

	@Override
	public synchronized boolean isActive() {
		return _active;
	}

	@Override
	public  synchronized Set<String> getRelativeURLs() {
		return _relativeURLs;
	}

	@Override
	public synchronized String getMemo() {
		return _memo;
	}

	@Override
	public synchronized String getCaption() {
		return _caption;
	}

	public synchronized String getCatalog() {
		return _catalog;
	}
}
