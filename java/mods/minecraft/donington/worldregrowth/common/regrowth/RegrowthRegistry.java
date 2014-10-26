package mods.minecraft.donington.worldregrowth.common.regrowth;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class RegrowthRegistry {
	private static Set<Block> regrowthBlocks;
	private static Set<Block> oreBlocks;
	private static Set<Block> ignoredBlocks;

	private static Map<Integer,LinkedList<RegrowthBlock>> cache;

	private static final int BLOCK_RESPAWN_TIME = 120;
	private static final int ORE_RESPAWN_TIME = 300;


	public RegrowthRegistry() {
		Set t;

		t = new HashSet();
		// generate regrowth table
		t.add(Blocks.stone);
		t.add(Blocks.grass);
		t.add(Blocks.dirt);
		t.add(Blocks.sand);
		t.add(Blocks.gravel);
		t.add(Blocks.log);
		t.add(Blocks.log2);
		t.add(Blocks.leaves);
		t.add(Blocks.leaves2);
		t.add(Blocks.clay);
		t.add(Blocks.netherrack);
		t.add(Blocks.soul_sand);
		regrowthBlocks = t;

		t = new HashSet();
		/* generate ore table */
		t.add(Blocks.gold_ore);
		t.add(Blocks.iron_ore);
		t.add(Blocks.coal_ore);
		t.add(Blocks.lapis_ore);
		t.add(Blocks.diamond_ore);
		t.add(Blocks.redstone_ore);
		t.add(Blocks.emerald_ore);
		t.add(Blocks.quartz_ore);
		oreBlocks = t;

		t = new HashSet();
		/* generate ignored table */
		t.add(Blocks.torch);
		ignoredBlocks = t;
	}


	/* return if the block is ignored or not */
	public static boolean isBlockIgnored(Block block) {
		return ignoredBlocks.contains(block);
	}


	public static boolean isValid(Block block) {
		/* not sure if this will increase efficiency
		if ( block.getCreativeTabToDisplayOn() != CreativeTabs.tabBlock )
			return false;
		Material material = block.getMaterial();
		if ( material.isLiquid() ) return false;
		if ( !material.isOpaque() && material != material.leaves && material != material.grass )
			return false;
		 */

		return ( regrowthBlocks.contains(block) || oreBlocks.contains(block) );
	}


	public static int getLifespan(Block block) {
		if ( oreBlocks.contains(block) )
			return ORE_RESPAWN_TIME;
		return BLOCK_RESPAWN_TIME;
	}

}
