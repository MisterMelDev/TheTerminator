package tech.mistermel.terminator.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import tech.mistermel.terminator.mc.Account;

public class AccountsFile extends FileProvider {
	
	public AccountsFile() {
		super("accounts.json");
	}
	
	public List<Account> loadAccounts() {
		JSONArray accountsArray = this.getJson().optJSONArray("accounts");
		if(accountsArray == null) return Collections.emptyList();
		
		List<Account> result = new ArrayList<>();
		for(int i = 0; i < accountsArray.length(); i++) {
			JSONObject json = accountsArray.getJSONObject(i);
			result.add(new Account(json.getString("username"), json.getString("clientToken"), json.getString("accessToken")));
		}
		
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
		json.put("clientToken", account.getClientToken());
		json.put("accessToken", account.getAccessToken());
		
		accountsArray.put(json);
		this.save();
	}

}
