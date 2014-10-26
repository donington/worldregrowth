package mods.minecraft.donington.worldregrowth.common.regrowth.event;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import mods.minecraft.donington.worldregrowth.ModWorldRegrowth;
import mods.minecraft.donington.worldregrowth.common.regrowth.RegrowthBlock;
import mods.minecraft.donington.worldregrowth.common.regrowth.RegrowthCache;
import mods.minecraft.donington.worldregrowth.common.regrowth.chunk.ChunkXZ;
import mods.minecraft.donington.worldregrowth.common.regrowth.chunk.RegrowthChunk;
import mods.minecraft.donington.worldregrowth.common.regrowth.chunk.RegrowthChunkCache;

public class TickEventHandler {
	private final RegrowthCache regrowthCache;

	private static final int TICKS_PER_SECOND = 20;
	private static final int REGEN_PLAYER_RANGE = 16;
	private static final int MAX_REGROWTH_FAILURES = 5;


	public TickEventHandler(RegrowthCache regrowthCache) {
		this.regrowthCache = regrowthCache;
	}


	public boolean canBlockExist(World world, RegrowthBlock block) {
		return world.isAirBlock(block.posX, block.posY, block.posZ);		
	}


	public boolean isPlayerTooClose(World world, RegrowthBlock block) {
		if ( world.getClosestPlayer(block.posX, block.posY, block.posZ, REGEN_PLAYER_RANGE) != null ) {
			//ModWorldRegrowth.info("player blocking regeneration of %s", block.toString());
			return true;
		}
		return false;
	}


	public boolean canBlockRegenerate(World world, RegrowthBlock block) {
		if ( ( block.posY-1 > 0 && !world.isAirBlock(block.posX, block.posY-1, block.posZ) ) ||
			 !world.isAirBlock(block.posX-1, block.posY, block.posZ) ||
			 !world.isAirBlock(block.posX+1, block.posY, block.posZ) ||
			 !world.isAirBlock(block.posX, block.posY, block.posZ-1) ||
			 !world.isAirBlock(block.posX, block.posY, block.posZ+1) )
			return true;
		return false;
	}


	@SubscribeEvent
	public void onTick(WorldTickEvent event) {
		if ( !event.phase.equals(Phase.START) ) return;

		World world = event.world;
		RegrowthChunkCache chunkCache = regrowthCache.getDimension(world.provider.dimensionId);
		if ( chunkCache == null ) return;

		int timer = chunkCache.getTimer();
//		ModWorldRegrowth.info("tick %d", timer);
//		if ( timer >= TICKS_PER_SECOND ) {
		if ( timer >= 1 ) {
//		if ( timer >= 5 ) {
			//ModWorldRegrowth.info("regrowth tick DIM%d", world.provider.dimensionId);

			for ( ChunkXZ chunkXZ : chunkCache.getLoadedChunks() ) {
				RegrowthChunk chunk = chunkCache.getChunk(chunkXZ);
				RegrowthBlock block = chunk.read();
				if ( block == null || !canBlockExist(world, block) )
					continue;

				// re-queue block if it cannot regenerate here
				if ( !canBlockRegenerate(world, block) ) {
					block.fail();
					if ( block.getFailures() > MAX_REGROWTH_FAILURES ) {
						ModWorldRegrowth.info("too many failures; dropping block %s", block.toString());
						continue;
					}
					chunk.queue(block);
					continue;
				}

                // re-queue block if player is too close
                if ( isPlayerTooClose(world, block) ) {
                    ModWorldRegrowth.info("player too close to block %s", block.toString());
                    chunk.queue(block);
                    continue;
                }

				//ModWorldRegrowth.info("restoring %s", block.toString());
				// add the block to the world, trigger block update, and notify client
				ModWorldRegrowth.info("restoring block %s", block.toString());
				world.setBlock(block.posX, block.posY, block.posZ, block.block, block.stateID, 3);
				chunk.resetTimer();  // reset timer on chunk
			}
			chunkCache.resetTimer();  // reset timer on cache
			return;
		}
		chunkCache.incrementTimer();  // increment cache timer
	}

}
