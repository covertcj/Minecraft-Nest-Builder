package mcnest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

//import nbt.*;
import org.jnbt.*;

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
			e.printStackTrace();
			System.exit(1);
		}
		
		// find the offset to help find a mapping between our array and minecraft coordinates
		CHUNK_ARRAY_OFFSET_X = playerChunkX - CHUNK_OFFSET_MAX;
		CHUNK_ARRAY_OFFSET_Z = playerChunkZ - CHUNK_OFFSET_MAX;
		
		// load in all of the necessary chunks
		LoadWorld();
	}
	
	public byte[][][] GetWorldBlocks() {
		int size = 16 * NUM_CHUNKS;
		byte[][][] blocks = new byte[size][128][size];
		
		for (int chunkX = 0; chunkX < NUM_CHUNKS; chunkX++) {
			for (int chunkZ = 0; chunkZ < NUM_CHUNKS; chunkZ++) {
				for (int x = 0; x < 16; x++) {
					for (int y = 0; y < 128; y++) {
						for (int z = 0; z < 16; z++) {
							blocks[chunkX*16 + x][y][chunkZ*16 + z] = chunks[chunkX][chunkZ].getBlock(x, y, z);
						}
					}
				}
			}
		}
		
		return blocks;
	}

	public void SetWorldBlocks(byte[][][] blocks) {
		// TODO: Convert the 3D block array to Chunks and save
		for (int chunkX = 0; chunkX < NUM_CHUNKS; chunkX++) {
			for (int chunkZ = 0; chunkZ < NUM_CHUNKS; chunkZ++) {
				for (int x = 0; x < 16; x++) {
					for (int y = 0; y < 128; y++) {
						for (int z = 0; z < 16; z++) {
							 chunks[chunkX][chunkZ].setBlock(x, y, z, blocks[chunkX*16 + x][y][chunkZ*16 + z]);
						}
					}
				}
				
				try {
					chunks[chunkX][chunkZ].Save();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void LoadPlayerInformation() throws Exception {
		// find the player's position
		File levelFile = new File(basePath + worldName + "/level.dat");
		FileInputStream fin = new FileInputStream(levelFile);
		NBTInputStream nbtin = new NBTInputStream(fin);
		
//		CompoundTag worldData = (CompoundTag)DTFReader.readDTFFile(levelFile);
//		CompoundTag worldDataData = (CompoundTag)worldData.getTagWithName("Data");
//		CompoundTag worldPlayerData = (CompoundTag)worldDataData.getTagWithName("Player");
//		
//		if (worldPlayerData != null) {
//			ListTag playerPos = (ListTag) worldPlayerData.getTagWithName("Pos");
//			this.playerX = (int)((DoubleTag) playerPos.value.get(0)).value;
//			this.playerY = (int)((DoubleTag) playerPos.value.get(1)).value;
//			this.playerZ = (int)((DoubleTag) playerPos.value.get(2)).value;
//			
//			// TODO: Why is this negative in the other person's code?
//			this.playerChunkX = -this.playerX / 16;
//			this.playerChunkZ = -this.playerZ / 16;
//		}
//		else {
//			throw new Exception("The player tag does not exist.");
//		}
		
		// read in the compound tag
		CompoundTag levelData = (CompoundTag) nbtin.readTag();
		CompoundTag levelDataData = (CompoundTag) levelData.getValue().get("Data");
		CompoundTag levelPlayerData = (CompoundTag) levelDataData.getValue().get("Player");
		
		ListTag playerPos = (ListTag) levelPlayerData.getValue().get("Pos");
		this.playerX = ((DoubleTag) playerPos.getValue().get(0)).getValue().intValue();
		this.playerY = ((DoubleTag) playerPos.getValue().get(1)).getValue().intValue();
		this.playerZ = ((DoubleTag) playerPos.getValue().get(2)).getValue().intValue();
		this.playerChunkX = -this.playerX / 16;
		this.playerChunkZ = -this.playerZ / 16;
				
		nbtin.close();
		fin.close();
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
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
