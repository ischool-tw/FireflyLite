package tw.com.ischool.fireflylite.util.dsutil;

import java.io.Serializable;

public interface ITargetURLProvider extends Serializable {
	public String getOriginal();

	public String getTargetURL();
}
