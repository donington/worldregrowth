package mods.minecraft.donington.worldregrowth.common.regrowth;

import net.minecraft.block.Block;

public class RegrowthBlock {
	public final Block block;
	public final int stateID;
	public final int posX, posY, posZ;
	public final int lifespan;

	private int failures;


	public RegrowthBlock(Block block, int stateID, int posX, int posY, int posZ) {
		this.block = block;
		this.stateID = stateID;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.lifespan = RegrowthRegistry.getLifespan(block);

		this.failures = 0;
	}


	public int getFailures() {
		return failures;
	}


	public void fail() {
		failures++;
	}


	@Override
	public String toString() {
		return new StringBuilder().append(Block.blockRegistry.getNameForObject(block)).append(":").append(stateID).append(":").append(posX).append(",").append(posY).append(",").append(posZ).toString();
	}
	
}
