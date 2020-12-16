package tech.mistermel.terminator.mc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerThread extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(PlayerThread.class);
	private static final int TARGET_TPS = 20;
	
	private BotPlayer botPlayer;
	private boolean isRunning = false;
	
	public PlayerThread(BotPlayer botPlayer) {
		this.botPlayer = botPlayer;
	}
	
	@Override
	public void run() {
		this.isRunning = true;
		
		int millisDelay = 1000 / TARGET_TPS;
		while(isRunning) {
			long startTime = System.currentTimeMillis();
			this.tick();
			
			try {
				long timeLeft = (startTime + millisDelay) - System.currentTimeMillis();
				if(timeLeft < 0) {
					logger.warn("Player thread for {} cannot keep up! Running {}ms behind", botPlayer.getUsername(), Math.abs(timeLeft));
					timeLeft = 0;
				}
				
				Thread.sleep(timeLeft);
			} catch (InterruptedException e) {}
		}
	}
	
	private void tick() {
		System.out.println("tick");
	}
	
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	
}
