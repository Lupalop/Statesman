package statesman;

import java.util.*;

public class App {

    public static final boolean debugMode = true;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Interpreter.setScanner(scanner);

        Content.setDataPath("./data.txt");
        if (Content.tryLoadData()) {
            Interpreter.setSource(Content.getSource());
            Interpreter.run();
        } else {
            System.out.println("Data file is missing!");
        }
    }

}
