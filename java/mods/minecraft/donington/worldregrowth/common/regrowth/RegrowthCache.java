package mods.minecraft.donington.worldregrowth.common.regrowth;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mods.minecraft.donington.worldregrowth.ModWorldRegrowth;
import mods.minecraft.donington.worldregrowth.common.regrowth.chunk.ChunkXZ;
import mods.minecraft.donington.worldregrowth.common.regrowth.chunk.RegrowthChunkCache;

public class RegrowthCache {
	private Map<Integer,RegrowthChunkCache> cache;


	public RegrowthCache() {
		this.cache = new HashMap();
	}


	public void loadDimension(int dim) {
		this.cache.put(dim, new RegrowthChunkCache(dim));
	}


	public RegrowthChunkCache getDimension(int dim) {
		return this.cache.get(dim);
	}


	public Collection<RegrowthChunkCache> getDimensions() {
		return this.cache.values();
	}


	public void unloadAll() {
		for ( RegrowthChunkCache chunkCache : this.getDimensions() )
			for ( ChunkXZ chunkXZ : chunkCache.getLoadedChunks() ) {
				//ModWorldRegrowth.info("unloading chunk %s", chunkXZ.toString());
				chunkCache.saveChunk(chunkXZ);
			}
	}

}
