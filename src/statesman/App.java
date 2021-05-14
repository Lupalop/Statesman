package statesman;
import java.util.*;

public class App {
	
	public static ContentManager Content = null;
	public static GameManager Game = null;
	public static boolean debugMode = true;
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Content = new ContentManager();
		Game = new GameManager(scanner);
		
		Content.setDataPath("./data.txt");
		if (Content.tryLoadData()) {
			Content.parseData();
			Game.startParser();
		} else {
			System.out.println("Data file is missing!");
		}
	}

}
