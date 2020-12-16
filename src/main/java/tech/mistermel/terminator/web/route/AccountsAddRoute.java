package tech.mistermel.terminator.web.route;

import java.util.UUID;

import com.github.steveice10.mc.auth.exception.request.InvalidCredentialsException;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.protocol.MinecraftProtocol;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import tech.mistermel.terminator.Launcher;
import tech.mistermel.terminator.mc.Account;

public class AccountsAddRoute implements Route {

	@Override
	public Response serve(IHTTPSession session) {
		String loginUsername = session.getParms().get("username");
		String password = session.getParms().get("password");
		
		if(loginUsername == null || password == null) {
			return NanoHTTPD.newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Missing username or password");
		}
		
		try {
			MinecraftProtocol protocol = new MinecraftProtocol(loginUsername, password);
			String clientToken = protocol.getClientToken();
			String accessToken = protocol.getAccessToken();
			
			String username = protocol.getProfile().getName();
			UUID uuid = protocol.getProfile().getId();
			
			if(Launcher.instance.getAccount(uuid) != null) {
				return NanoHTTPD.newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "An account with this UUID already exists");
			}
			
			Account account = new Account(username, uuid, loginUsername, clientToken, accessToken);
			Launcher.instance.addAccount(account);
			
			return NanoHTTPD.newFixedLengthResponse("OK");
		} catch (InvalidCredentialsException e) { 
			return NanoHTTPD.newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Invalid credentials");
		} catch (RequestException e) {
			return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Internal server error");
		}
	}

}
