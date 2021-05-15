package statesman.actions;

import statesman.Content;
import statesman.Scene;

public class PrintCombineCommand extends Command {

    public static final String Id = "printc";

    private String[] _messages;

    public PrintCombineCommand() {
        _messages = null;
    }

    public PrintCombineCommand(String[] messages) {
        this();
        _messages = new String[messages.length];
        for (int i = 0; i < messages.length; i++) {
            _messages[i] = Content.getMessages().getOrDefault(messages[i], messages[i]);
        }
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
        for (int i = 0; i < _messages.length; i++) {
            System.out.printf(_messages[i] + "%n");
        }
    }

}
