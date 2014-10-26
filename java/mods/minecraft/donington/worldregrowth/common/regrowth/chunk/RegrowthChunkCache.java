package mods.minecraft.donington.worldregrowth.common.regrowth.chunk;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import mods.minecraft.donington.worldregrowth.ModWorldRegrowth;

public class RegrowthChunkCache {
	private final int dimension;
	private Map<ChunkXZ,RegrowthChunk> cache;
	private int timer;


	public RegrowthChunkCache(int dim) {
		this.dimension = dim;
		this.cache = new ConcurrentHashMap();
		resetTimer();
	}


	public void loadChunk(ChunkXZ chunkXZ) {
		RegrowthChunk chunk = this.cache.get(chunkXZ);
		if ( chunk != null ) {
			ModWorldRegrowth.warning("loadChunk(%s): chunk not null", chunkXZ.toString());
			throw new IllegalStateException();
		}

		chunk = new RegrowthChunk(dimension, chunkXZ);
		this.cache.put(chunkXZ, chunk);
		chunk.loadChunk();
		//ModWorldRegrowth.info("chunk %s loaded", chunkXZ.toString());
	}


	public void saveChunk(ChunkXZ chunkXZ) {
		RegrowthChunk chunk = this.cache.get(chunkXZ);
		if ( chunk == null ) {
			ModWorldRegrowth.warning("saveChunk(%s): chunk is null", chunkXZ.toString());
			throw new IllegalStateException();
		}

		chunk.saveChunk();
		this.cache.remove(chunkXZ);
		//ModWorldRegrowth.info("chunk %s saved", chunkXZ.toString());
	}


	public Set<ChunkXZ> getLoadedChunks() {
		return cache.keySet();
	}


	public RegrowthChunk getChunk(ChunkXZ chunkXZ) {
		return cache.get(chunkXZ);
	}


	public int getTimer() {
		return timer;
	}


	public void resetTimer() {
		timer = 1;
	}


	public void incrementTimer() {
		timer++;
	}

}
