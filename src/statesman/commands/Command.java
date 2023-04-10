package statesman.commands;

import java.util.HashMap;

import statesman.GameException;

public abstract class Command {

    private static HashMap<String, Command> _commands;

    public static HashMap<String, Command> getCommands() {
        if (_commands == null) {
            _commands = new HashMap<>();
            _commands.put(PrintCommand.ID_PRINT, PrintCommand.getDefault());
            _commands.put(PrintCommand.ID_PRINTC, PrintCommand.getDefault());
            _commands.put(PrintCommand.ID_PRINTR, PrintCommand.getDefault());
            _commands.put(SceneCommand.ID_SCENE, SceneCommand.getDefault());
            _commands.put(CallCommand.ID_CALL, CallCommand.getDefault());
            _commands.put(CallCommand.ID_GOTO, CallCommand.getDefault());
            _commands.put(CallCommand.ID_CALL_GLOB, CallCommand.getDefault());
            _commands.put(CallCommand.ID_SUPER, CallCommand.getDefault());
            _commands.put(JumpCommand.ID_JMP, JumpCommand.getDefault());
            _commands.put(JumpCommand.ID_IJMP, JumpCommand.getDefault());
            _commands.put(JumpCommand.ID_SJMP, JumpCommand.getDefault());
            _commands.put(JumpCommand.ID_RET, JumpCommand.getDefault());
            _commands.put(SwitchSetCommand.ID_SET,
                    SwitchSetCommand.getDefault());
            _commands.put(InventoryCommand.ID_INV,
                    InventoryCommand.getDefault());
            _commands.put(PointsCommand.ID_POINTS, PointsCommand.getDefault());
            _commands.put(MenuCommand.ID_SAVE, MenuCommand.getDefault());
            _commands.put(MenuCommand.ID_LOAD, MenuCommand.getDefault());
            _commands.put(MenuCommand.ID_QUIT, MenuCommand.getDefault());
        }
        return _commands;
    }

    public static Command find(String[] arguments) throws GameException {
        String commandId = arguments[0].toLowerCase();
        Command command = getCommands().get(commandId);
        if (command == null) {
            return null;
        }
        return command.fromText(commandId, arguments);
    }

    public abstract Command fromText(String id, String[] arguments)
            throws GameException;

    public abstract void execute();

}
