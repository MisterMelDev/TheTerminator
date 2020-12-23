package tech.mistermel.terminator.util;

import java.util.Arrays;
import java.util.List;

public class BlockType {
	
	private List<String> notSolid = Arrays.asList("minecraft:air", "minecraft:cave_air", "minecraft:grass", "minecraft:tall_grass", "minecraft:seagrass", "minecraft:tall_seagrass", "minecraft:kelp",
			"minecraft:oxeye_daisy", "minecraft:dandelion", "minecraft:poppy", "minecraft:sugar_cane", "minecraft:wheat", "minecraft:carrots", "minecraft:potatoes", "minecraft:beetroot");
	
	private int id;
	private String name, friendlyName;
	
	private boolean isSolid;
	
	public BlockType(int id, String name, String friendlyName, boolean isSolid) {
		this.id = id;
		this.name = name;
		this.friendlyName = friendlyName;
		this.isSolid = isSolid;
		
		if(!notSolid.contains(name))
			this.isSolid = true;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getNameWithoutNamespace() {
		return name.substring(name.indexOf(':') + 1);
	}
	
	public String getFriendlyName() {
		return friendlyName;
	}
	
	public boolean isSolid() {
		return isSolid;
	}
	
}
