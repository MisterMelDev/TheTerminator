package tech.mistermel.terminator.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockTypeRegistry {

	private static final Logger logger = LoggerFactory.getLogger(BlockTypeRegistry.class);
	
	private Map<String, String> translations = new HashMap<>();
	private Map<Integer, BlockType> blockTypes = new HashMap<>();
	
	private BlockType airType;
	
	public void load() {
		JSONObject langJson = new JSONObject(this.readFile("lang.json"));
		for(String id : langJson.keySet()) {
			translations.put(id, langJson.getString(id));
		}
		logger.info("Loaded lang.json (element num: {})", translations.size());
		
		JSONObject blocksJson = new JSONObject(this.readFile("blocks.json"));
		for(String block : blocksJson.keySet()) {
			JSONArray statesJson = blocksJson.getJSONObject(block).getJSONArray("states");
			for(int i = 0; i < statesJson.length(); i++) {
				JSONObject stateJson = statesJson.getJSONObject(i);
				int id = stateJson.getInt("id");
				
				BlockType type = new BlockType(id, block, this.getTranslation("block." + block.replace(":", ".")), false);
				blockTypes.put(id, type);
			}
		}
		logger.info("Loaded blocks.json (element num: {})", blockTypes.size());
		
		this.airType = this.getBlockType("minecraft:air");
	}
	
	public BlockType getBlockType(int id) {
		return blockTypes.get(id);
	}
	
	public BlockType getBlockType(String name) {
		for(BlockType type : blockTypes.values()) {
			if(type.getName().equals(name))
				return type;
		}
		
		return null;
	}
	
	public BlockType getAirType() {
		return airType;
	}
	
	private String readFile(String name) {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("mc-data/" + name);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		return reader.lines().collect(Collectors.joining());
	}
	
	public String getTranslation(String id) {
		return translations.get(id);
	}
	
}
