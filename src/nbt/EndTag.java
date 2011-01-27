package nbt;

public class EndTag extends Tag {
	public EndTag() {
		
	}
	public String toString(int tab) {
		return tab(tab) + "TAG_End()\n";
	}
}
