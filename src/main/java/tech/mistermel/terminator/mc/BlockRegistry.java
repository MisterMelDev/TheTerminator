package tech.mistermel.terminator.mc;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;

import tech.mistermel.terminator.Launcher;
import tech.mistermel.terminator.util.BlockType;
import tech.mistermel.terminator.util.Location;

public class BlockRegistry {

	private static final Logger logger = LoggerFactory.getLogger(BlockRegistry.class);
	
	private Set<Column> columns = new HashSet<>();
	
	public void registerColumn(Column column) {
		Column existingColumn = this.getColumn(column.getX(), column.getZ());
		if(existingColumn != null) {
			logger.info("Overwriting existing column (x: {}, z: {})", column.getX(), column.getZ());
			columns.remove(existingColumn);
		}
		
		columns.add(column);
	}
	
	public void processBlockChange(BlockChangeRecord record) {
		Location loc = Location.fromPosition(record.getPosition());
		Chunk chunk = this.getChunk(loc);
		if(chunk == null)
			return;
		
		int[] blockCoords = loc.toChunkBlockCoords();
		chunk.set(blockCoords[0], blockCoords[1], blockCoords[2], record.getBlock());
	}
	
	public BlockType getBlock(Location loc) {
		Chunk chunk = this.getChunk(loc);
		if(chunk == null) {
			// If the column has loaded, but this chunk is null,
			// the chunk is entirely air, so we just return AIR.
			return Launcher.instance.getBlockStateRegistry().getAirType();
		}
		
		int[] blockCoords = loc.toChunkBlockCoords();
		return Launcher.instance.getBlockStateRegistry().getBlockType(chunk.get(blockCoords[0], blockCoords[1], blockCoords[2]));
	}
	
	public Chunk getChunk(Location loc) {
		int[] chunkCoords = loc.toChunkCoords();
		Column column = this.getColumn(chunkCoords[0], chunkCoords[2]);
		if(column == null) {
			logger.warn("Column x={} z={} has not loaded yet", chunkCoords[0], chunkCoords[2]);
			return null;
		}
		
		return column.getChunks()[chunkCoords[1]];
	}
	
	// This is absolutely horrible, need to fix the O(n) performance
	public Column getColumn(int columnX, int columnZ) {
		for(Column column : columns) {
			if(column.getX() == columnX && column.getZ() == columnZ)
				return column;
		}
		
		return null;
	}
	
	public static BlockType getHighestBlock(Column column, int chunkBlockX, int chunkBlockZ) {
		for(int i = column.getChunks().length - 1; i >= 0; i--) {
			Chunk chunk = column.getChunks()[i];
			if(chunk == null)
				continue;
			
			for(int y = 15; y >= 0; y--) {
				BlockType type = Launcher.instance.getBlockStateRegistry().getBlockType(chunk.get(chunkBlockX, y, chunkBlockZ));
				if(type != null && type.isSolid())
					return type;
			}
		}
		
		return null;
	}
	
}
