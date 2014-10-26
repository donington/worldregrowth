package mods.minecraft.donington.worldregrowth.common.regrowth.chunk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import mods.minecraft.donington.worldregrowth.ModWorldRegrowth;
import mods.minecraft.donington.worldregrowth.common.regrowth.RegrowthBlock;
import net.minecraft.block.Block;

public class ChunkDataHelper {

	public static BufferedWriter openFileForWriting(File file) {
		FileWriter fw;
		try {
			fw = new FileWriter(file.getAbsoluteFile());
		} catch (IOException e) {
			return null;
		}
		return new BufferedWriter(fw);
	}


	public static BufferedReader openFileForReading(File file) {
		FileReader fr;
		try {
			fr = new FileReader(file.getAbsoluteFile());
		} catch (IOException e) {
			return null;
		}
		return new BufferedReader(fr);
	}


	public static RegrowthBlock readBlock(BufferedReader buf) {
		String line = null;
		try {
			line = buf.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if ( line == null ) return null;

		String args[] = line.split(":");
		if ( args.length != 4 ) {
			ModWorldRegrowth.warning("readBlock(): error parsing line '%s'", line);
			return null;			
		}

		String blockName;
		Integer stateID;
		String posArgs[];
		int pos[];

		blockName = args[0] + ":" + args[1];
		if ( !Block.blockRegistry.containsKey(blockName) ) {
			ModWorldRegrowth.warning("readBlock(): invalid block '%s'", blockName);
			return null;
		}

		try {
			stateID = Integer.parseInt(args[2]);
		} catch ( NumberFormatException e ) {
			ModWorldRegrowth.warning("readBlock(): %s: invalid stateID '%s'", args[0], args[1]);
			return null;
		}
		/* 1.8
		if ( Block.getStateById(stateID) != null ) {
			ModWorldRegrowth.warning("readBlock(): %s: invalid stateID '%s'", args[0], args[1]);
			return null;
		}
		 */

		posArgs = args[3].split(",");
		if ( posArgs.length != 3 ) {
			ModWorldRegrowth.warning("readBlock(): %s:%d: invalid position (%s)", args[0], args[1], args[2]);
			return null;
		}
		pos = new int[3];
		try {
			pos[0] = Integer.parseInt(posArgs[0]);
			pos[1] = Integer.parseInt(posArgs[1]);
			pos[2] = Integer.parseInt(posArgs[2]);
		} catch ( NumberFormatException e ) {
			ModWorldRegrowth.warning("readBlock(): %s:%d: invalid position (%s,%s,%s)", args[0], args[1], posArgs[0], posArgs[1], posArgs[2]);
			return null;
		}

		return new RegrowthBlock((Block)Block.blockRegistry.getObject(blockName), stateID, pos[0], pos[1], pos[2]);
	}


	public static boolean writeBlock(BufferedWriter buf, RegrowthBlock block) {
		try {
			buf.write(block.toString());
			buf.newLine();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
