package mods.minecraft.donington.worldregrowth.common.regrowth.chunk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import mods.minecraft.donington.worldregrowth.ModWorldRegrowth;
import mods.minecraft.donington.worldregrowth.common.regrowth.RegrowthBlock;

public class RegrowthChunk {
	private final int dimension;
	private final ChunkXZ chunkXZ;
	private LinkedList<RegrowthBlock> cache;
	private final String savePath;
	private int timer;

	public RegrowthChunk(int dimension, ChunkXZ chunkXZ) {
		this.dimension = dimension;
		this.chunkXZ = chunkXZ;
		this.cache = new LinkedList();
		this.savePath = ModWorldRegrowth.getSavePrefix() + "dim" + dimension + File.separator;
		ModWorldRegrowth.createSavePath(savePath);
	}


	/** load chunk from file if available **/
	protected void loadChunk() {
		File file = new File(savePath + chunkXZ.getFilename());
		if ( !file.exists() ) return;
		ModWorldRegrowth.info("loadChunk(): %s", file.getAbsolutePath());

		BufferedReader buf = ChunkDataHelper.openFileForReading(file);
		if ( buf == null ) {
			ModWorldRegrowth.warning("error opening file: %s", file.getAbsolutePath());
			return;
		}

		RegrowthBlock block = ChunkDataHelper.readBlock(buf);
		if ( block == null ) {
			ModWorldRegrowth.info("loading empty file: %s", file.getAbsolutePath());
			return;
		}
		while ( block != null ) {
			ModWorldRegrowth.info("loadChunk(): loading block %s", block.toString());
			cache.add(block);
			block = ChunkDataHelper.readBlock(buf);
		}

		// close stupid
		try {
			buf.close();
		} catch (IOException e) {
			ModWorldRegrowth.warning("error closing file: %s", file.getAbsolutePath());
			ModWorldRegrowth.warning("%s", e.getStackTrace()[0]);
		}
	}


	/** save chunk to file or remove if empty **/
	protected void saveChunk() {
		File file = new File(savePath + chunkXZ.getFilename());
		if ( file.exists() ) file.delete();
		if ( cache.isEmpty() ) {
			//ModWorldRegrowth.info("saveChunk(): skipping empty chunk %s", chunkXZ.toString());
			return;
		}
		if ( !file.exists() ) try {
			file.createNewFile();
		} catch (IOException e) {
			ModWorldRegrowth.warning("cannot create file: %s", file.getAbsolutePath());
			ModWorldRegrowth.warning("%s", e.getStackTrace()[0]);
			return;
			//FMLCommonHandler.instance().exitJava(1, false);
		}
		ModWorldRegrowth.info("saveChunk(): %s", file.getAbsolutePath());

		BufferedWriter buf = ChunkDataHelper.openFileForWriting(file);
		if ( buf == null ) {
			ModWorldRegrowth.warning("error writing file: %s", file.getAbsolutePath());
			return;
		}

		for ( RegrowthBlock block : cache.toArray(new RegrowthBlock[0]) ) {
			ModWorldRegrowth.info("saveChunk(): writing block %s", block.toString());
			if ( ChunkDataHelper.writeBlock(buf, block) ) continue;
			ModWorldRegrowth.warning("error writing file: %s", file.getAbsolutePath());
			ModWorldRegrowth.warning("error while writing block: %s", block.toString());
			return;
			//FMLCommonHandler.instance().exitJava(1, false);
		}

		try {
			buf.close();
		} catch (IOException e) {
			ModWorldRegrowth.warning("error closing file: %s", file.getAbsolutePath());
			ModWorldRegrowth.warning("save operation may have failed!", file.getAbsolutePath());
			ModWorldRegrowth.warning("%s", e.getStackTrace()[0]);
		}

	}


	/** add regrowth block to the end of the queue **/
	public void queue(RegrowthBlock block) {
		if ( this.cache.isEmpty() ) timer = 0;
		this.cache.offer(block);
	}


	/** read next regrowth block if ready **/
	public RegrowthBlock read() {
		RegrowthBlock block = cache.peek();
		if ( block == null ) return null;
		if ( timer < block.lifespan ) {  // require enough time elapsed
			//ModWorldRegrowth.info("chunk%s: tick %d", chunkXZ.toString(), timer);
			timer++;
			return null;
		}
		return cache.poll();
	}


	/** remove regrowth within range of position **/
	public void dropNearPoint(int posX, int posY, int posZ, int range) {
		if ( cache.isEmpty() ) return;

		int minX = posX - range;
		int maxX = posX + range;
		int minY = posY - range;
		int maxY = posY + range;
		int minZ = posZ - range;
		int maxZ = posZ + range;

		for ( RegrowthBlock block : cache.toArray(new RegrowthBlock[0]) ) {
			if ( block.posX < minX || block.posX > maxX ) continue;
			if ( block.posZ < minZ || block.posZ > maxZ ) continue;
			if ( block.posY < minY || block.posY > maxY ) continue;
			//ModWorldRegrowth.info("chunk %s: removing %s", chunkXZ.toString(), block.toString());
			cache.remove(block);
		}
	}


	/** the timer must be reset manually after a successful read; this is to allow re-queuing data */
	public void resetTimer() {
		timer = 0;
	}

}
