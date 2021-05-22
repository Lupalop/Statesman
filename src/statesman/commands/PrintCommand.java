package statesman.commands;

import statesman.Interpreter;
import statesman.Scene;

public class PrintCommand implements Command {

    public static final String Id = "print";

    private boolean _initialized;
    private String _message;

    public PrintCommand() {
        _initialized = false;
        _message = null;
    }

    public PrintCommand(String message) {
        this();
        _message = message;
    }

    @Override
    public void execute() {
        if (!_initialized) {
            _message = Interpreter.getSource().getMessages().getOrDefault(_message, _message);
            _initialized = true;
        }
        
        System.out.printf(_message + "%n");
    }

    @Override
    public Command createInstance(Scene parent, String[] arguments) {
        if (arguments.length == 2) {
            return new PrintCommand(arguments[1]);
        }
        return null;
    }

    @Override
    public Command createInstance(String[] arguments) {
        return createInstance(null, arguments);
    }

}
