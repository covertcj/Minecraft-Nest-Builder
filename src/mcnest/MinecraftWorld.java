package mcnest;

import java.io.File;
import java.io.FileNotFoundException;

import nbt.*;

public class MinecraftWorld {
	
	private final int NUM_CHUNKS = 7;
	private final int CHUNK_OFFSET_MAX = (NUM_CHUNKS - 1) / 2;
	private final int CHUNK_ARRAY_OFFSET_X;
	private final int CHUNK_ARRAY_OFFSET_Z;
	
	private int playerChunkX;
	private int playerChunkZ;
	
	private int playerX;
	private int playerY;
	private int playerZ;
	
	private String basePath;
	private String worldName;
	
	private Chunk[][] chunks;
	
	public MinecraftWorld(String basePath, String worldName) {
		this.worldName = worldName;
		this.basePath = basePath;
		
		// holds all loaded chunks
		this.chunks = new Chunk[NUM_CHUNKS][NUM_CHUNKS];
		
		// load the player information from the level.dat file
		try {
			LoadPlayerInformation();
		}
		catch (Exception e) {
			System.exit(1);
		}
		
		// find the offset to help find a mapping between our array and minecraft coordinates
		CHUNK_ARRAY_OFFSET_X = playerChunkX - CHUNK_OFFSET_MAX;
		CHUNK_ARRAY_OFFSET_Z = playerChunkZ - CHUNK_OFFSET_MAX;
		
		// load in all of the necessary chunks
		LoadWorld();
	}
	
	public byte[][][] GetWorldBlocks() {
		int size = NUM_CHUNKS * 16;
		byte[][][] blocks = new byte[size][128][size];
		
		// TODO: Convert the chunks to a 3D block array
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < 128; y++) {
				for (int z = 0; z < size; z++) {
					blocks[x][y][z] = this.GetBlockAt(x, y, z);
				}
			}
		}
		
		return blocks;
	}
	
	private byte GetBlockAt(int x, int y, int z) {
		int chunkX = -x / 16;
		int chunkZ = -z / 16;	
		int blockX = x % 16;
		int blockZ = x % 16;
		
		byte block = chunks[chunkX][chunkZ].getBlock(blockX, y, blockZ);
		
		return block;
	}
	
	public void SetWorldBlocks(byte[][][] blocks) {
		// TODO: Convert the 3D block array to Chunks and save
	}
	
	private void LoadPlayerInformation() throws Exception {
		// find the player's position
		File levelFile = new File(basePath + worldName + "/level.dat");
		CompoundTag worldData = (CompoundTag)DTFReader.readDTFFile(levelFile);
		CompoundTag worldDataData = (CompoundTag)worldData.getTagWithName("Data");
		CompoundTag worldPlayerData = (CompoundTag)worldDataData.getTagWithName("Player");
		
		if (worldPlayerData != null) {
			ListTag playerPos = (ListTag) worldPlayerData.getTagWithName("Pos");
			this.playerX = (int)((DoubleTag) playerPos.value.get(0)).value;
			this.playerY = (int)((DoubleTag) playerPos.value.get(1)).value;
			this.playerZ = (int)((DoubleTag) playerPos.value.get(2)).value;
			
			// TODO: Why is this negative in the other person's code?
			this.playerChunkX = -this.playerX / 16;
			this.playerChunkZ = -this.playerZ / 16;
		}
		else {
			throw new Exception("The player tag does not exist.");
		}
	}
	
	private void LoadWorld() {
		// loop through the chunk array and load corresponding chunks
		for (int i = -this.CHUNK_OFFSET_MAX; i <= this.CHUNK_OFFSET_MAX; i++) {
			for (int j = -this.CHUNK_OFFSET_MAX; j <= this.CHUNK_OFFSET_MAX; j++) {
				
				// find the minecraft coords of the chunk to load
				int x = i + this.playerChunkX;
				int z = j + this.playerChunkZ;
				
				// load the chunk
				try {
					chunks[x - CHUNK_ARRAY_OFFSET_X][z - CHUNK_ARRAY_OFFSET_Z] = Chunk.Load(x, z, this.basePath, this.worldName);
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
