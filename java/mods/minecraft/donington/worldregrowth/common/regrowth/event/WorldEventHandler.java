package mods.minecraft.donington.worldregrowth.common.regrowth.event;

import mods.minecraft.donington.worldregrowth.ModWorldRegrowth;
import mods.minecraft.donington.worldregrowth.common.regrowth.RegrowthBlock;
import mods.minecraft.donington.worldregrowth.common.regrowth.RegrowthCache;
import mods.minecraft.donington.worldregrowth.common.regrowth.RegrowthRegistry;
import mods.minecraft.donington.worldregrowth.common.regrowth.chunk.ChunkXZ;
import mods.minecraft.donington.worldregrowth.common.regrowth.chunk.RegrowthChunk;
import mods.minecraft.donington.worldregrowth.common.regrowth.chunk.RegrowthChunkCache;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WorldEventHandler {
	private final RegrowthCache regrowthCache;
	private static WorldEventHandler instance;

	public WorldEventHandler(RegrowthCache regrowthCache) {
		this.regrowthCache = regrowthCache;
		this.instance = this;
	}


	@SubscribeEvent
	public void onBlockBreak(BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if ( player != null && player.capabilities.isCreativeMode ) {
			//ModWorldRegrowth.info("onBlockBreak(): ignoring creative mode player");
			return;
		}

		Block block = event.block;
		if ( !RegrowthRegistry.isValid(block) ) return;

		// this will be replaced with BlockState in 1.8
		int metadata = event.world.getBlockMetadata(event.x, event.y, event.z);
        /* 1.8: block states are registered in a map,
         *  so can be indexed as such
         *
        int stateID = Block.getStateId(state);
        world.setBlockState(pos, Block.getStateById(stateID));
         */


		Chunk worldChunk = event.world.getChunkFromBlockCoords(event.x, event.z);
		RegrowthChunkCache chunkCache = regrowthCache.getDimension(event.world.provider.dimensionId);
		RegrowthChunk chunk = chunkCache.getChunk(new ChunkXZ(worldChunk));
		chunk.queue(new RegrowthBlock(block, metadata, event.x, event.y, event.z));
	}


	@SubscribeEvent
	public void onBlockPlace(PlaceEvent event) {
		/* creative mode should still stop natural regeneration
		if ( event.player.capabilities.isCreativeMode ) {
			//ModWorldRegrowth.info("onBlockPlace(): ignoring creative mode player");
			return;
		}
		 */

		if ( RegrowthRegistry.isBlockIgnored(event.block) )
			return;

		RegrowthChunkCache chunkCache = regrowthCache.getDimension(event.world.provider.dimensionId);

		ChunkXZ chunkxz = new ChunkXZ(event.world.getChunkFromBlockCoords(event.x, event.z));
		chunkCache.getChunk(chunkxz).dropNearPoint(event.x, event.y, event.z, 5);

		for ( ChunkXZ xz : chunkxz.getSurroundingChunks() ) {
			RegrowthChunk chunk = chunkCache.getChunk(xz);
			if ( chunk != null )
				chunkCache.getChunk(xz).dropNearPoint(event.x, event.y, event.z, 5);
		}

	}


	@SubscribeEvent
	public void onHarvestBlock(HarvestDropsEvent event) {
		
	}


	@SubscribeEvent
	public void onExplosionStart(ExplosionEvent.Start event) {
	    //event.setCanceled(true);
	}


	@SubscribeEvent
	public void onExplosionDetonate(ExplosionEvent.Detonate event) {
	    if (!event.explosion.isSmoking) return;

	    RegrowthChunkCache chunkCache = regrowthCache.getDimension(event.world.provider.dimensionId);

	    for ( ChunkPosition pos : event.getAffectedBlocks() ) {
            Block block = event.world.getBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
            if ( !RegrowthRegistry.isValid(block) ) continue;

            int metadata = event.world.getBlockMetadata(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
            ChunkXZ chunkxz = new ChunkXZ(event.world.getChunkFromBlockCoords(pos.chunkPosX, pos.chunkPosZ));
	        chunkCache.getChunk(chunkxz).queue(new RegrowthBlock(block, metadata, pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ));
	    }

	    event.getAffectedEntities().clear();
	}

}
