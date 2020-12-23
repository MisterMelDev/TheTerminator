package tech.mistermel.terminator.util;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;

public class Location {

	private double x, y, z;
	private float yaw, pitch;
	
	public Location(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Location(double x, double y, double z, float yaw, float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public Location add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	public Location subtract(double x, double y, double z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}
	
	public Location clone() {
		return new Location(x, y, z, yaw, pitch);
	}
	
	public int getBlockX() {
		return (int) Math.floor(x);
	}
	
	public int getBlockY() {
		return (int) Math.floor(y);
	}
	
	public int getBlockZ() {
		return (int) Math.floor(z);
	}
	
	public int[] toChunkCoords() {
		int chunkX = this.getBlockX() >> 4;
		int chunkY = this.getBlockY() >> 4;
		int chunkZ = this.getBlockZ() >> 4;
		
		return new int[] { chunkX, chunkY, chunkZ };
	}
	
	public int[] toChunkBlockCoords() {
		int blockX = (int) Math.floor(x % 16);
		int blockY = (int) Math.floor(y % 16);
		int blockZ = (int) Math.floor(z % 16);
		if(blockX < 0) blockX += 16;
		if(blockZ < 0) blockZ += 16;
		
		return new int[] { blockX, blockY, blockZ };
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public double getZ() {
		return z;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public static Location fromPosition(Position position) {
		return new Location(position.getX(), position.getY(), position.getZ());
	}
	
}
