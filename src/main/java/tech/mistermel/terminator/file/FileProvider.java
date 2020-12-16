package tech.mistermel.terminator.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileProvider {

	private static final Logger logger = LoggerFactory.getLogger(FileProvider.class);
	
	private File file;
	private JSONObject json;
	
	public FileProvider(String name) {
		this.file = new File(name);
	}
	
	public void load() {	
		try {
			if(!file.exists()) {
				file.createNewFile();
				Files.writeString(file.toPath(), "{}");
				
				this.json = new JSONObject();
				return;
			}
			
			String str = new String(Files.readAllBytes(file.toPath()));
			this.json = new JSONObject(str);
		} catch (IOException e) {
			logger.error("Error while attempting to load file", e);
		}
	}
	
	public void save() {
		try {
			Files.writeString(file.toPath(), json.toString());
		} catch (IOException e) {
			logger.error("Error while attempting to save file", e);
		}
	}
	
	public JSONObject getJson() {
		return json;
	}
	
}
