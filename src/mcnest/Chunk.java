package mcnest;

import java.io.File;
import java.io.FileNotFoundException;

import nbt.*;

public class Chunk {

	private CompoundTag tag;
	private int x, z;
	private String path;
	
	public Chunk(int x, int z, CompoundTag chunkTag, String path) {
		this.tag = chunkTag;
		
		this.x = x;
		this.z = z;
		
		this.path = path;
	}
	
	public ByteArrayTag getBlockTag() {
		return (ByteArrayTag) this.tag.getTagWithName("Blocks");
	}
	
	@Override
	public String toString() {
		return "[" + x + ", " + z + "]";
	}
	
	public void Save() {
		// TODO: Implement Chunk.Save()
	}
	
	public static Chunk Load(int x, int z, String basePath, String worldName) throws FileNotFoundException {
		File chunkFile = GetChunkFile(x, z, basePath, worldName);
		
		// ensure the file exists
		if (!chunkFile.exists()) {
			throw new FileNotFoundException("The chunk (" + x + ", " + z + ") has failed to load. This is likely due to Minecraft not having initialized nearby chunks.");
		}
		
		// read in the chunk
		Tag chunkTag = DTFReader.readDTFFile(chunkFile);
		
		// place the chunk
		return new Chunk(x, z, (CompoundTag)chunkTag, chunkFile.getAbsolutePath());
	}
	
	private static File GetChunkFile(int x, int z, String basePath, String worldName) {
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
		File chunkFile = new File(basePath + worldName + "/" + firstFolder + "/" + secondFolder + "/" + filename);
		
		return chunkFile;
	}

	public byte getBlock(int blockX, int y, int blockZ) {
		ByteArrayTag blockTag = (ByteArrayTag) this.tag.getTagWithName("Blocks");
		
		return blockTag.value[y + (z * 128 + (x * 128 * 16))];
	}
	
}
