package statesman.actions;

import java.util.Random;
import statesman.Interpreter;
import statesman.Scene;

public class PrintRandomCommand implements Command {

    public static final String Id = "printr";

    private boolean _initialized;
    private String[] _messages;
    private Random _random;

    public PrintRandomCommand() {
        _initialized = false;
        _messages = null;
        _random = new Random();
    }

    public PrintRandomCommand(String[] messages) {
        this();
        _messages = messages;
    }

    @Override
    public Command createInstance(Scene parent, String[] arguments) {
        if (arguments.length > 2) {
            String[] messages = new String[arguments.length - 1];
            System.arraycopy(arguments, 1, messages, 0, arguments.length - 1);
            return new PrintRandomCommand(messages);
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

        int i = _random.nextInt(_messages.length);
        System.out.printf(_messages[i] + "%n");
    }

}
