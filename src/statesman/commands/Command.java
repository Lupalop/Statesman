package statesman.commands;

import java.util.HashMap;

public abstract class Command {

	private static HashMap<String, Command> _commands;

    static {
        _commands = new HashMap<String, Command>();
        // XXX: manually input IDs of new commands here!
        _commands.put(PrintCommand.ID, new PrintCommand());
        _commands.put(PrintRandomCommand.ID, new PrintRandomCommand());
        _commands.put(PrintCombineCommand.ID, new PrintCombineCommand());
        _commands.put(SceneCommand.ID, new SceneCommand());
        _commands.put(GotoCommand.ID, new GotoCommand());
        _commands.put(JumpCommand.ID, new JumpCommand());
        _commands.put(ReturnCommand.ID, new ReturnCommand());
        _commands.put(SwitchSetCommand.ID, new SwitchSetCommand());
        _commands.put(SwitchJumpCommand.ID, new SwitchJumpCommand());
        _commands.put(SwitchConditionalCommand.ID, new SwitchConditionalCommand());
        _commands.put(InventoryCommand.ID, new InventoryCommand());
        _commands.put(InventoryJumpCommand.ID, new InventoryJumpCommand());
        _commands.put(InventoryConditionalCommand.ID, new InventoryConditionalCommand());
        _commands.put(PointsCommand.ID, new PointsCommand());
        _commands.put(QuitCommand.ID, new QuitCommand());
        _commands.put(SaveCommand.ID, new SaveCommand());
        _commands.put(LoadCommand.ID, new LoadCommand());
    }

    public static HashMap<String, Command> getCommands() {
        return _commands;
    }

    public static Command findCommand(String[] arguments) {
        String commandId = arguments[0].toLowerCase();
        Command command = getCommands().getOrDefault(commandId, null);
        if (command != null) {
            return command.createInstance(arguments);
        }
        return null;
    }

    public abstract Command createInstance(String[] arguments);

    public abstract void execute();

}
