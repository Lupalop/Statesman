package statesman.commands;

import statesman.Content;

public class PrintCommand extends Command {

    public static final String ID = "print";

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
            boolean unescapeString = _message.startsWith("@");
            _message = Content.getScript().getMessages().getOrDefault(_message, _message);
            if (unescapeString) {
                _message = _message.replace("\\e", "\033");
            }
            _initialized = true;
        }
        
        System.out.printf(_message + "%n");
    }

    @Override
    public Command createInstance(String[] arguments) {
        if (arguments.length == 2) {
            return new PrintCommand(arguments[1]);
        }
        return null;
    }

}
