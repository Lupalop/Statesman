package statesman.actions;

import statesman.Interpreter;
import statesman.Scene;

public class GotoCommand implements Command {

    public static final String Id = "goto";

    private String _groupName;

    public GotoCommand() {
        _groupName = "";
    }

    public GotoCommand(String commandGroupName) {
        this();
        if (commandGroupName.isBlank()) {
            throw new IllegalArgumentException();
        }
        _groupName = commandGroupName;
    }

    @Override
    public Command createInstance(Scene parent, String[] arguments) {
        if (arguments.length == 2) {
            return new GotoCommand(arguments[1]);
        }
        return null;
    }

    @Override
    public Command createInstance(String[] arguments) {
        return createInstance(null, arguments);
    }

    @Override
    public void execute() {
        CommandGroup group = Interpreter.getSource().getCommandGroups().get(_groupName);
        // Local scene groups override the global group
        if (Interpreter.getCurrentScene() != null) {
            CommandGroup localGroup = Interpreter.getCurrentScene().getCommandGroups().get(_groupName);
            if (localGroup != null) {
                group = localGroup;
            }
        }
        
        if (group != null) {
            group.execute();
        }
    }

}
