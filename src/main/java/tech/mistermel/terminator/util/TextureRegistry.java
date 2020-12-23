package tech.mistermel.terminator.util;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextureRegistry {

	private static Logger logger = LoggerFactory.getLogger(TextureRegistry.class);
	private Map<String, BufferedImage> textures = new HashMap<>();
	
	public BufferedImage getTexture(String path) {
		return textures.get(path);
	}
	
	public void loadTextures() {
		File directory = new File("textures");
		if(!directory.isDirectory()) {
			this.extractTextures();
		}
		
		logger.info("Loading textures...");
		
		try {
			List<Path> files = Files.walk(directory.toPath())
				.collect(Collectors.toList());
			
			for(Path filePath : files) {
				File file = filePath.toFile();
				if(file.isDirectory())
					continue;
				
				String universalPath = filePath.toString().replace(File.separatorChar, '/');
				textures.put(universalPath, ImageIO.read(file));
			}
			
			logger.info("Loaded {} textures", textures.size());
		} catch (IOException e) {
			logger.error("Error occurred while trying to load textures", e);
		}
	}
	
	private void extractTextures() {
		logger.info("Extracting texture resources...");
		
		new File("textures").mkdir();
		
		try {
			ZipInputStream in = new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream("textures.zip"));
			
			ZipEntry entry;
			while((entry = in.getNextEntry()) != null) {
				String fileName = "textures" + File.separator + entry.getName();
				if(entry.isDirectory()) {
					new File(fileName).mkdir();
					continue;
				}
				
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
				
				byte[] bytesIn = new byte[4096];
				int read = 0;
				while((read = in.read(bytesIn)) != -1) {
					out.write(bytesIn, 0, read);
				}
				
				out.close();
				in.closeEntry();
			}
		} catch (IOException e) {
			logger.error("Error occurred while trying to extract texture files", e);
		}
	}
	
}
