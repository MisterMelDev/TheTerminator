package tech.mistermel.terminator.web;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

public class WebServer extends NanoWSD {

	private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

	private static final int PORT = 8080;
	private static final String INDEX_FILE = "/index.html";
	
	public WebServer() {
		super(PORT);
		
		try {
			this.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
			logger.info("Web server started on port {}", PORT);
		} catch (IOException e) {
			logger.error("Error occurred while attempting to start web server", e);
		}
	}
	
	@Override
	protected Response serveHttp(IHTTPSession session) {
		if(session.getUri().startsWith("."))
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "URI cannot start with .");
		
		String uri = session.getUri().equals("/") ? INDEX_FILE : session.getUri();
		logger.debug("Received {} request for {}", session.getMethod().name(), uri);
		
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
	
	@Override
	protected WebSocket openWebSocket(IHTTPSession handshake) {
		return null;
	}
}
