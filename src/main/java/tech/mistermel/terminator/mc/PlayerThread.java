package tech.mistermel.terminator.mc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.steveice10.mc.protocol.data.SubProtocol;

import tech.mistermel.terminator.util.BlockType;

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
		if(botPlayer.getProtocol().getSubProtocol() != SubProtocol.GAME)
			return;
		
		/*Location standingLoc = botPlayer.getLocation().subtract(0, 1, 0);
		BlockType standingBlock = botPlayer.getBlock(standingLoc);
		
		//System.out.println(standingLoc.getBlockY() + ": " + standingBlock.getFriendlyName() + " (solid? " + standingBlock.isSolid() + ")");
		
		boolean touchingBlock = botPlayer.getLocation().getY() - Math.floor(botPlayer.getLocation().getY()) == 0;
		botPlayer.setOnGround(touchingBlock);
		//System.out.println("Touching block? " + touchingBlock);
		
		if(!touchingBlock) {
			botPlayer.setVelocityY((botPlayer.getVelocityY() - 0.08f) * 0.98f);
			//System.out.println("Y velocity is set to " + botPlayer.getVelocityY());
		} else botPlayer.setVelocityY(0);
		
		System.out.println(botPlayer.getLocation().getX() + " " + botPlayer.getLocation().getY() + " " + botPlayer.getLocation().getZ());*/
		
		BlockType standingBlock = botPlayer.getBlock(botPlayer.getLocation().subtract(0, 1, 0));
		boolean touchingBlock = botPlayer.getLocation().getY() - Math.floor(botPlayer.getLocation().getY()) <= 0.1;
		
		
		//TODO: still broken af
		if(!touchingBlock || !standingBlock.isSolid()) {
			botPlayer.setVelocityY((botPlayer.getVelocityY() - 0.08f) * 0.98f);
		} else botPlayer.setVelocityY(0);
		
		botPlayer.applyVelocity();
	}
	
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	
}
