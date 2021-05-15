package statesman;
import java.util.*;

public class App {
	
	public static Content Content = null;
	public static boolean debugMode = true;
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Content = new Content();
		Interpreter.setScanner(scanner);
		
		Content.setDataPath("./data.txt");
		if (Content.tryLoadData()) {
			Interpreter.setScenes(Content.parseScenesSource());
			Interpreter.startParser();
		} else {
			System.out.println("Data file is missing!");
		}
	}

}
