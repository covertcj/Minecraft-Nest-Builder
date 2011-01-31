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
	
	public int[][][] GetWorldBlocks() {
		// TODO: Convert the chunks to a 3D block array
		return null;
	}
	
	public void SetWorldBlocks(int[][][] blocks) {
		// TODO: Convert the 3D block array to Chunks and save
	}
	
//	private void LoadChunk(int x, int z) throws FileNotFoundException {
//		File chunkFile = this.GetChunkFile(x, z);
//		
//		// ensure the file exists
//		if (!chunkFile.exists()) {
//			throw new FileNotFoundException("The chunk has failed to load. This is likely due to having uninitialized chunks.");
//		}
//		
//		// read in the chunk
//		Tag chunkTag = DTFReader.readDTFFile(chunkFile);
//		
//		// place the chunk
//		if (chunkTag != null) {
//			chunks[x - CHUNK_ARRAY_OFFSET_X][z - CHUNK_ARRAY_OFFSET_Z] = new Chunk(x, z, (CompoundTag)chunkTag, chunkFile.getAbsolutePath());
//		}
//	}
//	
//	private File GetChunkFile(int x, int z) {
//		// convert world coords to chunk coords
//		int x2 = x % 64;
//		int z2 = z % 64;
//		
//		// ensure x and z are not negative
//		if (x2 < 0)
//			x2 = x2 + 64;
//		
//		if (z2 < 0)
//			z2 = z2 + 64;
//		
//		// build the folder and file names
//		String firstFolder = Integer.toString(x2, 36);
//		String secondFolder = Integer.toString(z2, 36);
//		String filename = "c." + Integer.toString(x, 36) + "." + Integer.toString(z, 36) + ".dat";
//		
//		// open the file
//		File chunkFile = new File(this.basePath + this.worldName + "/" + firstFolder + "/" + secondFolder + "/" + filename);
//		
//		return chunkFile;
//	}
	
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
