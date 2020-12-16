package tech.mistermel.terminator.file;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.mistermel.terminator.mc.Account;

public class AccountsFile extends FileProvider {
	
	private static final Logger logger = LoggerFactory.getLogger(AccountsFile.class);
	
	public AccountsFile() {
		super("accounts.json");
	}
	
	public List<Account> loadAccounts() {
		super.load();
		List<Account> result = new ArrayList<>();
		
		JSONArray accountsArray = this.getJson().optJSONArray("accounts");
		if(accountsArray == null) return result;
		
		for(int i = 0; i < accountsArray.length(); i++) {
			JSONObject json = accountsArray.getJSONObject(i);
			result.add(new Account(json.getString("username"), UUID.fromString(json.getString("uuid")), json.getString("loginUsername"), json.getString("clientToken"), json.getString("accessToken")));
		}
		
		logger.info("Loaded {} account(s) from accounts.json", result.size());
		return result;
	}
	
	public void saveAccount(Account account) {
		JSONArray accountsArray = this.getJson().optJSONArray("accounts");
		if(accountsArray == null) {
			accountsArray = new JSONArray();
			this.getJson().put("accounts", accountsArray);
		}
		
		JSONObject json = new JSONObject();
		json.put("username", account.getUsername());
		json.put("uuid", account.getUuid().toString());
		json.put("loginUsername", account.getLoginUsername());
		json.put("clientToken", account.getClientToken());
		json.put("accessToken", account.getAccessToken());
		
		accountsArray.put(json);
		this.getJson().put("accounts", accountsArray);
		
		super.save();
	}

}
