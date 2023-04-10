package statesman.commands;

import statesman.Content;
import statesman.Interpreter;

public class CallCommand extends Command {

    public static final String ID_GOTO = "goto";
    public static final String ID_CALL = "call";

    private String _groupName;

    private CallCommand() {
        _groupName = "";
    }

    public CallCommand(String commandGroupName) {
        this();
        if (commandGroupName.isBlank()) {
            throw new IllegalArgumentException();
        }
        _groupName = commandGroupName;
    }

    private static Command _defaultInstance;

    public static Command getDefault() {
        if (_defaultInstance == null) {
            _defaultInstance = new CallCommand();
        }
        return _defaultInstance;
    }

    @Override
    public Command fromText(String commandId, String[] arguments) {
        if (!commandId.equalsIgnoreCase(ID_GOTO)
                && !commandId.equalsIgnoreCase(ID_CALL)) {
            return null;
        }
        if (arguments.length == 2) {
            return new CallCommand(arguments[1]);
        }
        return null;
    }

    @Override
    public void execute() {
        CommandGroup group = Content.getScript().getCommandGroups()
                .get(_groupName);
        // Local scene groups override the global group
        if (Interpreter.getScene() != null) {
            CommandGroup localGroup = Interpreter.getScene().getCommandGroups()
                    .get(_groupName);
            if (localGroup != null) {
                group = localGroup;
            }
        }

        if (group != null) {
            group.execute();
        }
    }

}
