package mcnest;

public class Blocks {
	private final int CHUNK_X = 16;
	private final int CHUNK_Y = 128;
	private final int CHUNK_Z = 16;
	
	private byte[] blocks;
	
	public Blocks(byte[] blocks) 
	{
		this.blocks = blocks;
	}
	
	public byte getBlock(int x, int y, int z)
	{
		return blocks[y + (z * CHUNK_Y + (x * CHUNK_Y * CHUNK_Z))];
	}
	
	public void setBlock(int x, int y, int z, byte newBlock)
	{
		blocks[y + (z * CHUNK_Y + (x * CHUNK_Y * CHUNK_Z))] = newBlock;
	}
	
	public byte[] getBlocks()
	{
		return blocks;
	}
}
