package statesman.commands;

import statesman.Interpreter;

public class InventoryJumpCommand extends JumpCommand {

    public static final String Id = "ijmp";

    private String _itemName;

    public InventoryJumpCommand() {
        super();
        _itemName = "";
    }

    public InventoryJumpCommand(String itemName, int lineIfTrue, int lineIfFalse) {
        _itemName = itemName;
        _lineIfTrue = lineIfTrue;
        _lineIfFalse = lineIfFalse;
    }

    @Override
    public Command createInstance(String[] arguments) {
        if (arguments.length == 4) {
            String itemName = arguments[1];
            int lineIfTrue = getLineNumberFromString(arguments[2]);
            int lineIfFalse = getLineNumberFromString(arguments[3]);
            return new InventoryJumpCommand(itemName, lineIfTrue, lineIfFalse);
        }
        return null;
    }

    @Override
    public void execute() {
    }

    public int getJumpIndex() {
        if (Interpreter.getInventory().containsKey(_itemName)) {
            return _lineIfTrue;
        }
        return _lineIfFalse;
    }

}
