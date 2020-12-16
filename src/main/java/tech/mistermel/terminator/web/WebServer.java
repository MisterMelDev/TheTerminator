package tech.mistermel.terminator.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;
import tech.mistermel.terminator.web.route.Route;

public class WebServer extends NanoWSD {

	private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

	private static final int PORT = 8080;
	private static final String INDEX_FILE = "/index.html";
	private static final byte[] PING_PAYLOAD = "1889BEJANDJKM859".getBytes();
	
	private Map<String, Route> routes = new HashMap<>();
	private Set<SocketClient> socketClients = new HashSet<>();
	
	public WebServer() {
		super(PORT);
		
		try {
			this.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
			new PingThread().start();
			
			logger.info("Web server started on port {}", PORT);
		} catch (IOException e) {
			logger.error("Error occurred while attempting to start web server", e);
		}
	}
	
	public void registerRoute(String uri, Route route) {
		routes.put(uri, route);
	}
	
	@Override
	protected Response serveHttp(IHTTPSession session) {
		if(session.getUri().startsWith("."))
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "URI cannot start with .");
		
		String uri = session.getUri().equals("/") ? INDEX_FILE : session.getUri();
		logger.debug("Received {} request for {}", session.getMethod().name(), uri);
		
		Route route = routes.get(uri);
		if(route != null) {
			return route.serve(session);
		}
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("static" + uri);
		if(in == null)
			return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 Not Found");
				
		try {
			String mimeType = NanoHTTPD.getMimeTypeForFile(uri);
			return newFixedLengthResponse(Response.Status.OK, mimeType, in, in.available());
		} catch (IOException e) {
			logger.error("Error occurred while attempting to serve response", e);
			return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", e.getMessage());
		}
	}
	
	private final class PingThread extends Thread {
		
		@Override
		public void run() {
			while(true) {
				for(SocketClient socketClient : socketClients) {
					try {
						socketClient.ping(PING_PAYLOAD);
					} catch (IOException e) {
						logger.error("Error occurred while attempting to send ping", e);
					}
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.warn("Thread sleep in PingThread was interrupted");
					break;
				}
			}
		}
		
	}
	
	@Override
	protected WebSocket openWebSocket(IHTTPSession handshake) {
		SocketClient socketClient = new SocketClient(handshake);
		socketClients.add(socketClient);
		return socketClient;
	}
	
	public void removeWebSocket(SocketClient socketClient) {
		socketClients.remove(socketClient);
	}
}
