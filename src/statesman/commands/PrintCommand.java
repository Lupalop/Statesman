package statesman.commands;

import java.util.Random;

import statesman.Content;

public class PrintCommand extends Command {

    public static final String ID_PRINT = "print";
    public static final String ID_PRINTC = "printc";
    public static final String ID_PRINTR = "printr";

    private enum PrintType {
        SINGLE, CONCATENATE, RANDOM
    }

    private boolean _initialized;
    private String[] _messages;

    private PrintType _printType;
    private Random _random;

    private PrintCommand() {
        _initialized = false;
        _messages = null;
        _printType = PrintType.SINGLE;
    }

    public PrintCommand(String[] messages, PrintType printType) {
        this();
        _messages = messages;
        _printType = printType;
        if (printType == PrintType.RANDOM) {
            _random = new Random();
        }
    }

    private static Command _defaultInstance;

    public static Command getDefault() {
        if (_defaultInstance == null) {
            _defaultInstance = new PrintCommand();
        }
        return _defaultInstance;
    }

    @Override
    public void execute() {
        initializeStrings();

        if (_printType == PrintType.CONCATENATE) {
            for (int i = 0; i < _messages.length; i++) {
                System.out.printf(_messages[i]);
            }
            return;
        }

        int i = 0;
        if (_printType == PrintType.RANDOM) {
            i = _random.nextInt(_messages.length);
        }
        System.out.printf(_messages[i] + "%n");
    }

    @Override
    public Command fromText(String commandId, String[] arguments) {
        if (arguments.length < 2) {
            return null;
        }

        String[] messages = new String[arguments.length - 1];
        System.arraycopy(arguments, 1, messages, 0, arguments.length - 1);

        PrintType printType;
        switch (commandId) {
        case ID_PRINT:
            printType = PrintType.SINGLE;
            break;
        case ID_PRINTC:
            printType = PrintType.CONCATENATE;
            break;
        case ID_PRINTR:
            printType = PrintType.RANDOM;
            break;
        default:
            return null;
        }

        return new PrintCommand(messages, printType);
    }

    private void initializeStrings() {
        if (_initialized) {
            return;
        }

        for (int i = 0; i < _messages.length; i++) {
            _messages[i] = Content.getScript().findMessage(
                    _messages[i], false);
        }
        _initialized = true;
    }

}
