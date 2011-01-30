package mcnest;

import nbt.IntTag;
import nbt.Tag;

public class Chunk {

	private Tag tag;
	private int x, z;
	
	public Chunk(int x, int z, Tag chunkTag) {
		// TODO Auto-generated constructor stub
		this.tag = chunkTag;
		
		this.x = x;
		this.z = z;
	}
	
	public IntTag getBlockTag() {
		// TODO: Extract blocks from a chunk and return them
		
		return null;
	}
	
	@Override
	public String toString() {
		return "[" + x + ", " + z + "]";
	}
	
}
