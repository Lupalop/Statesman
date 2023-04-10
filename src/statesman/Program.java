package statesman;

import java.util.Scanner;

public class Program {

    public static final boolean debugMode = true;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Interpreter.setScanner(scanner);

        String location = "./scripts";
        if (args.length == 1) {
            location = args[0];
        }

        initialize(location);
    }

    public static void initialize(String scriptLocation,
            String overrideInitialScene) {
        if (Interpreter.isRunning()) {
            Interpreter.stop();
        }
        Content.setDataPath(scriptLocation);
        if (Content.tryLoadData()) {
            if (overrideInitialScene == null) {
                Interpreter.run();
            } else {
                Interpreter.run(overrideInitialScene);
            }
        } else {
            System.out.println(
                    "An error occurred while loading the game scripts.");
        }
    }

    public static void initialize(String scriptLocation) {
        initialize(scriptLocation, null);
    }

}
