package statesman.actions;

import statesman.Content;
import statesman.Scene;

public class PrintCommand implements Command {

    public static final String Id = "print";

    private String _message;

    public PrintCommand() {
        _message = "";
    }

    public PrintCommand(String message) {
        _message = message;
    }

    public PrintCommand(String message, boolean dynamic) {
        this(Content.getMessages().getOrDefault(message, message));
    }

    @Override
    public void execute() {
        System.out.printf(_message + "%n");
    }

    @Override
    public Command createInstance(Scene parent, String[] arguments) {
        if (arguments.length == 2) {
            return new PrintCommand(arguments[1], true);
        }
        return null;
    }

    @Override
    public Command createInstance(String[] arguments) {
        return createInstance(null, arguments);
    }

}
