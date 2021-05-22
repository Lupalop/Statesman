package statesman.commands;

import statesman.Interpreter;

public class SwitchJumpCommand extends JumpCommand {

    public static final String Id = "sjmp";

    private int _switchId;

    public SwitchJumpCommand() {
        super();
        _switchId = 0;
    }

    public SwitchJumpCommand(int switchId, int lineIfTrue, int lineIfFalse) {
        if (switchId < 0 || switchId > Interpreter.getSwitches().length) {
            throw new IllegalArgumentException();
        }
        _switchId = switchId;
        _lineIfTrue = lineIfTrue;
        _lineIfFalse = lineIfFalse;
    }

    @Override
    public Command createInstance(String[] arguments) {
        if (arguments.length == 4) {
            int switchId = Integer.parseInt(arguments[1]);
            int lineIfTrue = getLineNumberFromString(arguments[2]);
            int lineIfFalse = getLineNumberFromString(arguments[3]);
            return new SwitchJumpCommand(switchId, lineIfTrue, lineIfFalse);
        }
        return null;
    }

    @Override
    public void execute() {
    }

    public int getJumpIndex() {
        if (Interpreter.getSwitches()[_switchId]) {
            return _lineIfTrue;
        }
        return _lineIfFalse;
    }

}
