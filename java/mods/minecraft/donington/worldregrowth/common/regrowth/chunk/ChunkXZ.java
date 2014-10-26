package mods.minecraft.donington.worldregrowth.common.regrowth.chunk;

import java.util.ArrayList;

import net.minecraft.world.chunk.Chunk;

public class ChunkXZ {
	public final int xPos;
	public final int zPos;
	private final int hash;


	/** generate ChunkXZ from provided Chunk object */
	public ChunkXZ(Chunk chunk) {
		this.xPos = chunk.xPosition;
		this.zPos = chunk.zPosition;
		this.hash = (xPos/2) + ( (zPos/2) << 16 );
	}


	/** internal use only */
	private ChunkXZ(int posX, int posZ) {
		this.xPos = posX;
		this.zPos = posZ;
		this.hash = (xPos/2) + ( (zPos/2) << 16 );
	}


	/** returns an array of references to surrounding chunks */
	public ChunkXZ[] getSurroundingChunks() {
		ChunkXZ[] xzlist = new ChunkXZ[8];
		xzlist[0] = new ChunkXZ(this.xPos, this.zPos+1);
		xzlist[1] = new ChunkXZ(this.xPos-1, this.zPos+1);
		xzlist[2] = new ChunkXZ(this.xPos-1, this.zPos);
		xzlist[3] = new ChunkXZ(this.xPos-1, this.zPos-1);
		xzlist[4] = new ChunkXZ(this.xPos,   this.zPos-1);
		xzlist[5] = new ChunkXZ(this.xPos+1, this.zPos-1);
		xzlist[6] = new ChunkXZ(this.xPos+1, this.zPos);
		xzlist[7] = new ChunkXZ(this.xPos+1, this.zPos+1);
		return xzlist;
	}


	/** returns the file name to use for this chunk */
	public String getFilename() {
		return new StringBuilder().append("chunk.").append(this.xPos).append(".").append(this.zPos).append(".dat").toString();
	}


	public boolean equals(ChunkXZ chunkXZ) {
		if ( chunkXZ.xPos == this.xPos && chunkXZ.zPos == this.zPos )
			return true;
		return false;
	}


	@Override
	public boolean equals(Object obj) {
		if ( obj.getClass().equals(this.getClass()) )
			return equals((ChunkXZ)obj);
		return false;
	}


	@Override
	public int hashCode() {
		return hash;
	}


	@Override
	public String toString() {
		return new StringBuilder().append("[").append(this.xPos).append(",").append(this.zPos).append("]").toString();
	}
}
