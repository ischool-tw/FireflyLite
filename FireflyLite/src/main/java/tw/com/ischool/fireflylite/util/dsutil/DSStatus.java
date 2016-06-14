package tw.com.ischool.fireflylite.util.dsutil;

public class DSStatus {

	public static DSStatus Successful() {
		return new DSStatus("0", "Successful");
	}

	public static DSStatus UnhandledException() {
		return new DSStatus("500", "Unhandled Exception");
	}

	public static DSStatus UnhandledException(String msg) {
		return new DSStatus("500", msg);
	}

	public static DSStatus ServiceNotFound() {
		return new DSStatus("501", "Service Not Found");
	}

	public static DSStatus ServiceNotFound(String msg) {
		return new DSStatus("501", msg);
	}

	public static DSStatus CredentialInvalid() {
		return new DSStatus("502", "Credential Invalid");
	}

	public static DSStatus CredentialInvalid(String msg) {
		return new DSStatus("502", msg);
	}

	public static DSStatus AccessDeny() {
		return new DSStatus("503", "Access Deny");
	}

	public static DSStatus AccessDeny(String msg) {
		return new DSStatus("503", msg);
	}

	public static DSStatus ServiceExecutionError() {
		return new DSStatus("504", "Service Execution Error");
	}

	public static DSStatus ServiceExecutionError(String msg) {
		return new DSStatus("504", msg);
	}

	public static DSStatus ServiceBusy() {
		return new DSStatus("505", "Service Busy");
	}

	public static DSStatus ServiceBusy(String msg) {
		return new DSStatus("505", msg);
	}

	public static DSStatus InvalidRequestDocument() {
		return new DSStatus("506", "Invalid Request Document");
	}

	public static DSStatus InvalidRequestDocument(String msg) {
		return new DSStatus("506", msg);
	}

	public static DSStatus InvalidResponseDocument() {
		return new DSStatus("507", "Invalid Response Document");
	}

	public static DSStatus ServiceActivationError() {
		return new DSStatus("508", "Service Activation Error");
	}

	public static DSStatus ServiceActivationError(String msg) {
		return new DSStatus("508", msg);
	}

	public static DSStatus ServerUnavailable() {
		return new DSStatus("509", "Server Unavailable ");
	}

	public static DSStatus ServerUnavailable(String msg) {
		return new DSStatus("509", msg);
	}

	public static DSStatus ApplicationUnavailable() {
		return new DSStatus("510", "Application Unavailable ");
	}

	public static DSStatus ApplicationUnavailable(String msg) {
		return new DSStatus("510", msg);
	}

	public static DSStatus SessionInvalid() {
		return new DSStatus("511", "Session Invalid");
	}

	public static DSStatus SessionInvalid(String msg) {
		return new DSStatus("511", msg);
	}

	public static DSStatus PassportExpire() {
		return new DSStatus("512", "Passport Expire");
	}

	public static DSStatus PassportExpire(String msg) {
		return new DSStatus("512", msg);
	}

	public static DSStatus AccessPointNotFound() {
		return new DSStatus("513", "Access Point Not Found");
	}

	public static DSStatus AccessPointNotFound(String msg) {
		return new DSStatus("513", msg);
	}

	public static DSStatus InvalidConfigFile() {
		return new DSStatus("514", "Invalid Config File");
	}

	public static DSStatus InvalidConfigFile(String msg) {
		return new DSStatus("514", msg);
	}

	public static DSStatus ConnectionError() {
		return new DSStatus("515", "Build Connection Fail");
	}

	public static DSStatus ConnectionError(String msg) {
		return new DSStatus("515", msg);
	}

	public static DSStatus CheckUpdateFail() {
		return new DSStatus("516", "Check Update Fail");
	}

	public static DSStatus CheckUpdateFail(String msg) {
		return new DSStatus("516", msg);
	}

	public static DSStatus ApplicationDisabled() {
		return new DSStatus("517", "Application Disabled");
	}

	public static DSStatus ApplicationDisabled(String msg) {
		return new DSStatus("517", msg);
	}

	public static DSStatus ContractDisabled() {
		return new DSStatus("518", "Contract Disabled");
	}

	public static DSStatus ContractDisabled(String msg) {
		return new DSStatus("518", msg);
	}

	public static DSStatus UDSServiceExecutionError() {
		return new DSStatus("519", "UDS Service Execution Error");
	}

	public static DSStatus UDSServiceExecutionError(String msg) {
		return new DSStatus("519", msg);
	}

	public static DSStatus ResponseTypeException() {
		return new DSStatus("520", "Response Type Handler Error.");
	}

	public static DSStatus ResponseTypeException(String msg) {
		return new DSStatus("520", msg);
	}

	public static DSStatus RequestParserException() {
		return new DSStatus("521", "Request Parser Error.");
	}

	public static DSStatus RequestParserException(String msg) {
		return new DSStatus("521", msg);
	}

	public static DSStatus ApplicationNotFound() {
		return new DSStatus("522", "Application Not Found");
	}

	public static DSStatus ApplicationNotFound(String msg) {
		return new DSStatus("522", msg);
	}

	public static DSStatus ContractNotFound() {
		return new DSStatus("523", "Contract Not Found");
	}

	public static DSStatus ContractNotFound(String msg) {
		return new DSStatus("523", msg);
	}

	public static DSStatus ApplicationExpired() {
		return new DSStatus("524", "Application Expired");
	}

	public static DSStatus DecryptionError() {
		return new DSStatus("525", "Decryption Error.");
	}

	public static DSStatus DecryptionError(String msg) {
		return new DSStatus("525", msg);
	}

	public static DSStatus EncryptionError() {
		return new DSStatus("525", "Encryption Error.");
	}

	public static DSStatus EncryptionError(String msg) {
		return new DSStatus("525", msg);
	}

	public static DSStatus MemoryAccessError() {
		return new DSStatus("526", "DSA Memory Access Error");
	}

	public static DSStatus MemoryAccessError(String msg) {
		return new DSStatus("526", msg);
	}

	public static DSStatus AccessTokenInvalid() {
		return new DSStatus("527", "Access token invalid.");
	}

	public static DSStatus AccessTokenInvalid(String msg) {
		return new DSStatus("527", msg);
	}
	
	public static DSStatus DelayRequestError(){
		return new DSStatus("528","Delay request error.");
	}
	
	public static DSStatus DelayRequestError(String msg) {
		return new DSStatus("528", msg);
	}
	
	private String code;

	private String message;

	public DSStatus(String code, String message) {

		this.code = code;
		this.message = message;
	}

	public String code() {

		return code;
	}

	public String message() {

		return message;
	}

	public String toString() {
		return code() + ":" + message();
	}

	public boolean equals(DSStatus status) {
		return code.equals(status.code());
	}

}
