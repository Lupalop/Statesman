package statesman;

import java.util.*;

public class App {

    public static final boolean debugMode = true;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Interpreter.setScanner(scanner);
        
        String location = "./data.txt";
        if (args.length == 1) {
            location = args[0];
        }

        Content.setDataPath(location);
        if (Content.tryLoadData()) {
            Interpreter.run();
        } else {
            System.out.println("Unable to load the game's data file.");
        }
    }

}
