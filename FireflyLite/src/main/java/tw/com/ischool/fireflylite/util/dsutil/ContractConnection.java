package tw.com.ischool.fireflylite.util.dsutil;

import android.os.AsyncTask;

import org.w3c.dom.Element;

import java.io.Serializable;
import java.security.PublicKey;

import tw.com.ischool.fireflylite.exception.DisconnectedException;
import tw.com.ischool.fireflylite.exception.UnsupportPassportException;
import tw.com.ischool.fireflylite.exception.UnsupportSessionException;
import tw.com.ischool.fireflylite.util.http.Cancelable;
import tw.com.ischool.fireflylite.util.http.HttpUtil;
import tw.com.ischool.fireflylite.util.dsutil.token.ITokenProvider;
import tw.com.ischool.fireflylite.util.XmlHelper;
import tw.com.ischool.fireflylite.util.XmlUtil;
import tw.com.ischool.fireflylite.util.StringUtil;


public class ContractConnection implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String SERVICE_CONNECT = "DS.Base.Connect";
	public static final String SERVICE_GET_PASSPORT = "DS.Base.GetPassportToken";
	public static final String INFO_CONTRACT = "info";
	public static final String SERVICE_GET_CONTRACT_PUBLIC_KEY = "Public.GetPublicKey";
	public static final String SERVICE_GET_APPLICATION_PUBLIC_KEY = "Public.GetApplicationPublicKey";
	public static final String SERIVCE_GET_CONTRACT_USAGE = "GetContractUsage";
	public static final int DEFAULT_TIMEOUT = 60000;

	// private Version _serverVersion;
	private String _targetContract;
	private ITargetURLProvider _urlProvider;
	private String _oriSecElement;
	private String _sessionid;
	private boolean _useSession;
	private int _timeout = -1;
	// private PublicKey _targetContractPublicKey;
	// private DSUserInfo _userInfo;
	private boolean _connected;
	// private KeyPair _enhancedKeyPair;
	private String _contractUsage;
	private String _applicationComment;
	private String _lastDelayService;

	public ContractConnection(String appUrlOrDSNS, String targetContract) {
		_urlProvider = new AutoSwitchURLProvider(appUrlOrDSNS);
		_targetContract = targetContract;
	}

	public ContractConnection(ITargetURLProvider urlProvider,
							  String targetContract) {
		_urlProvider = urlProvider;
		_targetContract = targetContract;
	}

	public ContractConnection(ITargetURLProvider urlProvider) {
		_urlProvider = urlProvider;
		_targetContract = "";
	}

	public ContractConnection(String appUrlOrDSNS) {
		_urlProvider = new AutoSwitchURLProvider(appUrlOrDSNS);
	}

	public String getTargetContract() {
		return _targetContract;
	}

	public String getTargetApplicationURL() {
		return _urlProvider.getTargetURL();
	}

	public void connect(ITokenProvider tokenProvider, boolean useSession) {
		Element oriSecElement = tokenProvider.getSecurityToken();
		_oriSecElement = XmlHelper.convertToString(oriSecElement);
		this.connect(useSession);
	}

	public void connect(String username, String password, boolean useSession) {
		Element oriSecElement = XmlUtil.createElement("SecurityToken");
		oriSecElement.setAttribute("Type", "Basic");
		XmlUtil.addCDATASection(oriSecElement, "UserName", username);
		XmlUtil.addCDATASection(oriSecElement, "Password", password);
		_oriSecElement = XmlHelper.convertToString(oriSecElement);
		this.connect(useSession);
	}

	public void connect(Element passport, boolean useSession) {
		Element oriSecElement = XmlUtil.createElement("SecurityToken");
		oriSecElement.setAttribute("Type", "Passport");
		XmlUtil.appendElement(oriSecElement, passport);
		_oriSecElement = XmlHelper.convertToString(oriSecElement);
		this.connect(useSession);
	}

	public void connect(DSAPassport passport, boolean useSession) {
		this.connect(passport.getPassportTokenElement(), useSession);
	}

	public void connect(String sessionid, boolean useSession) {
		Element oriSecElement = XmlUtil.createElement("SecurityToken");
		oriSecElement.setAttribute("Type", "Session");

		_oriSecElement = XmlHelper.convertToString(oriSecElement);

		XmlUtil.addCDATASection(oriSecElement, "SessionID", sessionid);

		this.connect(useSession);
	}

	// public void connect(String username, String password, KeyPair keypair,
	// boolean useSession) {
	// _enhancedKeyPair = keypair;
	//
	// String nowString = Converter.toDateString(Calendar.getInstance()
	// .getTime());
	//
	// _oriSecElement = XmlUtil.createElement("SecurityToken");
	// _oriSecElement.setAttribute("Type", "Enhanced");
	// XmlUtil.addCDATASection(_oriSecElement, "UserName", username);
	// XmlUtil.addCDATASection(_oriSecElement, "Password", password);
	// XmlUtil.addCDATASection(_oriSecElement, "Timestamp", nowString);
	//
	// String signString = username + password + nowString;
	// String signedString;
	// try {
	// // signedString = KeyPairUtil.sign(keypair.getPrivate(),
	// // signString);
	// // KeyPairUtil.signElement(keypair, _oriSecElement);
	// } catch (Exception e) {
	// throw new RuntimeException(
	// "Signing enhanced security token occured an exception : "
	// + e.getMessage(), e);
	// }
	// // XmlUtil.addCDATASection(_oriSecElement, "SignatureValue",
	// // signedString);
	// this.connect(useSession);
	// }

	public DSResponse sendRequest(String targetService, Cancelable cancelable) {
		DSRequest req = new DSRequest();
		return sendRequest(targetService, req, cancelable);
	}

	public DSResponse sendRequest(String targetService, DSRequest request,
			Cancelable cancelable) {
		request.setTargetContract(_targetContract);
		request.setSecurityToken(this.getSecurityToken());
		request.setTargetService(targetService);

		String targetURL = _urlProvider.getTargetURL();
		String reqString = request.getXML();
		String result = HttpUtil.postDataForString(targetURL, reqString,
				this.getTimeout(), cancelable);

		if (cancelable != null && cancelable.isCanceled()) {
			return null;
		}

		DSResponse rsp = new DSResponse(result);
		handleResponse(rsp);
		return rsp;
	}

	/*
	 * vvvvvvvvvvvvvvvvvvvv Add By Kevin (directed by Lucifer)
	 * vvvvvvvvvvvvvvvvvvvvvvvvvv
	 */
	public void getJSONString(String targetService, DSRequest request,
			OnReceiveListener<String> listener, Cancelable cancelable) {

		request.setTargetContract(_targetContract);
		request.setSecurityToken(getSecurityToken());
		request.setTargetService(targetService);
		request.setParameter("rsptype", "json");

		StringAsyncTask task = new StringAsyncTask(cancelable);
		task.setOnReceiveListener(listener);
		task.execute(request);
	}

	private class ResponseTask extends AsyncTask<DSRequest, Void, DSResponse> {

		private OnReceiveListener<DSResponse> mListener;
		private Exception mEx;
		private Cancelable mCancelable;
		private String mTargetService;

		public ResponseTask(String targetService, Cancelable cancelable) {
			mCancelable = cancelable;
			mTargetService = targetService;
		}

		@Override
		protected DSResponse doInBackground(DSRequest... params) {
			DSResponse response = null;
			try {
				if (params.length > 0)
					response = sendRequest(mTargetService, params[0],
							mCancelable);
				else
					response = sendRequest(mTargetService, mCancelable);
			} catch (Exception ex) {
				mEx = ex;
			}
			return response;
		}

		@Override
		protected void onPostExecute(DSResponse result) {
			if (mListener == null)
				return;

			if (mEx != null)
				mListener.onError(mEx);
			else if (result == null) 
				mListener.onReceive(new DSResponse());
			else {
				mListener.onReceive(result);
			}
		}

		public void setOnReceiveListener(OnReceiveListener<DSResponse> listener) {
			mListener = listener;
		}
	}

	/**
	 * 這個 class 是俊傑寫的, 我看不太懂為什麼他這麼寫, 感覺有 BUG 我寫另一個 ResponseTask 來取代好了
	 * **/
	private class StringAsyncTask extends AsyncTask<DSRequest, Void, String> {

		private OnReceiveListener<String> mListener;
		private Exception mEx;
		private Cancelable mCancelable;

		public StringAsyncTask(Cancelable cancelable) {
			mCancelable = cancelable;
		}

		@Override
		protected String doInBackground(DSRequest... requests) {
			// TODO something wrong
			String result = null;
			try {
				DSRequest request = null;
				if (requests.length > 0) {
					request = requests[0];
					String targetURL = _urlProvider.getTargetURL();
					String reqString = request.getXML();
					result = HttpUtil.postDataForString(targetURL, reqString,
							getTimeout(), mCancelable);
				} else
					mEx = new Exception("No DSRequest !");
			} catch (Exception ex) {
				mEx = ex;
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (mListener == null)
				return;

			if (mEx != null)
				mListener.onError(mEx);
			else {
				mListener.onReceive(result);
			}
		}

		public void setOnReceiveListener(OnReceiveListener<String> listener) {
			mListener = listener;
		}

	}

	public ContractConnection clone() {
		ContractConnection cn = new ContractConnection(
				_urlProvider.getTargetURL(), _targetContract);
		cn._sessionid = this._sessionid;
		return cn;
	}

	/* ^^^^^^^^^^^^^^^^^^^^^ ^^^^^^^^^^^^^^^^^^^^^ */

	// public DSResponse sendSecureReqeust(String targetService, String
	// cryptoType) {
	// DSRequest request = new DSRequest();
	// return sendSecureRequest(targetService, request, cryptoType);
	// }
	//
	// public DSResponse sendSecureReqeust(String targetService) {
	// DSRequest request = new DSRequest();
	// return sendSecureRequest(targetService, request);
	// }
	//
	// public DSResponse sendSecureRequest(String targetService, DSRequest
	// request) {
	// return sendSecureRequest(targetService, request, "dynamic");
	// }

	// public DSResponse sendSecureRequest(String targetService,
	// DSRequest request, String cryptoType) {
	// request.setTargetContract(_targetContract);
	// request.setSecurityToken(this.getSecurityToken());
	// request.setTargetService(targetService);
	//
	// Element newElement = XmlUtil.createElement("Envelope");
	// Element headElement = XmlUtil.addElement(newElement, "Header");
	// Element bodyElement = XmlUtil.addElement(newElement, "Body");
	// XmlUtil.addElement(bodyElement, "Content");
	//
	// XmlUtil.addElement(headElement, "TargetContract", _targetContract);
	// Element ctElement = XmlUtil.addElement(headElement, "CryptoToken");
	//
	// boolean dynamic = false;
	// if (cryptoType.equalsIgnoreCase("dynamic")) {
	// dynamic = true;
	// cryptoType = "Dynamic";
	// } else {
	// cryptoType = "Static";
	// }
	// ctElement.setAttribute("Type", cryptoType);
	//
	// // 處理 secret key
	// String secretKey = UUID.randomUUID().toString();
	// PublicKey publicKey = getTargetContractPublicKey();
	// // String encSecKey = KeyPairUtil.encrypt(publicKey, secretKey);
	// // XmlUtil.addElement(ctElement, "SecretKey", encSecKey);
	//
	// // 處理 public key
	// // KeyPair myKey = KeyPairUtil.generatorKeyPair();
	// // Element myPubKeyElement = KeyPairUtil.genPubKeyElement(myKey);
	// // String myPubKeyString = XmlHelper.convertToString(myPubKeyElement,
	// // false);
	// // String encMyPubKeyString = "";
	// // try {
	// // encMyPubKeyString = AESHelper.encrypt(myPubKeyString, secretKey);
	// // } catch (Exception e) {
	// // throw new DSAServerException(
	// //
	// DSStatus.UnhandledException("Encrypt public key occured an exception."),
	// // e);
	// // }
	// // XmlUtil.addElement(ctElement, "PublicKey", encMyPubKeyString);
	//
	// // 處理 cipher
	// String oriReqString = request.getXML();
	// String cipher = "";
	// try {
	// cipher = AESHelper.encrypt(oriReqString, secretKey);
	// } catch (Exception e) {
	// throw new DSAServerException(
	// DSStatus.UnhandledException("Encrypt cipher occured an exception."),
	// e);
	// }
	// XmlUtil.addElement(ctElement, "Cipher", cipher);
	//
	// String newData = XmlHelper.convertToString(newElement);
	// Element rspElement = null;
	// // 傳送 request
	// try {
	// rspElement = HttpUtil.postDataForElement(
	// _urlProvider.getTargetURL(), newData, this.getTimeout());
	// } catch (Exception ex) {
	// throw new DSAServerException(
	// DSStatus.UnhandledException("Post data occured an exception."),
	// ex);
	// }
	//
	// // 處理 response
	// headElement = XmlUtil.selectElement(rspElement, "Header");
	// ctElement = XmlUtil.selectElement(headElement, "CryptoToken");
	// cipher = XmlUtil.getElementText(ctElement, "Cipher");
	//
	// if (dynamic) {
	// // secretKey = XmlUtil.getElementText(ctElement, "SecretKey");
	// // secretKey = KeyPairUtil.decrypt(myKey.getPrivate(), secretKey);
	// }
	//
	// String oriRspString = "";
	// try {
	// oriRspString = AESHelper.decrypt(cipher, secretKey, "UTF-8");
	// } catch (Exception e) {
	// throw new DSAServerException(
	// DSStatus.UnhandledException("Decrypt response occured an exception."),
	// e);
	// }
	//
	// DSResponse rsp = new DSResponse(oriRspString);
	// handleResponse(rsp);
	// return rsp;
	// }


	public void sendAsyncRequest(String targetService, DSRequest request,
			final OnReceiveListener<DSResponse> listener, Cancelable cancelable) {
		ResponseTask task = new ResponseTask(targetService, cancelable);
		task.setOnReceiveListener(new OnReceiveListener<DSResponse>() {

			@Override
			public void onReceive(DSResponse result) {
				if (listener != null)
					listener.onReceive(result);

			}

			@Override
			public void onError(Exception ex) {
				if (listener != null)
					listener.onError(ex);
			}
		});
		task.execute(request);
	}


	public void setTimeout(int timeoutInMillis) {
		_timeout = timeoutInMillis;
	}

	public PublicKey getTargetContractPublicKey() {
		// if (_targetContractPublicKey != null)
		// return _targetContractPublicKey;
		//
		// Element e = this.getTargetContractPublicKeyElement();
		// return KeyPairUtil.loadPubKey(e);
		return null;
	}

	public Element getTargetContractPublicKeyElement() {
		DSRequest req = new DSRequest();
		req.setTargetContract(INFO_CONTRACT);

		Element securityToken = XmlUtil.createElement("SecurityToken");
		securityToken.setAttribute("Type", "Public");
		req.setSecurityToken(securityToken);

		req.setTargetService(SERVICE_GET_CONTRACT_PUBLIC_KEY);

		Element content = XmlUtil.createElement("Content");
		XmlUtil.addElement(content, "Format", "pkcs8");
		XmlUtil.addElement(content, "Contract", _targetContract);
		req.setContent(content);

		try {
			Element e = HttpUtil.postDataForElement(
					_urlProvider.getTargetURL(), req.getXML(), 100000);
			return e;
		} catch (Exception ex) {
			throw new DSAServerException(
					DSStatus.ConnectionError("Get target contract public occured an exception."),
					ex);
		}
	}

	// public PublicKey getTargetApplicationPublicKey() {
	// if (_targetContractPublicKey != null)
	// return _targetContractPublicKey;
	//
	// Element e = this.getTargetApplicationPublicKeyElement();
	// return KeyPairUtil.loadPubKey(e);
	// // return null;
	// }

	public Element getTargetApplicationPublicKeyElement() {
		DSRequest req = new DSRequest();
		req.setTargetContract(INFO_CONTRACT);

		Element securityToken = XmlUtil.createElement("SecurityToken");
		securityToken.setAttribute("Type", "Public");
		req.setSecurityToken(securityToken);

		req.setTargetService(SERVICE_GET_APPLICATION_PUBLIC_KEY);

		try {
			Element e = HttpUtil.postDataForElement(
					_urlProvider.getTargetURL(), req.getXML(), 100000);
			return e;
		} catch (Exception ex) {
			throw new DSAServerException(
					DSStatus.ConnectionError("Get target application public occured an exception."),
					ex);
		}
	}

	public DSAPassport getPassport() {
		if (!_connected)
			throw new DisconnectedException();

		DSResponse rsp = null;
		try {
			DSRequest req = new DSRequest();
			// if (_serverVersion.compareTo("4.4.0.0") >= 0) {
			// Element e = XmlUtil.createElement("Request");
			// XmlUtil.addElement(e, "Version", "2.0");
			// req.setContent(e);
			// }
			rsp = sendRequest(SERVICE_GET_PASSPORT, req, new Cancelable());
		} catch (DSAServiceException ex) {
			if (ex.getCode().equals("001"))
				throw new UnsupportPassportException();
		}

		Element content = rsp.getContent();

		return DSAPassport.parseFromPassportToken(content);
	}

	// public DSUserInfo getUserInfo() {
	// return _userInfo;
	// }

	public boolean isConnected() {
		return _connected;
	}

	public ContractConnection connectAnotherByPassport(
			String anotherAccessPoint, String anotherContract,
			boolean useSession) {
		ContractConnection con = new ContractConnection(anotherAccessPoint,
				anotherContract);
		con.connect(this.getPassport(), useSession);

		return con;
	}

	public void connectAnotherByPassportAsync(final String anotherAccessPoint,
			final String anotherContract, final boolean useSession,
			final OnReceiveListener<ContractConnection> listener) {
		AsyncTask<Void, Void, ContractConnection> task = new AsyncTask<Void, Void, ContractConnection>() {
			private Exception _exception;

			@Override
			protected ContractConnection doInBackground(Void... params) {
				try {
					return connectAnotherByPassport(anotherAccessPoint,
							anotherContract, useSession);
				} catch (Exception ex) {
					_exception = ex;
					return null;
				}
			}

			@Override
			protected void onPostExecute(ContractConnection result) {
				if (listener == null)
					return;
				if (_exception != null)
					listener.onError(_exception);
				else if (result != null)
					listener.onReceive(result);
			}
		};
		task.execute();
	}

	public void reconnect() {
		if (!_connected)
			throw new DisconnectedException();

		// TODO 若是 enhance 認證, 原有的 security token 將無法使用, 必須重新產生
		// String type = _oriSecElement.getAttribute("Type");
		//
		// if (type.equalsIgnoreCase("Enhanced")) {
		// String username = XmlUtil
		// .getElementText(_oriSecElement, "UserName");
		// String password = XmlUtil
		// .getElementText(_oriSecElement, "Password");
		// connect(username, password, _enhancedKeyPair, _useSession);
		// } else {
		connect(_useSession);
		// }
	}

	private void handleResponse(DSResponse response) {
		if (response.getStatus().equals(DSStatus.Successful()))
			return;

		if (response.getStatus().code()
				.equals(DSStatus.ServiceExecutionError().code())) {
			throw new DSAServiceException(response);
		}

		DSAServerException exception = new DSAServerException(
				response.getStatus());

		throw exception;
	}

	private int getTimeout() {
		if (_timeout == -1)
			return DEFAULT_TIMEOUT;
		return _timeout;
	}

	private void connect(boolean useSession) {
		_connected = false;
		_useSession = useSession;

		DSRequest req = new DSRequest();
		req.setTargetContract(_targetContract);
		req.setSecurityToken(this.getOriginalSecurityTokenElement());
		req.setTargetService(SERVICE_CONNECT);

		// Element userAgentElement = UserAgentProvider.getClientInfoToken();
		// req.setUserAgent(userAgentElement);

		if (useSession) {
			req.setContent(XmlUtil.createElement("RequestSessionID"));
		}

		DSResponse response = null;

		try {
			// response = this.sendSecureRequest(SERVICE_CONNECT, req);
			response = this.sendRequest(SERVICE_CONNECT, req, new Cancelable());
		} catch (DSAServiceException ex) {
			if (ex.getCode().equals("001"))
				throw new UnsupportSessionException();
		}

		// Element header = response.getHeader();
		// Element userInfoElement = XmlUtil.selectElement(header, "UserInfo");

		// _serverVersion = new Version(XmlUtil.getElementText(header,
		// "Version"));
		// _userInfo = DSUserInfo.load(userInfoElement);
		_connected = true;

		if (!useSession)
			return;

		_sessionid = XmlUtil.getElementText(response.getBody(), "SessionID");
	}

	private Element getSecurityToken() {
		if (!_useSession || !_connected) {

			// 未連線, 不使用 session, 則使用 public 來認證
			if (StringUtil.isNullOrWhitespace(_oriSecElement)) {
				Element publicStt = XmlUtil.createElement("SecurityToken");
				publicStt.setAttribute("Type", "Public");
				return publicStt;
			}
			return getOriginalSecurityTokenElement();
		}

		Element stt = XmlUtil.createElement("SecurityToken");
		stt.setAttribute("Type", "Session");
		XmlUtil.addCDATASection(stt, "SessionID", _sessionid);
		return stt;
	}

	private Element getOriginalSecurityTokenElement() {
		return XmlHelper.parseXml(_oriSecElement);
	}

//	public String getContractUsage() {
//		if (_contractUsage != null)
//			return _contractUsage;
//
//		loadUsage();
//		return _contractUsage;
//	}
//
//	public String getApplicationComment() {
//		if (_applicationComment != null)
//			return _applicationComment;
//
//		loadUsage();
//		return _applicationComment;
//	}

//	private synchronized void loadUsage() {
//		if (StringHelper.isNullOrEmpty(_urlProvider.getTargetURL())) {
//			_applicationComment = "Not exists in dsa application directory.";
//			_contractUsage = "";
//			return;
//		}
//
//		ContractConnection cc = new ContractConnection(
//				_urlProvider.getTargetURL(), INFO_CONTRACT);
//		try {
//			Element req = XmlUtil.createElement("Request");
//			XmlUtil.addElement(req, "Contract", _targetContract);
//			DSRequest request = new DSRequest();
//			request.setContent(req);
//
//			DSResponse rsp = cc.sendRequest(SERIVCE_GET_CONTRACT_USAGE,
//					request, new Cancelable());
//			Element content = rsp.getContent();
//
//			_applicationComment = XmlUtil.getElementText(content,
//					"ApplicationComment");
//			_contractUsage = XmlUtil.getElementText(content, "ContractUsage");
//		} catch (DSAServerException ex) {
//			_applicationComment = _urlProvider.getOriginal();
//			_contractUsage = _targetContract;
//		}
//	}

	public static void main(String[] args) throws InterruptedException {
		ContractConnection greening = new ContractConnection(
				"https://auth.ischool.com.tw:8443/dsa/greening", "user");
		greening.connect("lucifer.lie@ischool.com.tw", "lielie", true);
		DSAPassport p = greening.getPassport();
		System.out.println(XmlHelper.convertToString(p
				.getPassportTokenElement()));
		// ContractConnection cc2 =
		ContractConnection what = greening.connectAnotherByPassport("dev.sh_d",
				"ischool.snipe.what", true);
		ContractConnection who = what.connectAnotherByPassport("dev.sh_d",
				"ischool.snipe.who", true);

		System.out.println(who._sessionid);
	}
}
