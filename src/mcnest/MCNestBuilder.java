package mcnest;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class MCNestBuilder extends JFrame
{
	private JFileChooser fileChooser;
	private JTextField minecraftPathTextField;
	private JTextField worldTextField;
	private JButton chooseFileButton;
	private JButton runButton;
	
	public static void main(String[] args) {
		MCNestBuilder program = new MCNestBuilder(args);
		program.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		program.setSize(400, 300);
		program.setVisible(true);
	}
	
	public MCNestBuilder(String[] args) {
		// TODO: Add in a small GUI to allow selecting a world.
		super("Minecraft Nest Builder");
		GridLayout layout = new GridLayout(3, 3);
		layout.setVgap(5);
		layout.setHgap(5);
		setLayout(layout);
		MCNHandler handler = new MCNHandler();
		
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogTitle("Select your .minecraft/ directory...");
		fileChooser.addActionListener(handler);
		
		minecraftPathTextField = new JTextField("C:/Users/covertcj/AppData/Roaming/.minecraft");
		add(minecraftPathTextField);
		
		chooseFileButton = new JButton("Select Directory");
		chooseFileButton.addActionListener(handler);
		add(chooseFileButton);
		
		worldTextField = new JTextField("World1");
		add(worldTextField);
		
		runButton = new JButton("Run");
		runButton.addActionListener(handler);
		add(runButton);
	}
	
	private class MCNHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == runButton) {
				run();
			}
			else if (e.getSource() == chooseFileButton) {
				fileChooser.showOpenDialog(null);
			}
			else if (e.getSource() == fileChooser) {
				if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
					String filePath = ((JFileChooser)e.getSource()).getSelectedFile().getAbsolutePath();
					setMinecraftPath(filePath);
				}
			}
		}
		
		private void setMinecraftPath(String path) {
			minecraftPathTextField.setText(path);
		}
		
		private void run() {
			MinecraftWorld world = new MinecraftWorld(minecraftPathTextField.getText() + "/saves/", worldTextField.getText());
			byte [][][] blocks = world.GetWorldBlocks();
			
//			int ypos = world.getPlayerY();
//			
//			for (int chunkX = 0; chunkX < 7; chunkX++) {
//				for (int chunkZ = 0; chunkZ < 7; chunkZ++) {
//					for (int x = 0; x < 16; x++) {
//						for (int y = 0; y < 128; y++) {
//							for (int z = 0; z < 16; z++) {
//								//blocks[chunkX*16 + 8][ypos + 5][chunkZ*16 + 8] = (byte) ((chunkX + 1) + (chunkZ + 1));
//								if (y < ypos - 1)  {
//									blocks[chunkX*16 + x][y][chunkZ*16 + z] = 46;//(byte)(x+1);
//								} else if (y > ypos + 3) {
//									blocks[chunkX*16 + x][y][chunkZ*16 + z] = 46;
//								} else {
//									blocks[chunkX*16 + x][y][chunkZ*16 + z] = 0;
//								}
//							}
//						}
//					}
//				}
//			}
			
			world.SetWorldBlocks(blocks);
		}
		
	}
}