package statesman.commands;

import statesman.Interpreter;

public class ConditionalJumpCommand extends JumpCommand {

    public static final String Id = "cjmp";

    private int _lineIfTrue;
    private int _lineIfFalse;
    private int _switchId;

    public ConditionalJumpCommand() {
        super();
        _switchId = 0;
        _lineIfTrue = 0;
        _lineIfFalse = 0;
    }

    public ConditionalJumpCommand(int switchId, int lineIfTrue, int lineIfFalse) {
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
            int lineIfTrue = 0;
            if (arguments[2].equalsIgnoreCase("ret")) {
                lineIfTrue = Integer.MAX_VALUE;
            } else {
                lineIfTrue = Integer.parseInt(arguments[2]);
            }
            int lineIfFalse = 0;
            if (arguments[3].equalsIgnoreCase("ret")) {
                lineIfFalse = Integer.MAX_VALUE;
            } else {
                lineIfFalse = Integer.parseInt(arguments[3]);
            }
            return new ConditionalJumpCommand(switchId, lineIfTrue, lineIfFalse);
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
