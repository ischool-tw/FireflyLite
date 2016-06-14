package tw.com.ischool.fireflylite.util.dsutil;

import java.util.ArrayList;
import java.util.List;
import tw.com.ischool.fireflylite.util.StringUtil;


public class DSScope {
	public static final String UNSPECIFIED_CONTRACT = "*";
	private String _application;
	private String _contract;
	private boolean _isEmpty;

	private DSScope() {
		_application = "";
		_contract = UNSPECIFIED_CONTRACT;
		_isEmpty = true;
	}

	public DSScope(String application, String contract) {
		_application = application;
		_contract = contract;
		_isEmpty = false;
	}

	public String getApplication() {
		return _application;
	}

	public String getContract() {
		return _contract;
	}

	public boolean isDsnsApplicationName() {
		String lower = _application.toLowerCase();
		if (lower.startsWith("http://") || lower.startsWith("https://"))
			return false;
		return true;
	}

	public boolean isSpecifiedContract() {
		if (_contract.equals(UNSPECIFIED_CONTRACT))
			return false;
		return true;
	}

	public boolean isEmpty() {
		return _isEmpty;
	}

	public boolean validScope(String appName, String contractName) {
		if (!appName.equals(_application))
			return false;

		if (this.isSpecifiedContract())
			return true;

		if (_contract.equals(contractName))
			return true;

		return false;
	}

	public boolean validScope(DSScope scope) {
		return validScope(scope.getApplication(), scope.getApplication());
	}

	public String toString() {
		if (_contract.isEmpty())
			return _application;
		return _application + ":" + _contract;
	}

	public static final DSScope empty() {
		return new DSScope();
	}



	public static List<DSScope> parse(String scopeString) {
		ArrayList<DSScope> scopes = new ArrayList<DSScope>();

		if (StringUtil.isNullOrWhitespace(scopeString)) {
			return scopes;
		}

		try {
			String[] ss = scopeString.split(",");
			for (String s : ss) {
				String[] ac = s.split(":");
				String application = ac[0];
				String contract = "*";
				if(ac.length > 1)
					contract = ac[1];
				
				DSScope scope = new DSScope(application, contract);
				scopes.add(scope);
			}
		} catch (Exception ex) {
			throw new RuntimeException("Scope parse error", ex);
		}
		return scopes;
	}
	
	public static void main(String[] args){
		String str = "";
		for(DSScope scope : parse(str)){
			System.out.println(scope.toString());
		}
				
	}
}
