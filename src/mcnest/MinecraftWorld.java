package mcnest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import nbt.*;

public class MinecraftWorld {
	
	private final int NUM_CHUNKS = 7;
	private final int CHUNK_OFFSET_MAX = (NUM_CHUNKS - 1) / 2;
	private final int CHUNK_ARRAY_OFFSET_X;
	private final int CHUNK_ARRAY_OFFSET_Z;
	
	private int playerChunkX;
	private int playerChunkZ;
	
	private String basePath;
	private String worldName;
	
	private Chunk[][] chunks;
	
	public MinecraftWorld(String basePath, String worldName) {
		this.worldName = worldName;
		this.basePath = basePath;
		
		this.chunks = new Chunk[NUM_CHUNKS][NUM_CHUNKS];
		
		CHUNK_ARRAY_OFFSET_X = playerChunkX - CHUNK_OFFSET_MAX;
		CHUNK_ARRAY_OFFSET_Z = playerChunkZ - CHUNK_OFFSET_MAX;
	}
	
	public Tag LoadChunk(int x, int z) {
		File chunkFile = this.GetChunkFile(x, z);
		
		// ensure the file exists
		if (!chunkFile.exists()) {
			// TODO: Throw exception if it doesn't
			return null;
		}
		
		// read in the chunk
		Tag chunkTag = DTFReader.readDTFFile(chunkFile);
		
		// place the chunk
		if (chunkTag != null) {
			chunks[x - CHUNK_ARRAY_OFFSET_X][z - CHUNK_ARRAY_OFFSET_Z] = new Chunk(chunkTag);
		}
		else {
			// TODO: Throw and exception
		}
		
		return chunkTag;
	}
	
	private File GetChunkFile(int x, int z) {
		// convert world coords to chunk coords
		int x2 = x % 64;
		int z2 = z % 64;
		
		// ensure x and z are not negative
		if (x2 < 0)
			x2 = x2 + 64;
		
		if (z2 < 0)
			z2 = z2 + 64;
		
		// build the folder and file names
		String firstFolder = Integer.toString(x2, 36);
		String secondFolder = Integer.toString(z2, 36);
		String filename = "c." + Integer.toString(x, 36) + "." + Integer.toString(z, 36) + ".dat";
		
		// open the file
		File chunkFile = new File(this.basePath + firstFolder + "/" + secondFolder + "/" + filename);
		
		return chunkFile;
	}
}
