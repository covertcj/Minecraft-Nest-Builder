package mcnest;

public class MCNestBuilder
{
	public static void main(String[] args) {
		MCNestBuilder program = new MCNestBuilder(args);
	}
	
	MCNestBuilder(String[] args) {
		String basePath = "C:/Users/covertcj/AppData/Roaming/.minecraft/saves/";
		String worldName = "World1";
		MinecraftWorld world = new MinecraftWorld(basePath, worldName);
	}
}