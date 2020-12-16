package tech.mistermel.terminator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheTerminator {

	private static final Logger logger = LoggerFactory.getLogger(TheTerminator.class);
	
	public void start() {
		logger.info("Startup completed (took {}ms)", (System.currentTimeMillis() - Launcher.startupTime));
	}
	
}
