package tech.mistermel.terminator.map;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.steveice10.mc.protocol.data.game.chunk.Column;

import tech.mistermel.terminator.mc.BlockRegistry;
import tech.mistermel.terminator.mc.BotPlayer;
import tech.mistermel.terminator.util.BlockType;
import tech.mistermel.terminator.util.Location;

public class MapHandler {

	private static Logger logger = LoggerFactory.getLogger(MapHandler.class);
	private static int SIZE = 768;
	
	public BufferedImage createImage(BotPlayer player, boolean showPlayer) {
		BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = img.createGraphics();
		
		int[] chunkCoords = player.getLocation().toChunkCoords();
		int chunkX = chunkCoords[0], chunkZ = chunkCoords[2];
		
		for(int xOffset = -1; xOffset <= 1; xOffset++) {
			for(int zOffset = -1; zOffset <= 1; zOffset++) {
				Column column = player.getColumn(chunkX + xOffset, chunkZ + zOffset);
				this.renderColumn(g2d, column, xOffset, zOffset);
			}
		}
		
		if(showPlayer) {
			this.renderPlayerPoint(g2d, player.getLocation());
		}
		
		return img;
	}
	
	private void renderPlayerPoint(Graphics2D g2d, Location loc) {
		int[] blockCoords = loc.toChunkBlockCoords();
		
		g2d.setColor(Color.RED);
		g2d.fillOval((SIZE / 2 - 128) + blockCoords[0] * 16, (SIZE / 2 - 128) + blockCoords[2] * 16, 16, 16);
	}
	
	private void renderColumn(Graphics2D g2d, Column column, int xOffset, int zOffset) {
		int x = (SIZE / 2) + xOffset * 256 - 128;
		int y = (SIZE / 2) + zOffset * 256 - 128;
		
		if(column == null) {
			g2d.setColor(Color.RED);
			g2d.fillRect(x, y, 256, 256);
			return;
		}
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		for(int chunkX = 0; chunkX < 16; chunkX++) {
			for(int chunkZ = 0; chunkZ < 16; chunkZ++) {
				int blockX = x + chunkX * 16;
				int blockY = y + chunkZ * 16;
				
				BlockType type = BlockRegistry.getHighestBlock(column, chunkX, chunkZ);
				
				URL url = this.getTexture(type);
				if(url == null) {
					logger.warn("Could not find texture for block type {}", type.getName());
					continue;
				}
				
				try {
					g2d.drawImage(ImageIO.read(url), blockX, blockY, null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private URL getTexture(BlockType type) {
		String name = type.getNameWithoutNamespace();
		
		URL url = this.getTexture(name);
		if(url != null)
			return url;
		
		return this.getTexture(name + "_top");
	}
	
	private URL getTexture(String name) {
		return this.getClass().getClassLoader().getResource("mc-data/textures/block/" + name + ".png");
	}
	
}
