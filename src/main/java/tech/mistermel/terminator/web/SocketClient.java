package tech.mistermel.terminator.web;

import java.io.IOException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoWSD.WebSocket;
import fi.iki.elonen.NanoWSD.WebSocketFrame;
import fi.iki.elonen.NanoWSD.WebSocketFrame.CloseCode;
import tech.mistermel.terminator.Launcher;

public class SocketClient extends WebSocket {

	private static Logger logger = LoggerFactory.getLogger(SocketClient.class);
	
	public SocketClient(IHTTPSession handshakeRequest) {
		super(handshakeRequest);
	}

	@Override
	protected void onOpen() {
		logger.info("Connection opened");
	}

	@Override
	protected void onClose(CloseCode code, String reason, boolean initiatedByRemote) {
		Launcher.instance.getWebServer().removeWebSocket(this);
		logger.info("Connection closed: {}", code);
	}

	@Override
	protected void onMessage(WebSocketFrame message) {
		JSONObject json = new JSONObject(message.getTextPayload());
		String packetType = json.getString("type");
		
		if(packetType.equals("connect")) {
			int accountIndex = json.getInt("accountIndex");
			Launcher.instance.connectAccount(accountIndex);
		}
	}

	@Override
	protected void onPong(WebSocketFrame pong) {
		
	}

	@Override
	protected void onException(IOException exception) {
		logger.error("Error occurred in websocket client", exception);
	}

}
