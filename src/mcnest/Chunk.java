package mcnest;

import nbt.Tag;

public class Chunk {

	public Chunk(Tag chunkTag) {
		// TODO Auto-generated constructor stub
	}
	
}

/*

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.jnbt.*;

public class Chunk {
	
	private Map<String, Tag> items;
	private Map<String, Tag> mapItems;
	
	private Blocks blocks;
	
	private String filename;
	
	public Chunk(String filename) throws IOException 
	{
		items = null;
		mapItems = null;
		blocks = null;
		this.filename = filename;
		
		Load();
	}
	
	private void Load() throws IOException 
	{
		NBTInputStream nbtin = new NBTInputStream(new FileInputStream(this.filename));
		
		// load in the root "Level" tag and convert it to a map
		CompoundTag root = (CompoundTag)(nbtin.readTag());
		items = root.getValue();
		
		// grab the map information
		CompoundTag map = (CompoundTag)(items.get("Map"));
		mapItems = map.getValue();
		
		// return the list of blocks
		blocks = new Blocks(((ByteArrayTag)(mapItems.get("Blocks"))).getValue());
		
		// clean up
		nbtin.close();
	}
	
	public void Save() throws IOException
	{
		// put the block array back into the map
		ByteArrayTag blocksTag = new ByteArrayTag("Blocks", blocks.getBlocks());
		mapItems.put("Blocks", blocksTag);
		
		// recombine the root "level" map
		CompoundTag mapItemsTag = new CompoundTag("Map", mapItems);
		items.put("Map", mapItemsTag);
		
		// compile the root level into a compound tag
		CompoundTag rootTag = new CompoundTag("MinecraftLevel", items);
		
		// write the file
		FileOutputStream out = new FileOutputStream(this.filename);
		NBTOutputStream nbtout = new NBTOutputStream(out);
		nbtout.writeTag(rootTag);
		
		// cleanup
		out.flush();
		out.close();
		nbtout.close();
	}
}

*/