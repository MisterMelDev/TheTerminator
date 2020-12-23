package tech.mistermel.terminator.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.mistermel.terminator.Launcher;


public class BiomeRegistry {
	
	private static Logger logger = LoggerFactory.getLogger(BiomeRegistry.class);
	private Map<Integer, Biome> biomes = new HashMap<>();
	
	public void load() {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("mc-data/biomes.json");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String txt = reader.lines().collect(Collectors.joining());
		
		JSONObject json = new JSONObject(txt);
		for(String key : json.keySet()) {
			JSONObject biomeObj = json.getJSONObject(key);
			
			Biome biome = new Biome(key, Launcher.instance.getBlockStateRegistry().getTranslation("biome." + key.replace(":", ".")), biomeObj.getFloat("temperature"), biomeObj.getFloat("downfall"));
			biomes.put(biomeObj.getInt("id"), biome);
		}
		logger.info("Loaded biomes.json (element num: {})", biomes.size());
	}
	
	public Biome getBiome(int id) {
		return biomes.get(id);
	}
	
	public static class Biome {
		
		private String name, friendlyName;
		private float temperature, rainfall;
		
		public Biome(String name, String friendlyName, float temperature, float rainfall) {
			this.name = name;
			this.friendlyName = friendlyName;
			this.temperature = temperature;
			this.rainfall = rainfall;
		}
		
		public String getName() {
			return name;
		}
		
		public String getFriendlyName() {
			return friendlyName;
		}
		
		public float getTemperature() {
			return temperature;
		}
		
		public float getRainfall() {
			return rainfall;
		}
		
	}
	
}
