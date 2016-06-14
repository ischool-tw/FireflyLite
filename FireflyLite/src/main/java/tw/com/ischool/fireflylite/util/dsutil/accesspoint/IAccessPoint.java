package tw.com.ischool.fireflylite.util.dsutil.accesspoint;

import java.util.Set;

public interface IAccessPoint {
	String getName();

	boolean isActive();

	Set<String> getRelativeURLs();

	String getMemo();

	String getCaption();

	String getCatalog();
}
