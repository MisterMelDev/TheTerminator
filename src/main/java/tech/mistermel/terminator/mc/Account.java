package tech.mistermel.terminator.mc;

public class Account {

	private String username;
	private String clientToken, accessToken;
	
	public Account(String username, String clientToken, String accessToken) {
		this.username = username;
		this.clientToken = clientToken;
		this.accessToken = accessToken;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getClientToken() {
		return clientToken;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
}
