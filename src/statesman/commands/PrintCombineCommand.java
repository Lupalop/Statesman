package statesman.commands;

import statesman.Content;

public class PrintCombineCommand implements Command {

    public static final String ID = "printc";

    protected boolean _initialized;
    protected String[] _messages;

    public PrintCombineCommand() {
        _initialized = false;
        _messages = null;
    }

    public PrintCombineCommand(String[] messages) {
        this();
        _messages = messages;
    }

    @Override
    public Command createInstance(String[] arguments) {
        if (arguments.length > 2) {
            String[] messages = new String[arguments.length - 1];
            System.arraycopy(arguments, 1, messages, 0, arguments.length - 1);
            return new PrintCombineCommand(messages);
        }
        return null;
    }

    @Override
    public void execute() {
        initializeStrings();
        for (int i = 0; i < _messages.length; i++) {
            System.out.printf(_messages[i] + "%n");
        }
    }

    protected void initializeStrings() {
        if (!_initialized) {
            for (int i = 0; i < _messages.length; i++) {
                boolean unescapeString = _messages[i].startsWith("@");
                _messages[i] = Content.getScript().getMessages().getOrDefault(_messages[i], _messages[i]);
                if (unescapeString) {
                    _messages[i] = _messages[i].replace("\\e", "\033");
                }
            }
            _initialized = true;
        }
    }
    
}
