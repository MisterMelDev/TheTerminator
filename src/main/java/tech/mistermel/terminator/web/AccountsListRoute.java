package tech.mistermel.terminator.web;

import java.util.List;

import org.json.JSONArray;

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
			accountsArray.put(account.getUsername());
		}
		
		return NanoHTTPD.newFixedLengthResponse(accountsArray.toString());
	}

}
