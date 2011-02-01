package mcloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

//import nbt.*;
import org.jnbt.*;

public class Chunk {

	private CompoundTag tag;
	private CompoundTag levelTag;
	private ByteArrayTag blockTag;
	
	private byte[] blocks;
	
	private int x, z;
	private String path;
	
	public Chunk(int x, int z, CompoundTag chunkTag, String path) {
		this.tag = chunkTag;
		
		this.x = x;
		this.z = z;
		
		this.path = path;
		
//		CompoundTag levelTag = (CompoundTag) this.tag.getTagWithName("Level"); 
//		this.blockTag = (ByteArrayTag) levelTag.getTagWithName("Blocks");
//		this.blocks = this.blockTag.value;
		this.levelTag = (CompoundTag) this.tag.getValue().get("Level");
		this.blockTag = (ByteArrayTag) levelTag.getValue().get("Blocks");
		this.blocks = this.blockTag.getValue();
	}
	
//	public ByteArrayTag getBlockTag() {
//		return (ByteArrayTag) this.tag.getTagWithName("Blocks");
//	}
	
	@Override
	public String toString() {
		return "[" + x + ", " + z + "]";
	}
	
	public void Save() throws IOException {
		// TODO: Implement Chunk.Save()
		File outFile = new File(path);
		FileOutputStream fout = new FileOutputStream(outFile);
		NBTOutputStream nout = new NBTOutputStream(fout);
		
		HashMap<String, Tag> levelTagMap = new HashMap<String, Tag>(this.levelTag.getValue());
		levelTagMap.put("Blocks", new ByteArrayTag("Blocks", this.blocks));
		
		HashMap<String, Tag> tagMap = new HashMap<String, Tag>(this.tag.getValue());
		tagMap.put("Level", this.levelTag);
		
		Tag t = new CompoundTag("", tagMap);
		nout.writeTag(t);
		
		nout.close();
		fout.close();
	}
	
	public static Chunk Load(int x, int z, String basePath, String worldName) throws IOException {
		File chunkFile = GetChunkFile(x, z, basePath, worldName);
		
		// ensure the file exists
		if (!chunkFile.exists()) {
			throw new FileNotFoundException("The chunk (" + x + ", " + z + ") has failed to load. This is likely due to Minecraft not having initialized nearby chunks.");
		}
		
		// read in the chunk
//		Tag chunkTag = DTFReader.readDTFFile(chunkFile);
		FileInputStream fin = new FileInputStream(chunkFile);
		NBTInputStream nbtin = new NBTInputStream(fin);
		Tag chunkTag = nbtin.readTag();
		
		nbtin.close();
		fin.close();
		
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

	public byte getBlock(int x, int y, int z) {
		return this.blocks[y + (z * 128 + (x * 128 * 16))];
	}
	
	public void setBlock(int x, int y, int z, byte block) {
		this.blocks[y + (z * 128 + (x * 128 * 16))] = block;
	}
	
}
