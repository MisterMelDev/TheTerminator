package tech.mistermel.terminator.mc;

import java.util.UUID;

public class Account {

	private String username;
	private UUID uuid;
	
	private String loginUsername;
	private String clientToken, accessToken;
	
	public Account(String username, UUID uuid, String loginUsername, String clientToken, String accessToken) {
		this.username = username;
		this.uuid = uuid;
		this.loginUsername = loginUsername;
		this.clientToken = clientToken;
		this.accessToken = accessToken;
	}
	
	public String getUsername() {
		return username;
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public String getLoginUsername() {
		return loginUsername;
	}
	
	public String getClientToken() {
		return clientToken;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
}
