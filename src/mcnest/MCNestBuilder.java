package mcnest;

import java.io.FileNotFoundException;

public class MCNestBuilder
{
	public static void main(String[] args) {
		MCNestBuilder program = new MCNestBuilder(args);
	}
	
	MCNestBuilder(String[] args) {
		MinecraftWorld world = new MinecraftWorld("/home/covertcj/.minecraft/saves/", "World1");
		
		try {
			world.LoadChunk(0, 0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}