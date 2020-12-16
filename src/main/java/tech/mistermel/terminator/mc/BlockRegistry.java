package tech.mistermel.terminator.mc;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;

import tech.mistermel.terminator.Launcher;
import tech.mistermel.terminator.Location;

public class BlockRegistry {

	private static final Logger logger = LoggerFactory.getLogger(BlockRegistry.class);
	
	private Set<Column> columns = new HashSet<>();
	
	public void registerColumn(Column column) {
		Column existingColumn = this.getColumn(column.getX(), column.getZ());
		if(existingColumn != null) {
			columns.remove(existingColumn);
		}
		
		columns.add(column);
	}
	
	public void processBlockChange(BlockChangeRecord record) {
		Location loc = Location.fromPosition(record.getPosition());
		Chunk chunk = this.getChunk(loc);
		if(chunk == null)
			return;
		
		int[] blockCoords = this.toChunkBlockCoords(loc);
		chunk.set(blockCoords[0], blockCoords[1], blockCoords[2], record.getBlock());
	}
	
	public String getBlock(Location loc) {
		Chunk chunk = this.getChunk(loc);
		if(chunk == null)
			return null;
		
		int[] blockCoords = this.toChunkBlockCoords(loc);
		return Launcher.instance.getBlockStateRegistry().getBlock(chunk.get(blockCoords[0], blockCoords[1], blockCoords[2]));
	}
	
	public Chunk getChunk(Location loc) {
		int[] chunkCoords = this.toChunkCoords(loc);
		Column column = this.getColumn(chunkCoords[0], chunkCoords[2]);
		if(column == null) {
			logger.warn("Column x={} z={} has not loaded yet", chunkCoords[0], chunkCoords[2]);
			return null;
		}
		
		return column.getChunks()[chunkCoords[1]];
	}
	
	private int[] toChunkCoords(Location loc) {
		int chunkX = loc.getBlockX() >> 4;
		int chunkY = loc.getBlockY() >> 4;
		int chunkZ = loc.getBlockZ() >> 4;
		
		return new int[] { chunkX, chunkY, chunkZ };
	}
	
	private int[] toChunkBlockCoords(Location loc) {
		int blockX = (int) Math.floor(loc.getX() % 16);
		int blockY = (int) Math.floor(loc.getY() % 16);
		int blockZ = (int) Math.floor(loc.getZ() % 16);
		if(blockX < 0) blockX += 16;
		if(blockZ < 0) blockZ += 16;
		
		return new int[] { blockX, blockY, blockZ };
	}
	
	// This is absolutely horrible, need to fix the O(n) performance
	public Column getColumn(int columnX, int columnZ) {
		for(Column column : columns) {
			if(column.getX() == columnX && column.getZ() == columnZ)
				return column;
		}
		
		return null;
	}
	
}
