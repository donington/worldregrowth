package mods.minecraft.donington.worldregrowth.common.regrowth.event;

import mods.minecraft.donington.worldregrowth.ModWorldRegrowth;
import mods.minecraft.donington.worldregrowth.common.regrowth.RegrowthBlock;
import mods.minecraft.donington.worldregrowth.common.regrowth.RegrowthRegistry;
import mods.minecraft.donington.worldregrowth.common.regrowth.RegrowthCache;
import mods.minecraft.donington.worldregrowth.common.regrowth.chunk.ChunkXZ;
import mods.minecraft.donington.worldregrowth.common.regrowth.chunk.RegrowthChunk;
import mods.minecraft.donington.worldregrowth.common.regrowth.chunk.RegrowthChunkCache;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ChunkEventHandler {
	private static RegrowthCache regrowthCache;


	public ChunkEventHandler(RegrowthCache regrowthCache) {
		this.regrowthCache = regrowthCache;
	}


	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event) {
		RegrowthChunkCache chunkCache = regrowthCache.getDimension(event.world.provider.dimensionId);
		if ( chunkCache == null ) return;
		chunkCache.loadChunk(new ChunkXZ(event.getChunk()));
		//ModWorldRegrowth.info("onChunkLoad(): %s", chunkCache.getLoadedChunks().toString());
	}


	@SubscribeEvent
	public void onChunkUnload(ChunkEvent.Unload event) {
		RegrowthChunkCache chunkCache = regrowthCache.getDimension(event.world.provider.dimensionId);
		if ( chunkCache == null ) return;
		chunkCache.saveChunk(new ChunkXZ(event.getChunk()));
		//ModWorldRegrowth.info("onChunkUnload(): %s", chunkCache.getLoadedChunks().toString());
	}

}
