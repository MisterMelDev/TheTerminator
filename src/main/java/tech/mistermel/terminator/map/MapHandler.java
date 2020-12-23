package tech.mistermel.terminator.map;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.steveice10.mc.protocol.data.game.chunk.Column;

import tech.mistermel.terminator.Launcher;
import tech.mistermel.terminator.mc.BlockRegistry;
import tech.mistermel.terminator.mc.BlockRegistry.Block;
import tech.mistermel.terminator.mc.BotPlayer;
import tech.mistermel.terminator.util.BiomeRegistry.Biome;
import tech.mistermel.terminator.util.BlockType;
import tech.mistermel.terminator.util.Location;

public class MapHandler {

	private static Logger logger = LoggerFactory.getLogger(MapHandler.class);
	private static int SIZE = 768, WATER_COLOR = 4159204;
	
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
		
		Biome biome = Launcher.instance.getBiomeRegistry().getBiome(column.getBiomeData()[0]);
		
		/*BufferedImage colorMapImg = Launcher.instance.getTextureRegistry().getTexture("textures/colormap/grass.png");
		float adjTemp = clamp(biome.getTemperature(), 0.0f, 1.0f);
		float adjRainfall = clamp(biome.getRainfall(), 0.0f, 1.0f) * adjTemp;
		
		Color grassColor = new Color(colorMapImg.getRGB((int) (adjTemp * colorMapImg.getWidth()), (int) (adjRainfall * colorMapImg.getHeight())));*/
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		for(int chunkX = 0; chunkX < 16; chunkX++) {
			for(int chunkZ = 0; chunkZ < 16; chunkZ++) {
				int blockX = x + chunkX * 16;
				int blockY = y + chunkZ * 16;
				
				Block block = BlockRegistry.getHighestBlock(column, chunkX, chunkZ);
				
				BufferedImage textureImg = this.getTexture(block.getType());
				if(textureImg == null) {
					logger.warn("Could not find texture for block type {}", block.getType().getName());
					continue;
				}
				
				if(textureImg.getHeight() > 16)
					textureImg = textureImg.getSubimage(0, 0, 16, 16);
				
				if(block.getType().getName().equals("minecraft:grass_block")) {
					Color grassColor = this.getColorMapColor("grass", biome, block.getLocation().getBlockY());
					textureImg = this.applyColor(textureImg, grassColor);
				}
				
				if(block.getType().getName().equals("minecraft:oak_leaves")) {
					Color foliageColor = this.getColorMapColor("foliage", biome, block.getLocation().getBlockY());
					textureImg = this.applyColor(textureImg, foliageColor);
				}
				
				if(block.getType().getName().equals("minecraft:water")) {
					textureImg = this.applyColor(textureImg, new Color(WATER_COLOR));
				}
				
				g2d.drawImage(textureImg, blockX, blockY, null);
			}
		}
	}
	
	private Color getColorMapColor(String colorMap, Biome biome, int height) {
		int seaLevel = 62;
		int elevation = Math.max(0, height - seaLevel);
		
		BufferedImage img = Launcher.instance.getTextureRegistry().getTexture("textures/colormap/" + colorMap + ".png");
		float adjTemp = clamp(biome.getTemperature() - (float) elevation * 0.166667f, 0.0f, 1.0f);
		float adjRainfall = clamp(biome.getRainfall(), 0.0f, 1.0f) * adjTemp;
		
		int x = (int) (img.getWidth() * adjRainfall);
		int y = (int) (img.getHeight() * adjTemp);
		
		return new Color(img.getRGB(x, y));
	}
	
	private BufferedImage applyColor(BufferedImage imgIn, Color color) {
		BufferedImage img = new BufferedImage(imgIn.getWidth(), imgIn.getHeight(), imgIn.getType());
		
		for(int x = 0; x < img.getWidth(); x++) {
			for(int y = 0; y < img.getHeight(); y++) {
				Color pixelColor = new Color(imgIn.getRGB(x, y));
				img.setRGB(x, y, mixColors(pixelColor, color).getRGB());
			}
		}
		
		return img;
	}
	
	private Color mixColors(Color color1, Color color2) {
		float ratio = 0.5f;
		int r = (int) ((color1.getRed() * ratio) + (color2.getRed() * ratio));
		int g = (int) ((color1.getGreen() * ratio) + (color2.getGreen() * ratio));
		int b = (int) ((color1.getBlue() * ratio) + (color2.getBlue() * ratio));
		
		return new Color(r, g, b);
	}
	
	private float clamp(float value, float min, float max) {
		if(value > max)
			return max;
		if(value < min)
			return min;
		return value;
	}
	
	private BufferedImage getTexture(BlockType type) {
		String name = type.getNameWithoutNamespace();
		
		if(name.equals("water"))
			return this.getTexture("water_still");
		
		BufferedImage img = this.getTexture(name);
		if(img != null)
			return img;
		
		return this.getTexture(name + "_top");
	}
	
	private BufferedImage getTexture(String name) {
		return Launcher.instance.getTextureRegistry().getTexture("textures/block/" + name + ".png");
	}
	
}
