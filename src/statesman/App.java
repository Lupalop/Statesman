package statesman;
import java.util.*;

public class App {
	
	public static ContentManager Content = null;
	public static Interpreter Interpreter = null;
	public static boolean debugMode = true;
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Content = new ContentManager();
		Interpreter = new Interpreter(scanner);
		
		Content.setDataPath("./data.txt");
		if (Content.tryLoadData()) {
			Content.parseData();
			Interpreter.startParser();
		} else {
			System.out.println("Data file is missing!");
		}
	}

}
