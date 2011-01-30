package mcnest;

public class MCNestBuilder
{
	public static void main(String[] args) {
		MCNestBuilder program = new MCNestBuilder(args);
	}
	
	MCNestBuilder(String[] args) {
		MinecraftWorld world = new MinecraftWorld("C:/Users/covertcj/AppData/Roaming/.minecraft/saves/", "World1");
	}
}