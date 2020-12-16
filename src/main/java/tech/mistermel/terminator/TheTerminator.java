package tech.mistermel.terminator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.mistermel.terminator.web.WebServer;

public class TheTerminator {

	private static final Logger logger = LoggerFactory.getLogger(TheTerminator.class);
	
	private WebServer webServer;
	
	public void start() {
		this.webServer = new WebServer();
		
		logger.info("Startup completed (took {}ms)", (System.currentTimeMillis() - Launcher.startupTime));
	}
	
}
