package mcnest;

import java.io.IOException;

public class MCNestBuilder
{
	public static void main(String[] args) {
		MCNestBuilder program = new MCNestBuilder(args);
	}
	
	MCNestBuilder(String[] args) {
		try {
			Chunk ch = new Chunk("C:/Users/covertcj/AppData/Roaming/.minecraft/saves/World3/0/0/c.0.0.dat");
			
			ch.Save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}