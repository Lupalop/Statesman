package statesman.commands;

import statesman.Interpreter;
import statesman.Scene;

public class PrintCombineCommand implements Command {

    public static final String Id = "printc";

    private boolean _initialized;
    private String[] _messages;

    public PrintCombineCommand() {
        _initialized = false;
        _messages = null;
    }

    public PrintCombineCommand(String[] messages) {
        this();
        _messages = messages;
    }

    @Override
    public Command createInstance(Scene parent, String[] arguments) {
        if (arguments.length > 2) {
            String[] messages = new String[arguments.length - 1];
            System.arraycopy(arguments, 1, messages, 0, arguments.length - 1);
            return new PrintCombineCommand(messages);
        }
        return null;
    }

    @Override
    public Command createInstance(String[] arguments) {
        return createInstance(null, arguments);
    }

    @Override
    public void execute() {
        if (!_initialized) {
            for (int i = 0; i < _messages.length; i++) {
                _messages[i] = Interpreter.getSource().getMessages().getOrDefault(_messages[i], _messages[i]);
            }
            _initialized = true;
        }
        
        for (int i = 0; i < _messages.length; i++) {
            System.out.printf(_messages[i] + "%n");
        }
    }

}
