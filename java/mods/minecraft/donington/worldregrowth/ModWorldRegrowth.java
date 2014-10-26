package mods.minecraft.donington.worldregrowth;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import mods.minecraft.donington.worldregrowth.common.regrowth.RegrowthRegistry;
import mods.minecraft.donington.worldregrowth.common.regrowth.RegrowthCache;
import mods.minecraft.donington.worldregrowth.common.regrowth.event.ChunkEventHandler;
import mods.minecraft.donington.worldregrowth.common.regrowth.event.TickEventHandler;
import mods.minecraft.donington.worldregrowth.common.regrowth.event.WorldEventHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@Mod(modid = ModWorldRegrowth.MOD_ID, name = ModWorldRegrowth.MOD_NAME, version = ModWorldRegrowth.MOD_VERSION)
public class ModWorldRegrowth {
	public static final String MOD_ID       = "worldregrowth";
	public static final String MOD_NAME     = "World Regrowth";
	public static final String MOD_VERSION  = "0";

	@Instance(MOD_ID)
	public static ModWorldRegrowth instance;

	//@SidedProxy(clientSide = "mods.minecraft.donington.worldregrowth.client.ClientProxy", serverSide = "mods.minecraft.donington.worldregrowth.common.CommonProxy")
	//public static CommonProxy proxy;

	private boolean clientDetected;
	private RegrowthCache regrowthCache;
	private static ChunkEventHandler chunkEventHandler;
	private static WorldEventHandler worldEventHandler;
	private static TickEventHandler tickEventHandler;


	private static Configuration config;
	private static String savePrefix;


	@SideOnly(Side.CLIENT)
	private void validateClient() {
		clientDetected = true;
		warning("client detected, refusing to load ModWorldRegrowth");
	}


	private void detectClient() {
		try {
			validateClient();
		} catch ( NoSuchMethodError e ) {
			clientDetected = false;
			info("client not detected, loading ModWorldRegrowth");
		}
	}


	private void loadConfig(File configPrefix, File configFile) {
		savePrefix = configPrefix.getAbsolutePath() + File.separator + MOD_ID + File.separator;
		createSavePath(savePrefix);

		/*
		File chunkSavePrefix = new File(savePrefix);
		if ( !chunkSavePrefix.isDirectory() && chunkSavePrefix.exists() )
			throw new IllegalStateException(chunkSavePrefix.getAbsolutePath() + " is not a directory");
		if ( !chunkSavePrefix.exists() )
			chunkSavePrefix.mkdir();
		 */


		config = new Configuration(configFile);
		config.load();

		//worldSavePrefix = config.get(config.CATEGORY_GENERAL, "derop", true).getBoolean(true);
	}


	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		detectClient();
		if ( clientDetected ) return;
		loadConfig(event.getModConfigurationDirectory(), event.getSuggestedConfigurationFile());

		regrowthCache = new RegrowthCache();
		regrowthCache.loadDimension(0);  // enable dimension 0

		chunkEventHandler = new ChunkEventHandler(regrowthCache);
		worldEventHandler = new WorldEventHandler(regrowthCache);
		tickEventHandler = new TickEventHandler(regrowthCache);
	}


	@EventHandler
	public void init(FMLInitializationEvent event) {
		if ( clientDetected ) return;

		new RegrowthRegistry();
	}


	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if ( clientDetected ) return;

	}


	@EventHandler
	public void serverStartup(FMLServerAboutToStartEvent event) {
		if ( clientDetected ) return;

		MinecraftForge.EVENT_BUS.register(chunkEventHandler);
		MinecraftForge.EVENT_BUS.register(worldEventHandler);
		FMLCommonHandler.instance().bus().register(tickEventHandler);

		/* why is this wrong
		File savePrefix = new File("./config/regrowth");
		if ( !savePrefix.isDirectory() && savePrefix.exists() )
			throw new IllegalStateException(savePrefix.getAbsolutePath() + " is not a directory");
		if ( !savePrefix.exists() )
			savePrefix.mkdir();
		worldSavePrefix = savePrefix.getAbsolutePath();
		ModWorldRegrowth.info("save path := " + worldSavePrefix);
		 */
	}


	@EventHandler
	public void serverStopped(FMLServerStoppedEvent event) {
		if ( clientDetected ) return;

		MinecraftForge.EVENT_BUS.unregister(chunkEventHandler);
		MinecraftForge.EVENT_BUS.unregister(worldEventHandler);
		FMLCommonHandler.instance().bus().unregister(tickEventHandler);

		regrowthCache.unloadAll();
	}


	public static void createSavePath(String pathname) {
		File path = new File(pathname);
		if ( !path.isDirectory() && path.exists() )
			throw new IllegalStateException(path.getAbsolutePath() + " is not a directory");
		if ( !path.exists() )
			path.mkdir();
	}

	public static String getSavePrefix() {
		return savePrefix;
	}

	public static void info(String fmt, Object... data) {
		FMLLog.info("["+MOD_NAME+"]: "+fmt, data);
	}


	public static void warning(String fmt, Object... data) {
		FMLLog.warning("["+MOD_NAME+"]: "+fmt, data);
	}
	
}
