package statesman.commands;

import java.util.Random;

public class PrintRandomCommand extends PrintCombineCommand {

    public static final String ID = "printr";

    private Random _random;

    public PrintRandomCommand() {
        super();
        _random = new Random();
    }

    public PrintRandomCommand(String[] messages) {
        this();
        _messages = messages;
    }

    @Override
    public Command createInstance(String[] arguments) {
        if (arguments.length > 2) {
            String[] messages = new String[arguments.length - 1];
            System.arraycopy(arguments, 1, messages, 0, arguments.length - 1);
            return new PrintRandomCommand(messages);
        }
        return null;
    }

    @Override
    public void execute() {
        initializeStrings();
        int i = _random.nextInt(_messages.length);
        System.out.printf(_messages[i] + "%n");
    }

}
