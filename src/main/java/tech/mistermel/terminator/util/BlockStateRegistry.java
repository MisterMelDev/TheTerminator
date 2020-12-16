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

public class BlockStateRegistry {

	private static final Logger logger = LoggerFactory.getLogger(BlockStateRegistry.class);
	
	private Map<Integer, String> idToBlock = new HashMap<>();
	private Map<String, Integer> blockToId = new HashMap<>();
	
	public void load() {
		long startTime = System.currentTimeMillis();
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("blocks.json");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String str = reader.lines().collect(Collectors.joining());
		
		long readCompleteTime = System.currentTimeMillis();
		
		JSONObject json = new JSONObject(str);
		for(String block : json.keySet()) {
			JSONArray statesJson = json.getJSONObject(block).getJSONArray("states");
			for(int i = 0; i < statesJson.length(); i++) {
				JSONObject stateJson = statesJson.getJSONObject(i);
				int id = stateJson.getInt("id");
				
				idToBlock.put(id, block);
				blockToId.put(block, id);
			}
		}
		
		logger.info("Read and parsed blocks.json (elements: {}) in {}ms (read: {}ms, parse: {}ms)", idToBlock.size(), System.currentTimeMillis() - startTime, readCompleteTime - startTime, System.currentTimeMillis() - readCompleteTime);
	}
	
	public String getBlock(int id) {
		return idToBlock.get(id);
	}
	
	public int getId(String block) {
		return blockToId.get(block);
	}
	
}
