package tech.mistermel.terminator.web.route;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import tech.mistermel.terminator.Launcher;
import tech.mistermel.terminator.mc.Account;

public class AccountsListRoute implements Route {

	@Override
	public Response serve(IHTTPSession session) {
		List<Account> accounts = Launcher.instance.getAccounts();
		
		JSONArray accountsArray = new JSONArray();
		for(Account account : accounts) {
			JSONObject json = new JSONObject();
			accountsArray.put(json);
			
			json.put("username", account.getUsername());
			json.put("uuid", account.getUuid().toString());
		}
		
		return NanoHTTPD.newFixedLengthResponse(accountsArray.toString());
	}

}
