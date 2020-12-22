package tech.mistermel.terminator.util;

public class BlockType {
	
	private int id;
	private String name, friendlyName;
	
	private boolean isSolid;
	
	public BlockType(int id, String name, String friendlyName, boolean isSolid) {
		this.id = id;
		this.name = name;
		this.friendlyName = friendlyName;
		this.isSolid = isSolid;
		
		if(!name.equals("minecraft:air"))
			this.isSolid = true;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFriendlyName() {
		return friendlyName;
	}
	
	public boolean isSolid() {
		return isSolid;
	}
	
}
